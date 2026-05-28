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

import java.util.UUID;

public class DomainEngine {

    public static void launchDomain(Player player, PlayerProfile profile, FileConfiguration config) {
        ExamplePlugin plugin = JavaPlugin.getProvidingPlugin(ExamplePlugin.class);
        
        int ceCost = config.getInt("domain.ce-cost", 500);
        int radius = config.getInt("domain.radius", 10);
        double damage = config.getDouble("domain.surehit-damage", 16.0);

        profile.setCursedEnergy(profile.getCursedEnergy() - ceCost);
        Location center = player.getLocation();

        // ⚔️ DOMAIN CLASH DETECTION ENGINE
        UUID rivalUuid = plugin.getDomainManager().checkOverlap(center, radius);
        
        if (rivalUuid != null) {
            Player rival = Bukkit.getPlayer(rivalUuid);
            if (rival != null && rival.isOnline()) {
                PlayerProfile rivalProfile = plugin.getProfileManager().getProfile(rivalUuid);
                
                // Convert their assigned text grades into numeric weights for comparison
                int userWeight = getGradeWeight(profile.getGrade());
                int rivalWeight = getGradeWeight(rivalProfile.getGrade());

                // Broadcast the epic confrontation event to the server
                Bukkit.broadcastMessage("§d§l§m---------------------------------------------");
                Bukkit.broadcastMessage("§c§l💥 DOMAIN CLASH DETECTED! 💥");
                Bukkit.broadcastMessage("§e " + player.getName() + " §7is clashing barriers with §e" + rival.getName() + "§7!");
                Bukkit.broadcastMessage("§d§l§m---------------------------------------------");

                // Play intense explosion sounds at both players' locations
                center.getWorld().playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0F, 0.5F);
                center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center, 5, 2.0, 2.0, 2.0);

                if (userWeight < rivalWeight) {
                    // Scenario A: Caster has a weaker grade! Their domain instantly shatters.
                    player.sendMessage("§cYour barrier was completely overwhelmed and shattered by " + rival.getName() + "'s superior refinement!");
                    player.playSound(center, Sound.BLOCK_GLASS_BREAK, 2.0F, 0.5F);
                    player.damage(6.0, rival); // Take backfire penalty damage
                    return; // Stop domain from spawning entirely
                } 
                else if (userWeight > rivalWeight) {
                    // Scenario B: Caster has a stronger grade! They crush and close down the rival's domain.
                    rival.sendMessage("§cYour domain expansion collapsed! " + player.getName() + "'s domain totally overwhelmed your barrier space!");
                    rival.playSound(rival.getLocation(), Sound.BLOCK_GLASS_BREAK, 2.0F, 0.5F);
                    plugin.getDomainManager().unregisterDomain(rivalUuid); // Force close rival data assignment
                    
                    player.sendMessage("§aYour superior grade instantly overpowered " + rival.getName() + "'s barrier domain!");
                } 
                else {
                    // Scenario C: Perfectly equal grade weight! Fuses the arena layout (Stalemate)
                    player.sendMessage("§6Barriers are evenly matched! A refined, high-stakes stalemate has commenced!");
                    rival.sendMessage("§6Barriers are evenly matched! A refined, high-stakes stalemate has commenced!");
                    
                    // Spawn particle markers detailing the stalemate boundary lines
                    center.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, center, 100, radius, 2.0, radius);
                    // Let them fight it out using raw combat parameters—no extra walls are built
                    return; 
                }
            }
        }

        // Proceed with normal, uninterrupted domain generation if no clash/stalemate occurred
        plugin.getDomainManager().registerDomain(player.getUniqueId(), center);
        player.sendMessage("§0§lDomain Expansion!!");
        
        NamespacedKey pluginKey = new NamespacedKey(plugin, "domain_barrier");

        // Generate the physical protective outer shell boundary box layout
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance >= radius - 0.8 && distance <= radius + 0.5) {
                        Block block = center.clone().add(x, y, z).getBlock();
                        block.setType(Material.BLACK_CONCRETE);
                        
                        block.getChunk().getPersistentDataContainer().set(
                            new NamespacedKey(plugin, "domain_block_" + block.getX() + "_" + block.getY() + "_" + block.getZ()), 
                            PersistentDataType.BYTE, 
                            (byte) 1
                        );
                    }
                }
            }
        }

        center.getWorld().playSound(center, Sound.BLOCK_PORTAL_TRAVEL, 1.5F, 0.5F);

        // Apply automatic domain area sure-hit targeting profiles
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity target && !entity.equals(player)) {
                target.damage(damage, player);
                target.setFireTicks(100);
                target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation().add(0,1,0), 20);
            }
        }

        // Clean up and unregister the domain safely after 15 seconds so players aren't trapped forever
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getDomainManager().unregisterDomain(player.getUniqueId());
            // (Optional: You can add code here later to clear the concrete blocks back into air)
        }, 300L); // 15 seconds (300 ticks)
    }

    /**
     * Converts a player's text Grade into an integer priority weight value.
     */
    private static int getGradeWeight(String grade) {
        return switch (grade) {
            case "Special Grade" -> 5;
            case "Grade 1" -> 4;
            case "Grade 2" -> 3;
            case "Grade 3" -> 2;
            default -> 1; // Grade 4 or custom unranked titles
        };
    }
}
