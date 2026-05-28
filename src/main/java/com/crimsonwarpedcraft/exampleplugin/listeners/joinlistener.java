package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
        
        // Verifies if the Minecraft engine registers them as completely new
        if (!player.hasPlayedBefore()) {
            PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            
            // Collect all valid Techniques excluding NONE
            TechniqueType[] templates = TechniqueType.values();
            TechniqueType selectedType = TechniqueType.NONE;
            
            // Keep pulling until we get a real character trait slot
            while (selectedType == TechniqueType.NONE) {
                selectedType = templates[random.nextInt(templates.length)];
            }
            
            // Set values completely inside profile memory
            profile.setTechnique(selectedType);
            profile.setMaxCursedEnergy(1000);
            profile.setCursedEnergy(1000);
            
            // Broadcast the awakening celebration notice string
            player.sendMessage("§b§m---------------------------------------------");
            player.sendMessage("§e§l   SOUL AWAKENING DETECTED!");
            player.sendMessage("§7 You have been born with the Innate Technique: §d§l" + selectedType.name());
            player.sendMessage("§b§m---------------------------------------------");
        }
    }
}
