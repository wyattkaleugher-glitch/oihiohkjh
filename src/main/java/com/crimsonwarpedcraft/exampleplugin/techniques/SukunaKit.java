package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SukunaKit {
    public static void execute(Player p, PlayerProfile prof, int slot) {
        ExamplePlugin plugin = JavaPlugin.getPlugin(ExamplePlugin.class);
        
        if (slot == 1) { // Cleave
            // Fallback to 10.0 if config fails
            double dmg = plugin.getConfig().getDouble("kits.sukuna.cleave-damage", 10.0);
            p.sendMessage("§7§lCleave");
            
            for (Entity e : p.getNearbyEntities(5, 5, 5)) { // Range increased to 5
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(dmg, p); 
                    target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 5);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.5f);
                }
            }
            prof.setCooldown("ability1", 5);
        } else if (slot == 2) { // Dismantle
            double dmg = plugin.getConfig().getDouble("kits.sukuna.dismantle-damage", 7.0);
            p.sendMessage("§7§lDismantle");
            
            p.getNearbyEntities(8, 3, 8).forEach(e -> {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(dmg, p);
                    target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 10);
                }
            });
            prof.setCooldown("ability2", 8);
        }
    }
}
