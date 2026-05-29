package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DomainEngine {
    public static void launchDomain(Player p, PlayerProfile prof) {
        ExamplePlugin plugin = (ExamplePlugin) JavaPlugin.getPlugin(ExamplePlugin.class);
        Location center = p.getLocation();
        Material shell = Material.TINTED_GLASS;

        // MATCHING THE THEMES
        switch (prof.getTechnique()) {
            case GOJO -> shell = Material.BLACK_STAINED_GLASS;
            case SUKUNA -> shell = Material.RED_STAINED_GLASS;
            case MAHITO -> shell = Material.WHITE_STAINED_GLASS;
            case MEGUMI -> shell = Material.GRAY_STAINED_GLASS;
            case YUJI -> shell = Material.YELLOW_STAINED_GLASS;
            case INUMAKI -> shell = Material.LIGHT_GRAY_STAINED_GLASS;
            default -> shell = Material.GLASS;
        }

        int r = 8;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (Math.abs(Math.sqrt(x*x + y*y + z*z) - r) < 0.5) {
                        center.clone().add(x, y, z).getBlock().setType(shell);
                    }
                }
            }
        }
        
        p.sendMessage("§d§lDOMAIN EXPANSION!");
        plugin.getDomainManager().registerDomain(p.getUniqueId(), center);
    }
}
