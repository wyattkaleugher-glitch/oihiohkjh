package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CombatListener implements Listener {
    private final ExamplePlugin plugin;

    public CombatListener(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDomainBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        // Check if the block looks like a domain material
        if (type.name().contains("STAINED_GLASS") || type == Material.TINTED_GLASS || type == Material.OBSIDIAN || type == Material.BLACK_CONCRETE) {
            // If the block is part of any active domain, cancel the break
            if (plugin.getDomainManager().isInsideAnyDomain(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        // ISOH Specific logic: Allow it to bypass the "Unbreakable" rule on Left-Click
        if (item.getItemMeta().getDisplayName().contains("Inverted Spear")) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block b = event.getClickedBlock();
                if (b != null && plugin.getDomainManager().isInsideAnyDomain(b.getLocation())) {
                    b.setType(Material.AIR);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
                }
            }
            return;
        }

        // Technique Scroll logic
        NamespacedKey scrollKey = new NamespacedKey(plugin, "technique_scroll");
        if (item.getType() == Material.KNOWLEDGE_BOOK && item.getItemMeta().getPersistentDataContainer().has(scrollKey, PersistentDataType.STRING)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                String techName = item.getItemMeta().getPersistentDataContainer().get(scrollKey, PersistentDataType.STRING);
                PlayerProfile prof = plugin.getProfileManager().getProfile(player.getUniqueId());
                prof.setTechnique(com.crimsonwarpedcraft.exampleplugin.data.TechniqueType.valueOf(techName.toUpperCase()));
                item.setAmount(item.getAmount() - 1);
                player.sendMessage("§a§lSUCCESS! §7You mastered §d" + techName);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
                event.setCancelled(true);
            }
        }
    }
}
