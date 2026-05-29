package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CombatListener implements Listener {
    private final ExamplePlugin plugin;

    public CombatListener(ExamplePlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;

        if (attacker.equals(victim)) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = attacker.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Inverted Spear")) {
            PlayerProfile vProf = plugin.getProfileManager().getProfile(victim.getUniqueId());
            vProf.setCooldown("ability1", 5);
            vProf.setCooldown("ability2", 5);
            vProf.setCursedEnergy(Math.max(0, vProf.getCursedEnergy() - 150));
            victim.sendMessage("§c§lNULLIFIED!");
            victim.playSound(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 2f);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        NamespacedKey ceKey = new NamespacedKey(plugin, "dropped_ce_amount");
        if (item.getItemMeta().getPersistentDataContainer().has(ceKey, PersistentDataType.INTEGER)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                int amt = item.getItemMeta().getPersistentDataContainer().get(ceKey, PersistentDataType.INTEGER);
                PlayerProfile prof = plugin.getProfileManager().getProfile(player.getUniqueId());
                prof.setCursedEnergy(prof.getCursedEnergy() + amt);
                item.setAmount(item.getAmount() - 1);
                player.sendMessage("§b+" + amt + " CE Absorbed.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                event.setCancelled(true);
            }
            return;
        }

        if (item.getItemMeta().getDisplayName().contains("Inverted Spear")) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Block b = event.getClickedBlock();
                if (b != null && (b.getType() == Material.TINTED_GLASS || b.getType() == Material.CRYING_OBSIDIAN)) {
                    b.setType(Material.AIR);
                    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
                }
            }
        }
    }
}
