package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.techniques.AbilityEngine;
import com.crimsonwarpedcraft.exampleplugin.techniques.DomainEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TechniqueCommands implements CommandExecutor {
    private final ExamplePlugin plugin;

    public TechniqueCommands(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length == 0) return false;

        PlayerProfile prof = plugin.getProfileManager().getProfile(p.getUniqueId());

        // Using your sub-command structure
        if (args[0].equalsIgnoreCase("ability1")) {
            AbilityEngine.handleAbility(p, prof, 1);
            return true;
        } 
        
        if (args[0].equalsIgnoreCase("ability2")) {
            AbilityEngine.handleAbility(p, prof, 2);
            return true;
        }

        if (args[0].equalsIgnoreCase("domain")) {
            if (plugin.getDomainManager().hasActiveDomain(p.getUniqueId())) {
                p.sendMessage("§cYou already have a domain active!");
            } else {
                DomainEngine.launchDomain(p, prof);
            }
            return true;
        }

        // Logic for giving the class/technique
        if (args[0].equalsIgnoreCase("giveclass") && p.hasPermission("jjk.admin")) {
            if (args.length < 3) return false;
            // Your existing logic for giving a player a technique scroll or setting profile
            return true;
        }

        return true;
    }
}
