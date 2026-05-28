package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class InumakiKit {
    public static void castDontMove(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.inumaki.dontmove-ce-cost", 40);
        double recoil = config.getDouble("techniques.inumaki.dontmove-recoil", 2.0);

        if (profile.getCursedEnergy() < ceCost) {
            player.sendMessage("§cNot enough Cursed Energy (" + ceCost + " required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§7§l[Cursed Speech] §f§l\"Don't Move!\"");

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1.5F, 0.5F);
        player.damage(recoil); 

        for (Entity entity : player.getNearbyEntities(8.0, 4.0, 8.0)) {
            if (entity instanceof LivingEntity target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 10, false, true));
                target.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, target.getLocation().add(0,2,0), 3);
            }
        }
    }

    public static void castPlunge(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.inumaki.plunge-ce-cost", 50);
        double recoil = config.getDouble("techniques.inumaki.plunge-recoil", 3.0);
        double damage = config.getDouble("techniques.inumaki.plunge-damage", 10.0);

        if (profile.getCursedEnergy() < ceCost) {
            player.sendMessage("§cNot enough Cursed Energy (" + ceCost + " required)!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§7§l[Cursed Speech] §8§l\"Plunge!\"");

        player.damage(recoil); 

        for (Entity entity : player.getNearbyEntities(10.0, 5.0, 10.0)) {
            if (entity instanceof LivingEntity target) {
                target.setVelocity(new Vector(0, -2.5, 0));
                target.damage(damage, player);
                
                Location loc = target.getLocation();
                loc.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, loc, 30, 0.5, 0.1, 0.5, loc.getBlock().getBlockData());
                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_BREAK, 1.0F, 0.6F);
            }
        }
    }
}
