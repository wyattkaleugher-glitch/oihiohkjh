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

import java.util.ArrayList;
import java.util.List;

public class CombatListener implements Listener {

    private final ExamplePlugin plugin;
    private final NamespacedKey ceKey;

    public CombatListener(ExamplePlugin plugin) {
        this.plugin = plugin;
        // The secure digital key that holds the hidden numerical CE value inside the item
        this.ceKey = new NamespacedKey(plugin, "dropped_ce_amount");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        PlayerProfile profile = plugin.getProfileManager().getProfile(victim.getUniqueId());

        int currentCE = profile.getCursedEnergy();
        // If they have barely any energy, don't drop a completely empty shard
        if (currentCE <= 10) return; 

        // Calculate drop value (Victim loses 50% of their current CE pool)
        int ceToDrop = currentCE / 2;
        profile.setCursedEnergy(currentCE - ceToDrop);
        victim.sendMessage("§cYou dropped §e" + ceToDrop + " CE §con the ground upon death!");

        // Spawn ONE single Amethyst Shard as the physical token
        ItemStack crystal = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = crystal.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§d§lCrystallized Cursed Energy");
            
            List<String> lore = new ArrayList<>();
            lore.add("§7A fragmented shard of raw negative emotion.");
            lore.add("§7Right-Click to absorb its power into your core.");
            lore.add("");
            // Visually display the payload number to players
            lore.add("§fContains: §b" + ceToDrop + " CE");
            meta.setLore(lore);

            // SECURE INJECTION: Locks the numerical value permanently inside the item data
            meta.getPersistentDataContainer().set(ceKey, PersistentDataType.INTEGER, ceToDrop);
            crystal.setItemMeta(meta);
        }

        // Drop the custom crystal stack directly into the grave item drops array
        event.getDrops().add(crystal);
    }

    @EventHandler
    public void onAbsorbCE(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Ensure they are right-clicking an item
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (item == null || item.getType() != Material.AMETHYST_SHARD) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        // Security gate: If it doesn't have our hidden data key, ignore it (stops fake anvil items)
        if (!meta.getPersistentDataContainer().has(ceKey, PersistentDataType.INTEGER)) return;

        // Cancel standard block placements or default item interactions
        event.setCancelled(true);

        // Extract the hidden mathematical value from the item
        Integer ceStored = meta.getPersistentDataContainer().get(ceKey, PersistentDataType.INTEGER);
        if (ceStored == null) return;

        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        // Fail-safe: Prevent players from wasting the shard if they are already maxed out
        if (profile.getCursedEnergy() >= profile.getMaxCursedEnergy()) {
            player.sendMessage("§cYour Cursed Energy reserves are already entirely full!");
            return;
        }

        // Safely strip exactly ONE crystal from their hand stack
        item.setAmount(item.getAmount() - 1);
        
        // Deposit the extracted hidden value right into their active pool
        profile.setCursedEnergy(profile.getCursedEnergy() + ceStored);

        // Play feedback notification, sound engine, and ambient particles
        player.sendMessage("§a§l+ " + ceStored + " CE §eabsorbed! Current pool: §b" + profile.getCursedEnergy() + "/" + profile.getMaxCursedEnergy());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.6F);
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 1, 0), 12, 0.2, 0.4, 0.2, 0.02);
    }
}
