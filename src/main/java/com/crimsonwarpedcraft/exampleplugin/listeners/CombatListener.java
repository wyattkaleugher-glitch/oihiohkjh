package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class CombatListener implements Listener {
    private final ExamplePlugin plugin;
    public CombatListener(ExamplePlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;
        
        // 🛡️ SELF-DAMAGE FIX
        if (attacker.equals(victim)) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = attacker.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Inverted Spear")) {
            PlayerProfile vProf = plugin.getProfileManager().getProfile(victim.getUniqueId());
            vProf.setCooldown("ability1", 5);
            vProf.setCooldown("ability2", 5);
            vProf.setCursedEnergy(vProf.getCursedEnergy() - 150);
            victim.sendMessage("§c§lNULLIFIED!");
        }
    }

    @EventHandler
    public void onISOHInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getDisplayName().contains("Inverted Spear")) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            if (b != null && (b.getType() == Material.TINTED_GLASS || b.getType() == Material.CRYING_OBSIDIAN)) {
                // Pierces domain blocks
                b.setType(Material.AIR);
                player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
            }
        }
    }
}
