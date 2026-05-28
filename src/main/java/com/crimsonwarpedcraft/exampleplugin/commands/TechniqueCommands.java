package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import com.crimsonwarpedcraft.exampleplugin.techniques.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
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
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /jjk <ability1|ability2|domain|setclass|reload|givece|setmaxce|giveitem>");
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("reload")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cNo permission!");
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage("§a[JJK] Configurations refreshed!");
            return true;
        }

        if (action.equals("givece")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /jjk givece <player> <amount>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer is offline.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                PlayerProfile targetProfile = profileManager.getProfile(target.getUniqueId());
                targetProfile.setCursedEnergy(targetProfile.getCursedEnergy() + amount);
                sender.sendMessage("§aGave §e" + amount + " CE §ato §f" + target.getName());
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount!");
            }
            return true;
        }

        if (action.equals("setmaxce")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /jjk setmaxce <player> <amount>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer is offline.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                int absoluteCap = plugin.getConfig().getInt("adminsection.max-ce-cap-limit", 5000);
                if (amount > absoluteCap) {
                    sender.sendMessage("§cCannot exceed cap limit of " + absoluteCap);
                    return true;
                }
                PlayerProfile targetProfile = profileManager.getProfile(target.getUniqueId());
                targetProfile.setMaxCursedEnergy(amount);
                sender.sendMessage("§aSet " + target.getName() + "'s max capacity to " + amount);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount!");
            }
            return true;
        }

        if (action.equals("giveitem")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /jjk giveitem <player> <amount>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cTarget player is offline.");
                return true;
            }
            try {
                int ceAmount = Integer.parseInt(args[2]);
                
                ItemStack crystal = new ItemStack(Material.AMETHYST_SHARD);
                ItemMeta meta = crystal.getItemMeta();
                
                if (meta != null) {
                    meta.setDisplayName("§d§lCrystallized Cursed Energy");
                    meta.setLore(List.of("§7Right-Click to absorb.", "", "§fContains: §b" + ceAmount + " CE"));
                    
                    NamespacedKey ceKey = new NamespacedKey(plugin, "dropped_ce_amount");
                    meta.getPersistentDataContainer().set(ceKey, PersistentDataType.INTEGER, ceAmount);
                    crystal.setItemMeta(meta);
                }
                
                target.getInventory().addItem(crystal);
                sender.sendMessage("§aGenerated a physical item containing §e" + ceAmount + " CE §afor " + target.getName());
                
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid integer format.");
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can execute abilities!");
            return true;
        }

        PlayerProfile profile = profileManager.getProfile(player.getUniqueId());

        if (action.equals("setclass")) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /jjk setclass <class>");
                return true;
            }
            try {
                TechniqueType type = TechniqueType.valueOf(args[1].toUpperCase());
                profile.setTechnique(type);
                
                profile.setMaxCursedEnergy(1000);
                profile.setCursedEnergy(1000);
                
                player.sendMessage("§aYour Innate Soul Technique has transformed into: §e" + type.name());
                player.sendMessage("§bCursed Energy maxed out to 1000/1000 for testing!");
            } catch (IllegalArgumentException e) {
                player.sendMessage("§cInvalid character archetype selection.");
            }
            return true;
        }

        // BURNOUT VERIFICATION PASS COMPONENT REMOVED FROM THIS ROUTINE STACK AREA

        boolean bypassCooldowns = plugin.getConfig().getBoolean("adminsection.bypass-cooldowns", false);
        if ((action.equals("ability1") || action.equals("ability2") || action.equals("domain")) && !bypassCooldowns) {
            int remaining = profile.getRemainingCooldown(action);
            if (remaining > 0) {
                player.sendMessage("§cLocked on cooldown for another §e" + remaining + "§cs!");
                return true;
            }
        }

        switch (action) {
            case "ability1" -> {
                if (profile.getTechnique() == TechniqueType.NONE) { player.sendMessage("§cChoose a class first!"); return true; }
                routeAbilityOne(player, profile);
                int cd = plugin.getConfig().getInt("sections." + profile.getTechnique().name().toLowerCase() + "." + getAbilityKey(profile.getTechnique(), 1) + "-cooldown", plugin.getConfig().getInt("cooldowns.ability1", 5));
                profile.setCooldown("ability1", cd);
            }
            case "ability2" -> {
                if (profile.getTechnique() == TechniqueType.NONE) { player.sendMessage("§cChoose a class first!"); return true; }
                routeAbilityTwo(player, profile);
                int cd = plugin.getConfig().getInt("sections." + profile.getTechnique().name().toLowerCase() + "." + getAbilityKey(profile.getTechnique(), 2) + "-cooldown", plugin.getConfig().getInt("cooldowns.ability2", 12));
                profile.setCooldown("ability2", cd);
            }
            case "domain" -> {
                if (profile.getTechnique() == TechniqueType.NONE) return true;
                int domainCost = plugin.getConfig().getInt("domain.ce-cost", 500);
                
                if (profile.getCursedEnergy() < domainCost) {
                    player.sendMessage("§cInsufficent energy to form a domain! (Requires " + domainCost + " CE. You have: " + profile.getCursedEnergy() + ")");
                    return true;
                }
                
                DomainEngine.launchDomain(player, profile, plugin.getConfig());
                
                int cd = plugin.getConfig().getInt("domain.cooldown", 180);
                profile.setCooldown("domain", cd);
                
                // DATA RECOVERY DELAY RUNNABLE PIPELINE REMOVED TO PREVENT BURNOUT SYSTEM ACTIVATION LOCKS
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(Arrays.asList("ability1", "ability2", "domain", "setclass"));
            if (sender.hasPermission("jjk.admin")) subs.addAll(Arrays.asList("reload", "givece", "setmaxce", "giveitem"));
            for (String s : subs) if (s.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(s);
            return completions;
        }
        if (args.length == 2) {
            String first = args[0].toLowerCase();
            if (first.equals("setclass")) {
                for (TechniqueType type : TechniqueType.values()) {
                    if (type != TechniqueType.NONE && type.name().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(type.name().toLowerCase());
                }
            } else if (first.equals("givece") || first.equals("setmaxce") || first.equals("giveitem")) {
                if (sender.hasPermission("jjk.admin")) {
                    for (Player p : Bukkit.getOnlinePlayers()) if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(p.getName());
                }
            }
        }
        if (args.length == 3 && sender.hasPermission("jjk.admin")) {
            String first = args[0].toLowerCase();
            if (first.equals("givece") || first.equals("setmaxce") || first.equals("giveitem")) completions.add("[amount]");
        }
        return completions;
    }

    private void routeAbilityOne(Player player, PlayerProfile profile) {
        switch (profile.getTechnique()) {
            case GOJO -> GojoKit.castBlue(player, profile, plugin.getConfig());
            case SUKUNA -> SukunaKit.castDismantle(player, profile, plugin.getConfig());
            case MEGUMI -> MegumiKit.castDivineDog(player, profile, plugin.getConfig());
            case YUJI -> YujiKit.toggleDivergentFist(player, profile, plugin.getConfig());
            case INUMAKI -> InumakiKit.castDontMove(player, profile, plugin.getConfig());
            case MAHITO -> MahitoKit.castSoulStrike(player, profile, plugin.getConfig());
        }
    }

    private void routeAbilityTwo(Player player, PlayerProfile profile) {
        switch (profile.getTechnique()) {
            case GOJO -> GojoKit.castRed(player, profile, plugin.getConfig());
            case SUKUNA -> SukunaKit.castCleave(player, profile, plugin.getConfig());
            case MEGUMI -> MegumiKit.castMahoraga(player, profile, plugin.getConfig());
            case YUJI -> YujiKit.castManjiKick(player, profile, plugin.getConfig());
            case INUMAKI -> InumakiKit.castPlunge(player, profile, plugin.getConfig());
            case MAHITO -> MahitoKit.castPolymorphicIsomer(player, profile, plugin.getConfig());
        }
    }

    private String getAbilityKey(TechniqueType type, int slot) {
        if (slot == 1) {
            return switch (type) {
                case GOJO -> "blue"; case SUKUNA -> "dismantle"; case MEGUMI -> "dog";
                case YUJI -> "divergent"; case INUMAKI -> "dontmove"; case MAHITO -> "soulstrike";
                default -> "ability1";
            };
        } else {
            return switch (type) {
                case GOJO -> "red"; case SUKUNA -> "cleave"; case MEGUMI -> "mahoraga";
                case YUJI -> "manji"; case INUMAKI -> "plunge"; case MAHITO -> "isomer";
                default -> "ability2";
            };
        }
    }
}
