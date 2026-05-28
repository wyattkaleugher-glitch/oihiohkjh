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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TechniqueCommands implements CommandExecutor {

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
