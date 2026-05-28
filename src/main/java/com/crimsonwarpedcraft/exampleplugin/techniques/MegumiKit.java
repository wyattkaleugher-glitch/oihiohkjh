package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class MegumiKit {
    public static void castDivineDog(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.megumi.dog-ce-cost", 20);

        if (profile.getCursedEnergy() < ceCost) {
            player.sendMessage("§cNot enough Cursed Energy!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§8§lDivine Dog: Totality!");

        Location spawnLoc = player.getLocation().add(player.getLocation().getDirection().multiply(1.5));
        spawnLoc.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, spawnLoc, 20, 0.3, 0.3, 0.3);
        spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_WOLF_GROWL, 1.0F, 0.8F);

        Wolf wolf = (Wolf) player.getWorld().spawnEntity(spawnLoc, EntityType.WOLF);
        wolf.setTamed(true);
        wolf.setOwner(player);
        wolf.setCustomName("§8§lDivine Dog Totality");
        wolf.setCustomNameVisible(true);
    }

    public static void castMahoraga(Player player, PlayerProfile profile, FileConfiguration config) {
        int ceCost = config.getInt("techniques.megumi.mahoraga-ce-cost", 150);

        if (profile.getCursedEnergy() < ceCost) {
            player.sendMessage("§cNot enough Cursed Energy!");
            return;
        }
        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        player.sendMessage("§5§lWith this treasure, I summon...");

        Location spawnLoc = player.getLocation();
        spawnLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, spawnLoc, 5, 1.0, 1.0, 1.0);
        spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_WITHER_SPAWN, 1.0F, 0.5F);

        IronGolem mahoraga = (IronGolem) player.getWorld().spawnEntity(spawnLoc, EntityType.IRON_GOLEM);
        mahoraga.setCustomName("§5§lDivine General Mahoraga");
        mahoraga.setCustomNameVisible(true);
    }
}
