package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class YujiKit {
    public static void toggleDivergentFist(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.yuji.divergent-ce-cost", 15);
        double firstDamage = config.getDouble("techniques.yuji.divergent-first-damage", 4.0);
        double secondDamage = config.getDouble("techniques.yuji.divergent-second-damage", 8.0);

        var rayTrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 3.5);
        if (rayTrace == null || !(rayTrace.getHitEntity() instanceof LivingEntity target)) {
            player.sendMessage("§cYou must be within striking range to land Divergent Fist!");
            return;
        }

        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§e§lDivergent Fist!");

        target.damage(firstDamage, player);
        target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0,1,0), 10, 0.2, 0.2, 0.2);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 0.8F);

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
            if (target.isValid()) {
                target.damage(secondDamage, player); 
                Location loc = target.getLocation().add(0,1,0);
                loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 5, 0.1, 0.1, 0.1);
                loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0F, 1.4F);
            }
        }, 8L);
    }

    public static void castManjiKick(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.yuji.manji-ce-cost", 30);
        double damage = config.getDouble("techniques.yuji.manji-damage", 9.0);

        if (profile.getCursedEnergy() < ceCost) {
            player.sendMessage("§cNot enough Cursed Energy (" + ceCost + " required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§6§lManji Kick!");

        Vector launch = player.getLocation().getDirection().normalize().multiply(1.8).setY(0.2);
        player.setVelocity(launch);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0F, 0.5F);

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 15, 0.5, 0.2, 0.5);
            for (Entity entity : player.getNearbyEntities(2.0, 2.0, 2.0)) {
                if (entity instanceof LivingEntity target && !entity.equals(player)) {
                    target.damage(damage, player);
                    target.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(0.4));
                }
            }
        }, 5L);
    }
}
