package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DomainEngine {
    public static void launchDomain(Player player, PlayerProfile profile) {
        ExamplePlugin plugin = (ExamplePlugin) JavaPlugin.getProvidingPlugin(ExamplePlugin.class);
        Location center = player.getLocation();
        int r = 10;

        plugin.getDomainManager().registerDomain(player.getUniqueId(), center);

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    double d = Math.sqrt(x*x + y*y + z*z);
                    if (d >= r - 0.8 && d <= r + 0.5) {
                        Block b = center.clone().add(x, y, z).getBlock();
                        b.setType(d > r ? Material.CRYING_OBSIDIAN : Material.TINTED_GLASS);
                    }
                }
            }
        }

        Bukkit.getScheduler().runTaskTimer(plugin, (task) -> {
            if (!player.isOnline() || !plugin.getDomainManager().hasActiveDomain(player.getUniqueId())) {
                task.cancel();
                return;
            }
            profile.reduceAllCooldowns(3);
        }, 0L, 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getDomainManager().unregisterDomain(player.getUniqueId());
        }, 400L);
    }
}
