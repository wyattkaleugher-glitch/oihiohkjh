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
            double dmg = plugin.getConfig().getDouble("kits.sukuna.cleave-damage", 10.0);
            p.sendMessage("§7§lCleave");
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.5f);
            for (Entity e : p.getNearbyEntities(3, 3, 3)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(dmg, p);
                    target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 2);
                }
            }
            prof.setCooldown("ability1", 5);
        } else if (slot == 2) { // Dismantle
            double dmg = plugin.getConfig().getDouble("kits.sukuna.dismantle-damage", 6.0);
            p.sendMessage("§7§lDismantle");
            for (Entity e : p.getNearbyEntities(6, 2, 6)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(dmg, p);
                    target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 5);
                }
            }
            prof.setCooldown("ability2", 8);
        }
    }
}
