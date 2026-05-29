package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
            
            // Buffs for the "Summoner"
            p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 2));
            p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 3));
            
            p.getWorld().strikeLightningEffect(p.getLocation());
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);
            
            prof.setCooldown("ability2", 120);
        }
    }
}
