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
        
        // Only triggers if the player has never connected to this server before
        if (!player.hasPlayedBefore()) {
            PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            
            // Randomly select an Innate Technique (Excluding NONE)
            TechniqueType[] templates = TechniqueType.values();
            TechniqueType selectedType = TechniqueType.NONE;
            
            while (selectedType == TechniqueType.NONE) {
                selectedType = templates[random.nextInt(templates.length)];
            }
            
            // Apply the "First Join" starter package
            profile.setTechnique(selectedType);
            profile.setGrade("Grade 4");
            profile.setMaxCursedEnergy(1000);
            profile.setCursedEnergy(1000);
            
            // CRITICAL: Force the ProfileManager to write this to the .yml file immediately
            // This ensures the technique stays even if the server crashes or restarts
            plugin.getProfileManager().saveProfile(player.getUniqueId());

            // Awakening Message
            player.sendMessage("§b§m---------------------------------------------");
            player.sendMessage("§e§l   SOUL AWAKENING DETECTED!");
            player.sendMessage("§7 You have been born with the Innate Technique: §d§l" + selectedType.name());
            player.sendMessage("§7 Your current rank: §fGrade 4");
            player.sendMessage("§b§m---------------------------------------------");
        }
    }

    /**
     * Ensures all player data (CE, Grade, Technique) is saved to the disk
     * the moment a player leaves the server.
     */
    @EventHandler
    public void onPlayerLeaveSave(PlayerQuitEvent event) {
        plugin.getProfileManager().unloadProfile(event.getPlayer().getUniqueId());
    }
}
