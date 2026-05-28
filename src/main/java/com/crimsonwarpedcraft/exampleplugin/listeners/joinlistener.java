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
    private final Random random;

    public JoinListener(ExamplePlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onFirstJoinAssignment(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (!player.hasPlayedBefore()) {
            PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            
            TechniqueType[] templates = TechniqueType.values();
            TechniqueType selectedType = TechniqueType.NONE;
            
            while (selectedType == TechniqueType.NONE) {
                selectedType = templates[random.nextInt(templates.length)];
            }
            
            profile.setTechnique(selectedType);
            profile.setMaxCursedEnergy(1000);
            profile.setCursedEnergy(1000);
            profile.setGrade("Grade 4"); // Sets starting default tier
            
            plugin.getProfileManager().saveProfile(player.getUniqueId());

            player.sendMessage("§b§m---------------------------------------------");
            player.sendMessage("§e§l   SOUL AWAKENING DETECTED!");
            player.sendMessage("§7 You have been born with the Innate Technique: §d§l" + selectedType.name());
            player.sendMessage("§b§m---------------------------------------------");
        }
    }

    // 🔒 GUARD RAIL: Saves data to the disk storage file whenever a player logs off
    @EventHandler
    public void onPlayerLeaveSave(PlayerQuitEvent event) {
        plugin.getProfileManager().unloadProfile(event.getPlayer().getUniqueId());
    }
}
