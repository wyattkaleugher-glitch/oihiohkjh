package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GojoKit {
    public static void castBlue(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.gojo.blue-ce-cost", 25);
        double damage = config.getDouble("techniques.gojo.blue-damage", 5.0);
        double range = config.getDouble("techniques.gojo.blue-range", 12.0);

        if (profile.getCursedEnergy() < ceCost) {
            player.sendMessage("§cNot enough Cursed Energy (" + ceCost + " required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§9§lLapse Blue!");

        Location targetPoint = player.getTargetBlockExact((int)range) != null ? 
            player.getTargetBlockExact((int)range).getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(6));

        targetPoint.getWorld().spawnParticle(Particle.DUST, targetPoint, 30, 1.0, 1.0, 1.0, new Particle.DustOptions(Color.BLUE, 2.0F));
        targetPoint.getWorld().playSound(targetPoint, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.5F);

        for (Entity entity : targetPoint.getWorld().getNearbyEntities(targetPoint, 6.0, 6.0, 6.0)) {
            if (entity instanceof LivingEntity target && !entity.equals(player)) {
                Vector pullDirection = targetPoint.toVector().subtract(target.getLocation().toVector());
                target.setVelocity(pullDirection.normalize().multiply(1.2).setY(0.4));
                target.damage(damage, player); 
            }
        }
    }

    public static void castRed(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.gojo.red-ce-cost", 50);
        double damage = config.getDouble("techniques.gojo.red-damage", 12.0);
        double knockback = config.getDouble("techniques.gojo.red-knockback", 2.5);

        if (profile.getCursedEnergy() < ceCost) {
            player.sendMessage("§cNot enough Cursed Energy (" + ceCost + " required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§c§lReversal Red!");

        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();

        for (int i = 1; i <= 15; i++) {
            Location point = eyeLoc.clone().add(direction.clone().multiply(i));
            point.getWorld().spawnParticle(Particle.DUST, point, 5, 0.1, 0.1, 0.1, new Particle.DustOptions(Color.RED, 1.5F));

            for (Entity entity : point.getWorld().getNearbyEntities(point, 1.5, 1.5, 1.5)) {
                if (entity instanceof LivingEntity target && !entity.equals(player)) {
                    target.setVelocity(direction.clone().multiply(knockback).setY(0.8));
                    target.damage(damage, player); 
                    point.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, point, 1);
                    point.getWorld().playSound(point, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
                    return;
                }
            }
        }
    }
}
