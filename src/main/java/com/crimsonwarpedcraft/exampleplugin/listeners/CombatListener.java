package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CombatListener implements Listener {
    private final ExamplePlugin plugin;
    public CombatListener(ExamplePlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        // Learn Technique from Knowledge Book Scroll
        NamespacedKey scrollKey = new NamespacedKey(plugin, "technique_scroll");
        if (item.getType() == Material.KNOWLEDGE_BOOK && item.getItemMeta().getPersistentDataContainer().has(scrollKey, PersistentDataType.STRING)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                String techName = item.getItemMeta().getPersistentDataContainer().get(scrollKey, PersistentDataType.STRING);
                PlayerProfile prof = plugin.getProfileManager().getProfile(player.getUniqueId());
                
                try {
                    prof.setTechnique(TechniqueType.valueOf(techName.toUpperCase()));
                    item.setAmount(item.getAmount() - 1);
                    player.sendMessage("§a§lSUCCESS! §7You mastered the §d" + techName + " §7technique.");
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
                } catch (Exception e) {
                    player.sendMessage("§cError: Invalid Technique in scroll.");
                }
                event.setCancelled(true);
            }
        }
    }
}
