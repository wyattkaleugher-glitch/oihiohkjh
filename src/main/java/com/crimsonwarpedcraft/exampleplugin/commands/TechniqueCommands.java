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

        // 🛠️ ADMIN: RELOAD CONFIG
        if (action.equals("reload")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cYou do not have permission to execute administrative overrides!");
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage("§a[JJK] Configuration parameters successfully refreshed!");
            return true;
        }

        // 🛠️ ADMIN: GIVE CURSED ENERGY DIRECTLY
        if (action.equals("givece")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cYou do not have permission to execute administrative overrides!");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /jjk givece <player> <amount>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cTarget player is currently offline.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                PlayerProfile targetProfile = profileManager.getProfile(target.getUniqueId());
                targetProfile.setCursedEnergy(targetProfile.getCursedEnergy() + amount);
                sender.sendMessage("§aGave §e" + amount + " CE §ato §f" + target.getName());
                target.sendMessage("§aAn administrator has gifted you §e" + amount + " CE§a!");
            } catch (NumberFormatException e) {
                sender.sendMessage("§cAmount must be a valid whole number!");
            }
            return true;
        }

        // 🛠️ ADMIN: SET MAX CAPACITY LIMIT
        if (action.equals("setmaxce")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cYou do not have permission to execute administrative overrides!");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /jjk setmaxce <player> <amount>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cTarget player is currently offline.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                int absoluteCap = plugin.getConfig().getInt("adminsection.max-ce-cap-limit", 5000);
                
                if (amount > absoluteCap) {
                    sender.sendMessage("§cCannot exceed the absolute safety cap of §e" + absoluteCap + " CE §cconfigured in adminsection!");
                    return true;
                }

                PlayerProfile targetProfile = profileManager.getProfile(target.getUniqueId());
                targetProfile.setMaxCursedEnergy(amount);
                sender.sendMessage("§aSet §f" + target.getName() + "'s §amax CE capacity limit to §b" + amount);
                target.sendMessage("§aYour maximum Cursed Energy capacity was updated to §b" + amount + " CE§a!");
            } catch (NumberFormatException e) {
                sender.sendMessage("§cAmount must be a valid whole number!");
            }
            return true;
        }

        // 🛠️ ADMIN: GIVE PHYSICAL CE ITEM SHARD
        if (action.equals("giveitem")) {
            if (!sender.hasPermission("jjk.admin")) {
                sender.sendMessage("§cYou do not have permission to execute administrative overrides!");
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
            sender.sendMessage("§cOnly players can execute physical cursed techniques!");
            return true;
        }

        PlayerProfile profile = profileManager.getProfile(player.getUniqueId());

        // 🌀 PLAYER COMMAND: SET INNATE JUTSU CLASS
        if (action.equals("setclass")) {
            if (args.length < 2) {
                player.sendMessage("§cSpecify a class name! (Gojo, Sukuna, Megumi, Yuji, Inumaki, Mahito)");
                return true;
            }
            try {
                TechniqueType type = TechniqueType.valueOf(args[1].toUpperCase());
                profile.setTechnique(type);
                player.sendMessage("§aYour Innate Soul Technique has scaled to: §e" + type.name());
            } catch (IllegalArgumentException e) {
                player.sendMessage("§cInvalid character archetype template selection!");
            }
            return true;
        }

        if (profile.isBurnedOut()) {
            player.sendMessage("§cYour neural structures are fried from Domain Burnout!");
            return true;
        }

        boolean bypassCooldowns = plugin.getConfig().getBoolean("adminsection.bypass-cooldowns", false);
        if ((action.equals("ability1") || action.equals("ability2") || action.equals("domain")) && !bypassCooldowns) {
            int remaining = profile.getRemainingCooldown(action);
            if (remaining > 0) {
                player.sendMessage("§cTechnique locked on cooldown for another §e" + remaining + "§cs!");
                return true;
            }
        }

        switch (action) {
            case "ability1" -> {
                if (profile.getTechnique() == TechniqueType.NONE) { 
                    player.sendMessage("§cSelect a technique via /jjk setclass"); 
                    return true; 
                }
                routeAbilityOne(player, profile);
                int cd = plugin.getConfig().getInt("sections." + profile.getTechnique().name().toLowerCase() + "." + getAbilityKey(profile.getTechnique(), 1) + "-cooldown", plugin.getConfig().getInt("cooldowns.ability1", 5));
                profile.setCooldown("ability1", cd);
            }
            case "ability2" -> {
                if (profile.getTechnique() == TechniqueType.NONE) { 
                    player.sendMessage("§cSelect a technique via /jjk setclass"); 
                    return true; 
                }
                routeAbilityTwo(player, profile);
                int cd = plugin.getConfig().getInt("sections." + profile.getTechnique().name().toLowerCase() + "." + getAbilityKey(profile.getTechnique(), 2) + "-cooldown", plugin.getConfig().getInt("cooldowns.ability2", 12));
                profile.setCooldown("ability2", cd);
            }
            case "domain" -> {
                if (profile.getTechnique() == TechniqueType.NONE) return true;
                if (profile.getCursedEnergy() < plugin.getConfig().getInt("domain.ce-cost", 500)) {
                    player.sendMessage("§cInsufficent Cursed Energy pools to construct a domain boundary!");
                    return true;
                }
                
                DomainEngine.launchDomain(player, profile, plugin.getConfig());
                
                int cd = plugin.getConfig().getInt("domain.cooldown", 180);
                profile.setCooldown("domain", cd);

                int burnoutSecs = plugin.getConfig().getInt("cooldowns.burnout-duration", 20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    profile.setBurnedOut(false);
                    player.sendMessage("§aYour brain's technique structures have processed burnout! Restored.");
                }, burnoutSecs * 20L);
            }
            default -> player.sendMessage("§cUnknown subcommand parameter loop.");
        }
        return true;
    }

    // 🌟 THIS HANDLES IN-GAME TAB COMPLETION AUTOMATICALLY
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        // Argument 1: /jjk [subcommand]
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>(Arrays.asList("ability1", "ability2", "domain", "setclass"));
            if (sender.hasPermission("jjk.admin")) {
                subcommands.addAll(Arrays.asList("reload", "givece", "setmaxce", "giveitem"));
            }
            // Auto-filter suggestions based on what the user has typed so far
            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        // Argument 2: depends completely on what the first subcommand was
        if (args.length == 2) {
            String firstArg = args[0].toLowerCase();

            // Case A: User typed "/jjk setclass [archetype]"
            if (firstArg.equals("setclass")) {
                for (TechniqueType type : TechniqueType.values()) {
                    if (type != TechniqueType.NONE && type.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(type.name().toLowerCase());
                    }
                }
                return completions;
            }

            // Case B: Admin typed "/jjk givece/setmaxce/giveitem [player]"
            if (firstArg.equals("givece") || firstArg.equals("setmaxce") || firstArg.equals("giveitem")) {
                if (sender.hasPermission("jjk.admin")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(onlinePlayer.getName());
                        }
                    }
                    return completions;
                }
            }
        }

        // Argument 3: Dynamic hints for values (e.g. amounts)
        if (args.length == 3) {
            String firstArg = args[0].toLowerCase();
            if (sender.hasPermission("jjk.admin") && (firstArg.equals("givece") || firstArg.equals("setmaxce") || firstArg.equals("giveitem"))) {
                completions.add("[amount]");
                return completions;
            }
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
