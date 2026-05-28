package com.crimsonwarpedcraft.exampleplugin.api;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CEActionBarTask extends BukkitRunnable {

    private final ProfileManager profileManager;

    public CEActionBarTask(ProfileManager manager) {
        this.profileManager = manager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = profileManager.getProfile(player.getUniqueId());
            
            if (profile == null) continue;

            int currentCE = profile.getCursedEnergy();
            int maxCE = profile.getMaxCursedEnergy();
            TechniqueType type = profile.getTechnique();

            // 1. Generate a dynamic visual energy progress bar segment (e.g., : ||||||||||)
            int barTotalSegments = 10;
            // Protect against division by zero if maxCE is configured to 0
            double percent = (maxCE <= 0) ? 0 : (double) currentCE / maxCE;
            int activeSegments = (int) Math.round(percent * barTotalSegments);
            
            StringBuilder barBuilder = new StringBuilder();
            for (int i = 0; i < barTotalSegments; i++) {
                if (i < activeSegments) {
                    barBuilder.append("┃"); // Filled segment
                } else {
                    barBuilder.append("︱"); // Empty segment
                }
            }

            // 2. Structural text formatting conditional checks
            String styleName = (type == TechniqueType.NONE) ? "UNAWAKENED" : type.name();
            
            Component finalMessage;
            
            // If the player is suffering from Domain Burnout, turn the action bar red to warn them!
            if (profile.isBurnedOut()) {
                finalMessage = Component.text("☠ BURNOUT ☠  ", NamedTextColor.RED)
                    .append(Component.text("[" + styleName + "] ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(currentCE + "/" + maxCE + " CE", NamedTextColor.GRAY));
            } else {
                // Regular active state bar
                finalMessage = Component.text(barBuilder.toString() + " ", NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(currentCE + "/" + maxCE + " CE ", NamedTextColor.AQUA))
                    .append(Component.text("[" + styleName + "]", NamedTextColor.GOLD));
            }

            // Send the finalized component payload straight to the player's actionbar slot
            player.sendActionBar(finalMessage);
        }
    }
}
