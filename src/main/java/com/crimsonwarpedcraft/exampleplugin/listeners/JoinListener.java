package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;

public class JoinListener implements Listener {

    private final ExamplePlugin plugin;

    public JoinListener(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        // Apply physical buffs based on saved Grade (Strength/Speed/etc)
        profile.applyGradeBuffs();

        // First join logic: Assign random technique and save soul data
        if (!player.hasPlayedBefore()) {
            TechniqueType[] types = TechniqueType.values();
            Random random = new Random();
            
            TechniqueType selected = types[random.nextInt(types.length)];
            while (selected == TechniqueType.NONE) {
                selected = types[random.nextInt(types.length)];
            }
            
            profile.setTechnique(selected);
            profile.setGrade("Grade 4");
            
            // Save immediately so the technique is locked to the player's file
            plugin.getProfileManager().saveProfile(player.getUniqueId());
            
            player.sendMessage("§b§m---------------------------------------------");
            player.sendMessage("§e§lAWAKENING: §7You have inherited the §d" + selected.name() + " §7technique!");
            player.sendMessage("§b§m---------------------------------------------");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Save and unload data when the player leaves to prevent memory leaks
        plugin.getProfileManager().unloadProfile(event.getPlayer().getUniqueId());
    }
}
