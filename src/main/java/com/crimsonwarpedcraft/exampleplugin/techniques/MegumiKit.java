package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MegumiKit {
    public static void execute(Player p, PlayerProfile prof, int slot) {
        ExamplePlugin plugin = JavaPlugin.getPlugin(ExamplePlugin.class);

        if (slot == 1) { // Divine Dogs
            p.sendMessage("§8Divine Dogs, come!");
            p.getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
            prof.setCooldown("ability1", 20);

        } else if (slot == 2) { // MAHORAGA SUMMON
            p.sendMessage("§8§lWith this treasure, I summon...");
            p.sendMessage("§0§lMAHORAGA");

            // Pull stats from Config
            double hp = plugin.getConfig().getDouble("kits.megumi.mahoraga.health", 200.0);
            int speed = plugin.getConfig().getInt("kits.megumi.mahoraga.speed-level", 3);
            int strength = plugin.getConfig().getInt("kits.megumi.mahoraga.strength-level", 1);
            int duration = plugin.getConfig().getInt("kits.megumi.mahoraga.lifespan-seconds", 60);

            // Spawn and Buff Golem
            IronGolem mahoraga = (IronGolem) p.getWorld().spawnEntity(p.getLocation(), EntityType.IRON_GOLEM);
            mahoraga.setCustomName("§0§lDivine General Mahoraga");
            mahoraga.setCustomNameVisible(true);
            mahoraga.setPlayerCreated(false);

            // Set HP
            mahoraga.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hp);
            mahoraga.setHealth(hp);

            // Apply Potion Effects
            mahoraga.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, speed));
            mahoraga.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration * 20, strength));
            mahoraga.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration * 20, 1));

            p.getWorld().strikeLightningEffect(p.getLocation());
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);

            // Targeting Loop
            new BukkitRunnable() {
                int lifespan = 0;
                @Override
                public void run() {
                    if (lifespan >= duration || mahoraga.isDead() || !p.isOnline()) {
                        if (!mahoraga.isDead()) mahoraga.remove();
                        this.cancel();
                        return;
                    }

                    Player target = null;
                    double dist = 20.0;

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (online.equals(p) || online.getGameMode().name().contains("CREATIVE")) continue;
                        double d = online.getLocation().distance(mahoraga.getLocation());
                        if (d < dist) {
                            dist = d;
                            target = online;
                        }
                    }

                    if (target != null) {
                        mahoraga.setTarget(target);
                    }
                    lifespan++;
                }
            }.runTaskTimer(plugin, 0L, 20L);

            prof.setCooldown("ability2", 300);
        }
    }
}
