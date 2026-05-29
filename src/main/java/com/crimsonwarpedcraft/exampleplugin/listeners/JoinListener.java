package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final ExamplePlugin plugin;

    public JoinListener(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerProfile profile = plugin.getProfileManager().getProfile(event.getPlayer().getUniqueId());
        profile.applyGradeBuffs(); // Re-apply Strength/Speed on login
    }


        if (!player.hasPlayedBefore()) {
            TechniqueType[] types = TechniqueType.values();
            TechniqueType selected = types[new Random().nextInt(types.length)];
            while (selected == TechniqueType.NONE) selected = types[new Random().nextInt(types.length)];
            
            profile.setTechnique(selected);
            profile.setGrade("Grade 4");
            // SAVE IMMEDIATELY
            plugin.getProfileManager().saveProfile(player.getUniqueId());
            player.sendMessage("§eAwakened: §f" + selected.name());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getProfileManager().unloadProfile(event.getPlayer().getUniqueId());
    }
}
