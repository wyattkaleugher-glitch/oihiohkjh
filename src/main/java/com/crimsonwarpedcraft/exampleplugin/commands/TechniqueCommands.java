package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import com.crimsonwarpedcraft.exampleplugin.techniques.DomainEngine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TechniqueCommands implements TabExecutor {

    private final ExamplePlugin plugin;

    public TechniqueCommands(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "info" -> {
                player.sendMessage("§b§m--- §e§lJUJUTSU PROFILE §b§m---");
                player.sendMessage("§7Grade: §d" + profile.getGrade());
                player.sendMessage("§7Technique: §a" + profile.getTechnique().name());
                player.sendMessage("§7Energy: §b" + profile.getCursedEnergy() + "/" + profile.getMaxCursedEnergy());
            }

            case "withdraw" -> {
                if (args.length < 2) return true;
                if (args[1].equalsIgnoreCase("ce")) {
                    if (args.length < 3) return true;
                    try {
                        int amt = Integer.parseInt(args[2]);
                        if (profile.getCursedEnergy() >= amt) {
                            profile.setCursedEnergy(profile.getCursedEnergy() - amt);
                            player.getInventory().addItem(createCEItem(amt));
                            player.sendMessage("§aWithdrew §e" + amt + " CE §ainto a crystal.");
                        }
                    } catch (NumberFormatException e) { player.sendMessage("§cInvalid amount."); }
                } else if (args[1].equalsIgnoreCase("class")) {
                    if (profile.getTechnique() != TechniqueType.NONE) {
                        TechniqueType t = profile.getTechnique();
                        profile.setTechnique(TechniqueType.NONE);
                        player.getInventory().addItem(createClassItem(t));
                        player.sendMessage("§aStored your §d" + t.name() + " §atechnique in a scroll.");
                    }
                }
            }

            case "ability" -> {
                player.sendMessage("§aActivating Cursed Technique...");
                // Add your ability trigger logic here
            }

            case "domain" -> {
                DomainEngine.launchDomain(player, profile);
            }

            case "givespear" -> {
                if (!player.hasPermission("jjk.admin")) return true;
                ItemStack spear = new ItemStack(Material.NETHERITE_SWORD);
                ItemMeta m = spear.getItemMeta();
                m.setDisplayName("§f§lInverted Spear of Heaven");
                m.setLore(List.of("§7Sharpness VI", "§7Nullifies all Techniques on hit."));
                m.addEnchant(Enchantment.SHARPNESS, 6, true);
                spear.setItemMeta(m);
                player.getInventory().addItem(spear);
                player.sendMessage("§aInverted Spear granted.");
            }

            case "giveclass" -> {
                if (!player.hasPermission("jjk.admin")) return true;
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /jjk giveclass <player> <technique>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { player.sendMessage("§cPlayer not found."); return true; }
                try {
                    TechniqueType tech = TechniqueType.valueOf(args[2].toUpperCase());
                    target.getInventory().addItem(createClassItem(tech));
                    player.sendMessage("§aGave " + tech.name() + " scroll to " + target.getName());
                } catch (Exception e) { player.sendMessage("§cInvalid technique type."); }
            }

            case "clearcooldown" -> {
                if (!player.hasPermission("jjk.admin")) return true;
                profile.clearAllCooldowns();
                player.sendMessage("§aAll cooldowns cleared.");
            }

            default -> sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage("§6§lJJK Commands:");
        p.sendMessage("§e/jjk info §7- View profile");
        p.sendMessage("§e/jjk withdraw <ce|class> [amt] §7- Store power");
        p.sendMessage("§e/jjk ability §7- Use technique");
        p.sendMessage("§e/jjk domain §7- Expand domain");
        if (p.hasPermission("jjk.admin")) {
            p.sendMessage("§c/jjk giveclass <player> <tech>");
            p.sendMessage("§c/jjk givespear §7- Get ISOH");
            p.sendMessage("§c/jjk clearcooldown §7- Reset timers");
        }
    }

    private ItemStack createCEItem(int amt) {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName("§d§lCrystallized Cursed Energy");
        m.setLore(List.of("§7Contains: §b" + amt + " CE", "§eRight-click to absorb."));
        m.getPersistentDataContainer().set(new NamespacedKey(plugin, "dropped_ce_amount"), PersistentDataType.INTEGER, amt);
        item.setItemMeta(m);
        return item;
    }

    private ItemStack createClassItem(TechniqueType tech) {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName("§6§lTechnique Scroll: §d" + tech.name());
        m.setLore(List.of("§7Mastery of §f" + tech.name(), "§eRight-click to learn."));
        m.getPersistentDataContainer().set(new NamespacedKey(plugin, "technique_scroll"), PersistentDataType.STRING, tech.name());
        item.setItemMeta(m);
        return item;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String a, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("info", "withdraw", "ability", "domain", "givespear", "giveclass", "clearcooldown");
        if (args.length == 2 && args[0].equalsIgnoreCase("withdraw")) return Arrays.asList("ce", "class");
        if (args.length == 3 && args[0].equalsIgnoreCase("giveclass")) {
            List<String> techs = new ArrayList<>();
            for (TechniqueType t : TechniqueType.values()) techs.add(t.name());
            return techs;
        }
        return new ArrayList<>();
    }
}
