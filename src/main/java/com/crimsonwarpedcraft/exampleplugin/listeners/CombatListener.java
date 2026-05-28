package com.crimsonwarpedcraft.exampleplugin.listeners;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CombatListener implements Listener {

    private final ExamplePlugin plugin;

    public CombatListener(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpearHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;
        ItemStack item = attacker.getInventory().getItemInMainHand();

        if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Inverted Spear")) {
            PlayerProfile vProf = plugin.getProfileManager().getProfile(victim.getUniqueId());
            vProf.setCooldown("ability1", 5);
            vProf.setCooldown("ability2", 5);
            vProf.setCursedEnergy(vProf.getCursedEnergy() - 150);
            victim.sendMessage("§c§lNULLIFIED!");
            victim.playSound(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 2f);
        }
    }

    @EventHandler
    public void onScrollUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        NamespacedKey key = new NamespacedKey(plugin, "technique_scroll");
        if (item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String techName = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            profile.setTechnique(TechniqueType.valueOf(techName));
            item.setAmount(item.getAmount() - 1);
            player.sendMessage("§aLearned: §d" + techName);
            plugin.getProfileManager().saveProfile(player.getUniqueId());
        }
    }
}
