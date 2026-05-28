package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        this.ceKey = new NamespacedKey(plugin, "dropped_ce_amount");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        PlayerProfile profile = plugin.getProfileManager().getProfile(victim.getUniqueId());

        int currentCE = profile.getCursedEnergy();
        if (currentCE <= 10) return; // Don't drop anything if they are already completely broke

        // Calculate drop amounts (Lose 50% of current CE upon death)
        int ceToDrop = currentCE / 2;
        profile.setCursedEnergy(currentCE - ceToDrop);
        victim.sendMessage("§cYou dropped §e" + ceToDrop + " CE §con the ground upon death!");

        // Create the physical Cursed Energy Item Stack
        ItemStack crystal = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = crystal.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§d§lCrystallized Cursed Energy");
            List<String> lore = new ArrayList<>();
            lore.add("§7A fragmented shard of raw negative emotion.");
            lore.add("§7Right-Click to absorb its power.");
            lore.add("");
            lore.add("§fContains: §b" + ceToDrop + " CE");
            meta.setLore(lore);

            // Inject the data value into the item permanently so players can't duplicate fake items
            meta.getPersistentDataContainer().set(ceKey, PersistentDataType.INTEGER, ceToDrop);
            crystal.setItemMeta(meta);
        }

        // Add it directly to the ground drops list at the death spot
        event.getDrops().add(crystal);
    }

    @EventHandler
    public void onAbsorbCE(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.AMETHYST_SHARD) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(ceKey, PersistentDataType.INTEGER)) return;

        // Block interaction if they try to look at a block while consuming it
        event.setCancelled(true);

        // Extract value
        int ceStored = meta.getPersistentDataContainer().get(ceKey, PersistentDataType.INTEGER);
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile.getCursedEnergy() >= profile.getMaxCursedEnergy()) {
            player.sendMessage("§cYour Cursed Energy reserves are already entirely full!");
            return;
        }

        // Consume 1 item from stack and deposit energy
        item.setAmount(item.getAmount() - 1);
        profile.setCursedEnergy(profile.getCursedEnergy() + ceStored);

        player.sendMessage("§a§l+ " + ceStored + " CE §ehas been absorbed back into your core! Current: §b" + profile.getCursedEnergy());
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.7F);
        player.getWorld().spawnParticle(org.bukkit.Particle.SOUL, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.05);
    }
}
