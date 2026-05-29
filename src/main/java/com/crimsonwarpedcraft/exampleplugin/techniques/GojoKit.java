package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GojoKit {
    public static void execute(Player p, PlayerProfile prof, int slot) {
        ExamplePlugin plugin = JavaPlugin.getPlugin(ExamplePlugin.class);
        if (slot == 1) { // Blue
            double dmg = plugin.getConfig().getDouble("kits.gojo.blue-damage", 4.0);
            p.sendMessage("§b§lBlue");
            for (Entity e : p.getNearbyEntities(7, 7, 7)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    Vector pull = p.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.5);
                    target.setVelocity(pull);
                    target.damage(dmg, p);
                    target.getWorld().spawnParticle(Particle.DUST, target.getLocation(), 20, new Particle.DustOptions(Color.BLUE, 1));
                }
            }
            prof.setCooldown("ability1", 10);
        } else if (slot == 2) { // Red
            double dmg = plugin.getConfig().getDouble("kits.gojo.red-damage", 8.0);
            p.sendMessage("§c§lRed");
            for (Entity e : p.getNearbyEntities(5, 5, 5)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    Vector push = target.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(2);
                    target.setVelocity(push);
                    target.damage(dmg, p);
                }
            }
            prof.setCooldown("ability2", 20);
        }
    }
}
