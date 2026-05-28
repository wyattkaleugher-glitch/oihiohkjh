package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CombatListener implements Listener {

    private final ExamplePlugin plugin;
    private final NamespacedKey ceKey;

    public CombatListener(ExamplePlugin plugin) {
        this.plugin = plugin;
        // The master digital key used to store and extract the CE amount from item metadata
        this.ceKey = new NamespacedKey(plugin, "dropped_ce_amount");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        PlayerProfile profile = plugin.getProfileManager().getProfile(victim.getUniqueId());

        int currentCE = profile.getCursedEnergy();
        if (currentCE <= 10) return; 

        // Calculate drop value: Exactly half of their active energy pool
        int ceToDrop = currentCE / 2;
        profile.setCursedEnergy(currentCE - ceToDrop);
        victim.sendMessage("§cYou dropped §e" + ceToDrop + " CE §con the ground upon death!");

        // Construct the physical item token
        ItemStack crystal = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = crystal.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§d§lCrystallized Cursed Energy");
            meta.setLore(List.of("§7Right-Click to absorb its power.", "", "§fContains: §b" + ceToDrop + " CE"));
            
            // Inject the data value permanently into the item
            meta.getPersistentDataContainer().set(ceKey, PersistentDataType.INTEGER, ceToDrop);
            crystal.setItemMeta(meta);
        }
        event.getDrops().add(crystal);
    }

    @EventHandler
    public void onAbsorbCE(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Safety verification gates
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (item == null || item.getType() != Material.AMETHYST_SHARD) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        // Crucial check: Verify if the item has our hidden digital tracking signature
        if (!meta.getPersistentDataContainer().has(ceKey, PersistentDataType.INTEGER)) return;

        // Cancel standard item usage (like placing blocks if it were a block)
        event.setCancelled(true);

        // Safely extract the integer value
        Integer ceStored = meta.getPersistentDataContainer().get(ceKey, PersistentDataType.INTEGER);
        if (ceStored == null) return;

        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        // Block absorption if their resource pools are already entirely maxed out
        if (profile.getCursedEnergy() >= profile.getMaxCursedEnergy()) {
            player.sendMessage("§cYour energy reserves are already maximized!");
            return;
        }

        // Subtract exactly 1 shard from their inventory stack
        item.setAmount(item.getAmount() - 1);
        
        // Add the extracted energy directly to their profile
        profile.setCursedEnergy(profile.getCursedEnergy() + ceStored);

        // Feedback particles and sound effects
        player.sendMessage("§a§l+ " + ceStored + " CE §eabsorbed! (§b" + profile.getCursedEnergy() + "/" + profile.getMaxCursedEnergy() + "§e)");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.6F);
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 1, 0), 12, 0.2, 0.4, 0.2, 0.02);
    }
}
