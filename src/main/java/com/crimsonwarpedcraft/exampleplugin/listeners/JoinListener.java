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

        // Apply physical buffs based on saved Grade
        profile.applyGradeBuffs();

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
