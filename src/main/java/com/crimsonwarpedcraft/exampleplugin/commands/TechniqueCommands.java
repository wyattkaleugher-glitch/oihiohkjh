package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import com.crimsonwarpedcraft.exampleplugin.techniques.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TechniqueCommands implements CommandExecutor {

    private final ExamplePlugin plugin;
    private final ProfileManager profileManager;

    public TechniqueCommands(ExamplePlugin plugin) {
        this.plugin = plugin;
        this.profileManager = plugin.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can execute cursed techniques!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /jjk <ability1|ability2|domain|setclass|reload>");
            return true;
        }

        PlayerProfile profile = profileManager.getProfile(player.getUniqueId());
        String action = args[0].toLowerCase();

        if (action.equals("reload")) {
            if (!player.hasPermission("jjk.admin")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            plugin.reloadConfig();
            player.sendMessage("§a[JJK] Configuration files reloaded successfully!");
            return true;
        }

        if (action.equals("setclass")) {
            if (args.length < 2) {
                player.sendMessage("§cSpecify a class!");
                return true;
            }
            try {
                TechniqueType type = TechniqueType.valueOf(args[1].toUpperCase());
                profile.setTechnique(type);
                player.sendMessage("§aYour Innate Technique has been set to: §e" + type.name());
            } catch (IllegalArgumentException e) {
                player.sendMessage("§cInvalid technique class!");
            }
            return true;
        }

        if (profile.isBurnedOut()) {
            player.sendMessage("§cYour brain is short-circuited from Domain Burnout!");
            return true;
        }

        // --- ENFORCE COOLDOWNS AUTOMATICALLY ---
        if (action.equals("ability1") || action.equals("ability2") || action.equals("domain")) {
            int remaining = profile.getRemainingCooldown(action);
            if (remaining > 0) {
                player.sendMessage("§cThis technique is on cooldown for another §e" + remaining + "§cs!");
                return true;
            }
        }

        switch (action) {
            case "ability1" -> {
                routeAbilityOne(player, profile);
                int cd = plugin.getConfig().getInt("techniques." + profile.getTechnique().name().toLowerCase() + "." + getAbilityKey(profile.getTechnique(), 1) + "-cooldown", plugin.getConfig().getInt("cooldowns.ability1", 5));
                profile.setCooldown("ability1", cd);
            }
            case "ability2" -> {
                routeAbilityTwo(player, profile);
                int cd = plugin.getConfig().getInt("techniques." + profile.getTechnique().name().toLowerCase() + "." + getAbilityKey(profile.getTechnique(), 2) + "-cooldown", plugin.getConfig().getInt("cooldowns.ability2", 12));
                profile.setCooldown("ability2", cd);
            }
            case "domain" -> {
                if (profile.getTechnique() == TechniqueType.NONE) return true;
                if (profile.getCursedEnergy() < plugin.getConfig().getInt("domain.ce-cost", 500)) {
                    player.sendMessage("§cYou lack the required Cursed Energy!");
                    return true;
                }
                DomainEngine.launchDomain(player, profile, plugin.getConfig());
                
                // Set Domain cooldown
                int cd = plugin.getConfig().getInt("domain.cooldown", 180);
                profile.setCooldown("domain", cd);

                // Start burnout timer logic task loop dynamically from config value
                int burnoutSecs = plugin.getConfig().getInt("cooldowns.burnout-duration", 20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    profile.setBurnedOut(false);
                    player.sendMessage("§aYour cursed technique has finished cycling! Burnout recovered.");
                }, burnoutSecs * 20L);
            }
            default -> player.sendMessage("§cUnknown action.");
        }

        return true;
    }

    private void routeAbilityOne(Player player, PlayerProfile profile) {
        switch (profile.getTechnique()) {
            case GOJO -> GojoKit.castBlue(player, profile, plugin.getConfig());
            case SUKUNA -> SukunaKit.castDismantle(player, profile, plugin.getConfig());
            case MEGUMI -> MegumiKit.castDivineDog(player, profile, plugin.getConfig());
            case YUJI -> YujiKit.toggleDivergentFist(player, profile, plugin.getConfig());
            case INUMAKI -> InumakiKit.castDontMove(player, profile, plugin.getConfig());
            case MAHITO -> MahitoKit.castSoulStrike(player, profile, plugin.getConfig());
            case NONE -> player.sendMessage("§cChoose a class first!");
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
            case NONE -> player.sendMessage("§cChoose a class first!");
        }
    }

    // Helper method to look up the exact config sub-key matching the character templates
    private String getAbilityKey(TechniqueType type, int slot) {
        if (slot == 1) {
            return switch (type) {
                case GOJO -> "blue";
                case SUKUNA -> "dismantle";
                case MEGUMI -> "dog";
                case YUJI -> "divergent";
                case INUMAKI -> "dontmove";
                case MAHITO -> "soulstrike";
                default -> "ability1";
            };
        } else {
            return switch (type) {
                case GOJO -> "red";
                case SUKUNA -> "cleave";
                case MEGUMI -> "mahoraga";
                case YUJI -> "manji";
                case INUMAKI -> "plunge";
                case MAHITO -> "isomer";
                default -> "ability2";
            };
        }
    }
}
