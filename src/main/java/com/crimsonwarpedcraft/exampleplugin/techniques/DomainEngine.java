package com.crimsonwarpedcraft.exampleplugin.techniques;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DomainEngine {

    public static void launchDomain(Player player, PlayerProfile profile, FileConfiguration config) {
        ExamplePlugin plugin = (ExamplePlugin) JavaPlugin.getProvidingPlugin(ExamplePlugin.class);
        
        int ceCost = config.getInt("domain.ce-cost", 500);
        int radius = config.getInt("domain.radius", 10);
        double damage = config.getDouble("domain.surehit-damage", 16.0);
        int durationTicks = 300; // 15 Seconds

        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        Location center = player.getLocation();

        // ⚔️ DOMAIN CLASH DETECTION ENGINE
        UUID rivalUuid = plugin.getDomainManager().checkOverlap(center, radius);
        
        if (rivalUuid != null) {
            Player rival = Bukkit.getPlayer(rivalUuid);
            if (rival != null && rival.isOnline()) {
                PlayerProfile rivalProfile = plugin.getProfileManager().getProfile(rivalUuid);
                
                int userWeight = getGradeWeight(profile.getGrade());
                int rivalWeight = getGradeWeight(rivalProfile.getGrade());

                Bukkit.broadcastMessage("§d§l§m---------------------------------------------");
                Bukkit.broadcastMessage("§c§l💥 DOMAIN CLASH DETECTED! 💥");
                Bukkit.broadcastMessage("§e " + player.getName() + " §7is clashing barriers with §e" + rival.getName() + "§7!");
                Bukkit.broadcastMessage("§d§l§m---------------------------------------------");

                center.getWorld().playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0F, 0.5F);
                center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center, 5, 2.0, 2.0, 2.0);

                if (userWeight < rivalWeight) {
                    player.sendMessage("§cYour barrier was completely overwhelmed and shattered by " + rival.getName() + "!");
                    player.playSound(center, Sound.BLOCK_GLASS_BREAK, 2.0F, 0.5F);
                    player.damage(6.0, rival);
                    return; 
                } 
                else if (userWeight > rivalWeight) {
                    rival.sendMessage("§cYour domain expansion collapsed! " + player.getName() + " overpowered you!");
                    rival.playSound(rival.getLocation(), Sound.BLOCK_GLASS_BREAK, 2.0F, 0.5F);
                    plugin.getDomainManager().unregisterDomain(rivalUuid);
                    player.sendMessage("§aYour superior grade instantly overpowered " + rival.getName() + "!");
                } 
                else {
                    player.sendMessage("§6Barriers are evenly matched! A stalemate has commenced!");
                    rival.sendMessage("§6Barriers are evenly matched! A stalemate has commenced!");
                    center.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, center, 100, radius, 2.0, radius);
                    return; 
                }
            }
        }

        // Proceed with normal domain generation
        plugin.getDomainManager().registerDomain(player.getUniqueId(), center);
        player.sendMessage("§0§lDomain Expansion!!");

        // List to track blocks for cleanup
        List<Block> domainBlocks = new ArrayList<>();

        // Generate the physical shell
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    // Create a hollow sphere shell
                    if (distance >= radius - 0.8 && distance <= radius + 0.5) {
                        Block block = center.clone().add(x, y, z).getBlock();
                        
                        // Only replace air to avoid deleting the floor
                        if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                            block.setType(Material.BLACK_CONCRETE);
                            domainBlocks.add(block);
                            
                            block.getChunk().getPersistentDataContainer().set(
                                new NamespacedKey(plugin, "domain_block_" + block.getX() + "_" + block.getY() + "_" + block.getZ()), 
                                PersistentDataType.BYTE, 
                                (byte) 1
                            );
                        }
                    }
                }
            }
        }

        center.getWorld().playSound(center, Sound.BLOCK_PORTAL_TRAVEL, 1.5F, 0.5F);

        // Sure-hit effect
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity target && !entity.equals(player)) {
                target.damage(damage, player);
                target.setFireTicks(100);
                target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation().add(0,1,0), 20);
            }
        }

        // PHYSICAL CLEANUP TASK
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // 1. Remove the blocks from the world
            for (Block b : domainBlocks) {
                if (b.getType() == Material.BLACK_CONCRETE) {
                    b.setType(Material.AIR);
                }
            }

            // 2. Play shattering effects
            center.getWorld().playSound(center, Sound.BLOCK_GLASS_BREAK, 2.0F, 0.5F);
            center.getWorld().spawnParticle(Particle.WITCH, center, 100, radius / 2.0, 2.0, radius / 2.0);

            // 3. Unregister the data
            plugin.getDomainManager().unregisterDomain(player.getUniqueId());
            
            if (player.isOnline()) {
                player.sendMessage("§7The domain barrier has faded...");
            }
        }, (long) durationTicks); 
    }

    private static int getGradeWeight(String grade) {
        if (grade == null) return 1;
        return switch (grade) {
            case "Special Grade" -> 5;
            case "Grade 1" -> 4;
            case "Grade 2" -> 3;
            case "Grade 3" -> 2;
            default -> 1;
        };
    }
}
