package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class YujiKit {
    public static void execute(Player p, PlayerProfile prof, int slot) {
        ExamplePlugin plugin = JavaPlugin.getPlugin(ExamplePlugin.class);
        if (slot == 1) { // Divergent Fist
            double dmg = plugin.getConfig().getDouble("kits.yuji.divergent-damage", 5.0);
            p.sendMessage("§f§lDivergent Fist");
            for (Entity e : p.getNearbyEntities(4, 4, 4)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(dmg, p);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        target.damage(dmg * 1.5, p);
                    }, 10L);
                }
            }
            prof.setCooldown("ability1", 6);
        } else if (slot == 2) { // Black Flash
            double dmg = plugin.getConfig().getDouble("kits.yuji.black-flash-damage", 20.0);
            p.sendMessage("§0§lBLACK FLASH!");
            for (Entity e : p.getNearbyEntities(5, 5, 5)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(dmg, p);
                    target.getWorld().spawnParticle(Particle.WITCH, target.getLocation(), 100);
                    p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
                }
            }
            prof.setCooldown("ability2", 45);
        }
    }
}
