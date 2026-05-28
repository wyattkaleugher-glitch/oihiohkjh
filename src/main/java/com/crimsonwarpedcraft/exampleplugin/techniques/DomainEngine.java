package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DomainEngine {
    public static void launchDomain(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("domain.ce-cost", 500);
        int radius = config.getInt("domain.radius", 8);
        double damage = config.getDouble("domain.surehit-damage", 16.0);

        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        profile.setBurnedOut(true);

        player.sendMessage("§0§lDomain Expansion!!");
        Location center = player.getLocation();

        // Physically build the boundary out of solid Black Concrete
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance >= radius - 0.8 && distance <= radius + 0.5) {
                        center.clone().add(x, y, z).getBlock().setType(Material.BLACK_CONCRETE);
                    }
                }
            }
        }

        center.getWorld().spawnParticle(Particle.FLASH, center, 5, 2.0, 2.0, 2.0);
        center.getWorld().playSound(center, Sound.BLOCK_PORTAL_TRAVEL, 1.5F, 0.5F);

        // Execute Sure-Hit payload calculations
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity target && !entity.equals(player)) {
                target.damage(damage, player);
                target.setFireTicks(100);
                target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation().add(0,1,0), 20);
            }
        }
    }
}
