package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;

public class DomainEngine {

    public static void launchDomain(Player player, PlayerProfile profile) {
        ExamplePlugin plugin = (ExamplePlugin) JavaPlugin.getProvidingPlugin(ExamplePlugin.class);
        Location center = player.getLocation();
        int radius = 10;

        plugin.getDomainManager().registerDomain(player.getUniqueId(), center);

        // Visuals
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x*x + y*y + z*z);
                    if (dist >= radius - 0.8 && dist <= radius + 0.5) {
                        Block b = center.clone().add(x, y, z).getBlock();
                        b.setType(dist > radius ? Material.CRYING_OBSIDIAN : Material.TINTED_GLASS);
                        
                        b.getChunk().getPersistentDataContainer().set(
                            new NamespacedKey(plugin, "domain_block_" + b.getX() + "_" + b.getY() + "_" + b.getZ()), 
                            PersistentDataType.BYTE, (byte) 1
                        );
                    }
                }
            }
        }

        // 🚀 Sure-Hit Buff: Tick cooldowns 4x faster
        Bukkit.getScheduler().runTaskTimer(plugin, (task) -> {
            // Check if player still owns a domain. If location is null, domain ended.
            if (!player.isOnline() || plugin.getDomainManager().getDomainLocation(player.getUniqueId()) == null) {
                task.cancel();
                return;
            }
            profile.reduceAllCooldowns(3); 
        }, 0L, 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getDomainManager().unregisterDomain(player.getUniqueId());
            player.sendMessage("§cYour domain has collapsed.");
        }, 400L);
    }
}
