package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MahitoKit {
    public static void execute(Player p, PlayerProfile prof, int slot) {
        ExamplePlugin plugin = JavaPlugin.getPlugin(ExamplePlugin.class);
        
        if (slot == 1) { // Idle Transfiguration
            p.sendMessage("§d§lIdle Transfiguration");
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 3));
            p.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 1f, 1.5f);
            prof.setCooldown("ability1", 15);
        } else if (slot == 2) { // Soul Repel
            double dmg = plugin.getConfig().getDouble("kits.mahito.repel-damage", 8.0);
            p.sendMessage("§5§lSoul Repel!");
            for (Entity e : p.getNearbyEntities(5, 5, 5)) {
                if (e instanceof LivingEntity target && !e.equals(p)) {
                    target.damage(dmg, p);
                    target.setVelocity(target.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(2));
                }
            }
            prof.setCooldown("ability2", 12);
        }
    }
}
