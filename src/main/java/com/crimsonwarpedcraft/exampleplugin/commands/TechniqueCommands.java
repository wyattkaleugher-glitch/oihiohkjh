package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TechniqueCommands implements TabExecutor {

    private final ExamplePlugin plugin;
    private final ProfileManager profileManager;

    public TechniqueCommands(ExamplePlugin plugin) {
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cConsole cannot use JJK commands.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        PlayerProfile profile = profileManager.getProfile(player.getUniqueId());
        String action = args[0].toLowerCase();

        switch (action) {
            case "info" -> {
                player.sendMessage("§b§m=====================================");
                player.sendMessage("§e§l  JUJUTSU SORCERER PROFILE");
                player.sendMessage(" §7• Grade: §d§l" + profile.getGrade());
                player.sendMessage(" §7• Technique: §a" + (profile.getTechnique() == TechniqueType.NONE ? "None" : profile.getTechnique().name()));
                player.sendMessage(" §7• Energy: §b" + profile.getCursedEnergy() + "§7/§3" + profile.getMaxCursedEnergy() + " CE");
                player.sendMessage("§b§m=====================================");
            }

            case "withdraw" -> {
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /jjk withdraw <ce|class> [amount]");
                    return true;
                }
                handleWithdraw(player, profile, args);
            }

            case "setgrade" -> {
                if (!player.hasPermission("jjk.admin")) return noPerm(player);
                if (args.length < 3) { player.sendMessage("§cUsage: /jjk setgrade <player> <grade>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) return true;
                
                String newGrade = args[2].replace("_", " ");
                PlayerProfile targetProf = profileManager.getProfile(target.getUniqueId());
                targetProf.setGrade(newGrade);
                profileManager.saveProfile(target.getUniqueId());
                player.sendMessage("§aSet " + target.getName() + " to " + newGrade);
            }

            case "giveclass" -> {
                if (!player.hasPermission("jjk.admin")) return noPerm(player);
                if (args.length < 3) { player.sendMessage("§cUsage: /jjk giveclass <player> <type>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) return true;
                try {
                    TechniqueType tech = TechniqueType.valueOf(args[2].toUpperCase());
                    target.getInventory().addItem(createClassItem(tech));
                    player.sendMessage("§aGave " + tech.name() + " scroll to " + target.getName());
                } catch (Exception e) { player.sendMessage("§cInvalid technique."); }
            }

            case "givegradeitem" -> {
                if (!player.hasPermission("jjk.admin")) return noPerm(player);
                Player target = args.length > 1 ? Bukkit.getPlayer(args[1]) : player;
                if (target == null) return true;
                target.getInventory().addItem(createGradeItem());
                player.sendMessage("§aGave Promotion Recommendation to " + target.getName());
            }

            default -> sendHelp(player);
        }

        profileManager.saveProfile(player.getUniqueId());
        return true;
    }

    private void handleWithdraw(Player player, PlayerProfile profile, String[] args) {
        String type = args[1].toLowerCase();

        if (type.equals("ce")) {
            if (args.length < 3) { player.sendMessage("§cUsage: /jjk withdraw ce <amount>"); return true; }
            try {
                int amount = Integer.parseInt(args[2]);
                if (amount <= 0 || profile.getCursedEnergy() < amount) {
                    player.sendMessage("§cInsufficient Cursed Energy!");
                    return;
                }
                profile.setCursedEnergy(profile.getCursedEnergy() - amount);
                player.getInventory().addItem(createCEItem(amount));
                player.sendMessage("§aWithdrew §e" + amount + " CE §ainto a crystal.");
            } catch (Exception e) { player.sendMessage("§cInvalid amount."); }
        } 
        
        else if (type.equals("class")) {
            if (profile.getTechnique() == TechniqueType.NONE) {
                player.sendMessage("§cYou have no technique to withdraw!");
                return;
            }
            TechniqueType current = profile.getTechnique();
            profile.setTechnique(TechniqueType.NONE);
            player.getInventory().addItem(createClassItem(current));
            player.sendMessage("§aWithdrew your §d" + current.name() + " §atechnique into a scroll.");
        }
    }

    private ItemStack createCEItem(int amount) {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d§lCrystallized Cursed Energy");
        meta.setLore(List.of("§7Contains: §b" + amount + " CE", "§eRight-click to absorb."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "dropped_ce_amount"), PersistentDataType.INTEGER, amount);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createClassItem(TechniqueType tech) {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lTechnique Scroll: §d" + tech.name());
        meta.setLore(List.of("§7Mastery of §f" + tech.name(), "", "§eRight-click to learn."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "technique_scroll"), PersistentDataType.STRING, tech.name());
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createGradeItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lJujutsu Promotion Recommendation");
        meta.setLore(List.of("§7Official Sorcerer promotion.", "", "§eRight-click to rank up."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "grade_up_token"), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    private void sendHelp(Player p) {
        p.sendMessage("§6§lJJK Commands:");
        p.sendMessage("§e/jjk info §7- View your status");
        p.sendMessage("§e/jjk withdraw <ce|class> §7- Store power in items");
        if (p.hasPermission("jjk.admin")) {
            p.sendMessage("§c/jjk setgrade <player> <grade>");
            p.sendMessage("§c/jjk giveclass <player> <type>");
            p.sendMessage("§c/jjk givegradeitem <player>");
        }
    }

    private boolean noPerm(Player p) {
        p.sendMessage("§cYou do not have permission for admin JJK commands.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("info", "withdraw", "setgrade", "giveclass", "givegradeitem");
        if (args.length == 2 && args[0].equalsIgnoreCase("withdraw")) return Arrays.asList("ce", "class");
        if (args.length == 3 && args[0].equalsIgnoreCase("giveclass")) {
            List<String> types = new ArrayList<>();
            for (TechniqueType t : TechniqueType.values()) if (t != TechniqueType.NONE) types.add(t.name());
            return types;
        }
        return null;
    }
}
