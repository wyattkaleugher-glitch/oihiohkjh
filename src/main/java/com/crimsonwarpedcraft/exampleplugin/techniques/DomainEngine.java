package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;

public class DomainEngine {
    public static void launchDomain(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("domain.ce-cost", 500);
        int radius = config.getInt("domain.radius", 8);
        double damage = config.getDouble("domain.surehit-damage", 16.0);

        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        profile.setBurnedOut(true);

        player.sendMessage("§0§lDomain Expansion!!");
        Location center = player.getLocation();
        
        // Dynamic lookup to get our plugin instance for NamespacedKeys
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(DomainEngine.class);
        NamespacedKey barrierKey = new NamespacedKey(plugin, "domain_barrier");

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance >= radius - 0.8 && distance <= radius + 0.5) {
                        Block block = center.clone().add(x, y, z).getBlock();
                        block.setType(Material.BLACK_CONCRETE);
                        
                        // SECURE DATA STAMP: Marks this specific concrete block as an unbreakable barrier
                        block.getChunk().getPersistentDataContainer().set(
                            new NamespacedKey(plugin, "domain_block_" + block.getX() + "_" + block.getY() + "_" + block.getZ()), 
                            PersistentDataType.BYTE, 
                            (byte) 1
                        );
                    }
                }
            }
        }

        center.getWorld().spawnParticle(Particle.FLASH, center, 5, 2.0, 2.0, 2.0);
        center.getWorld().playSound(center, Sound.BLOCK_PORTAL_TRAVEL, 1.5F, 0.5F);

        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity target && !entity.equals(player)) {
                target.damage(damage, player);
                target.setFireTicks(100);
                target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation().add(0,1,0), 20);
            }
        }
    }
}
