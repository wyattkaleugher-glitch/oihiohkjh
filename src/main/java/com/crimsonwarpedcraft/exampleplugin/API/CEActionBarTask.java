package com.crimsonwarpedcraft.exampleplugin.api;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CEActionBarTask extends BukkitRunnable {

    private final ProfileManager profileManager;

    // Inject the ProfileManager so this loop can read active player values
    public CEActionBarTask(ProfileManager manager) {
        this.profileManager = manager;
    }

    @Override
    public void run() {
        // Loop through every single player currently connected to the server
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = profileManager.getProfile(player.getUniqueId());
            
            if (profile == null) continue;

            // Gather structural variables from memory
            int currentCE = profile.getCursedEnergy();
            int maxCE = profile.getMaxCursedEnergy();

            // Format your custom display layout text string
            String hudDisplay = "|||||||||| " + currentCE + " / " + maxCE + " CE";

            // Send the text component to the action bar spot using modern Adventure API components
            player.sendActionBar(Component.text(hudDisplay, NamedTextColor.AQUA));
        }
    }
}
