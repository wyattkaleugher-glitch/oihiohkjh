package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;
import java.util.List;

public class CombatListener implements Listener {

    private final ExamplePlugin plugin;
    private final NamespacedKey ceKey;
    private final NamespacedKey gradeItemKey;

    public CombatListener(ExamplePlugin plugin) {
        this.plugin = plugin;
        this.ceKey = new NamespacedKey(plugin, "dropped_ce_amount");
        this.gradeItemKey = new NamespacedKey(plugin, "grade_up_token");
    }

    @EventHandler
    public void onDomainBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        NamespacedKey blockKey = new NamespacedKey(plugin, "domain_block_" + block.getX() + "_" + block.getY() + "_" + block.getZ());
        if (block.getChunk().getPersistentDataContainer().has(blockKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot alter the layout of a Domain Expansion's inner world!");
        }
    }

    @EventHandler
    public void onDomainExplode(EntityExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            NamespacedKey blockKey = new NamespacedKey(plugin, "domain_block_" + block.getX() + "_" + block.getY() + "_" + block.getZ());
            if (block.getChunk().getPersistentDataContainer().has(blockKey, PersistentDataType.BYTE)) iterator.remove();
        }
    }

    @EventHandler
    public void onDomainBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            NamespacedKey blockKey = new NamespacedKey(plugin, "domain_block_" + block.getX() + "_" + block.getY() + "_" + block.getZ());
            if (block.getChunk().getPersistentDataContainer().has(blockKey, PersistentDataType.BYTE)) iterator.remove();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        PlayerProfile profile = plugin.getProfileManager().getProfile(victim.getUniqueId());

        int currentCE = profile.getCursedEnergy();
        if (currentCE <= 10) return; 

        int ceToDrop = currentCE / 2;
        profile.setCursedEnergy(currentCE - ceToDrop);
        victim.sendMessage("§cYou dropped §e" + ceToDrop + " CE §con the ground upon death!");

        ItemStack crystal = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = crystal.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§lCrystallized Cursed Energy");
            meta.setLore(List.of("§7Right-Click to absorb its power.", "", "§fContains: §b" + ceToDrop + " CE"));
            meta.getPersistentDataContainer().set(ceKey, PersistentDataType.INTEGER, ceToDrop);
            crystal.setItemMeta(meta);
        }
        event.getDrops().add(crystal);
    }

    @EventHandler
    public void onItemUsageRouter(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        // 🎖️ ROUTINE A: GRADE UP PROMOTION LOGIC DETECTOR
        if (meta.getPersistentDataContainer().has(gradeItemKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            
            String currentGrade = profile.getGrade();
            if (currentGrade.equalsIgnoreCase("Special Grade")) {
                player.sendMessage("§cYou have already attained the highest peak of sorcery: Special Grade!");
                return;
            }

            // Consume exactly one recommendation scroll document
            item.setAmount(item.getAmount() - 1);
            
            // Advance rank & save to data folder
            profile.promoteToNextGrade();
            plugin.getProfileManager().saveProfile(player.getUniqueId());

            // Play celebratory server side special effects
            player.sendMessage("§b§m---------------------------------------------");
            player.sendMessage("§6§l        JUJUTSU RANK PROMOTION!");
            player.sendMessage("§7  Your official sorcerer registration classification");
            player.sendMessage("§7  has risen from §7" + currentGrade + " §7to: §d§l" + profile.getGrade());
            player.sendMessage("§b§m---------------------------------------------");

            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.2F, 1.0F);
            player.getWorld().spawnParticle(Particle.CHERRY_LEAVES, player.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.1);
            return;
        }

        // 💎 ROUTINE B: CURSED ENERGY CRYSTAL SHARD ABSORPTION
        if (meta.getPersistentDataContainer().has(ceKey, PersistentDataType.INTEGER)) {
            if (item.getType() != Material.AMETHYST_SHARD) return;
            event.setCancelled(true);

            Integer ceStored = meta.getPersistentDataContainer().get(ceKey, PersistentDataType.INTEGER);
            if (ceStored == null) return;

            if (profile.getCursedEnergy() >= profile.getMaxCursedEnergy()) {
                player.sendMessage("§cYour energy reserves are already maximized!");
                return;
            }

            item.setAmount(item.getAmount() - 1);
            profile.setCursedEnergy(profile.getCursedEnergy() + ceStored);
            plugin.getProfileManager().saveProfile(player.getUniqueId());

            player.sendMessage("§a§l+ " + ceStored + " CE §eabsorbed! (§b" + profile.getCursedEnergy() + "/" + profile.getMaxCursedEnergy() + "§e)");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.6F);
            player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 1, 0), 12, 0.2, 0.4, 0.2, 0.02);
        }
    }
}
