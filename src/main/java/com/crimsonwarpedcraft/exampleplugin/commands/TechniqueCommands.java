package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import com.crimsonwarpedcraft.exampleplugin.techniques.DomainEngine;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TechniqueCommands implements TabExecutor {

    private final ExamplePlugin plugin;

    public TechniqueCommands(ExamplePlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (args.length == 0) return true;
        String action = args[0].toLowerCase();

        switch (action) {
            case "info" -> {
                player.sendMessage("§eGrade: " + profile.getGrade());
                player.sendMessage("§aTechnique: " + profile.getTechnique().name());
            }
            case "withdraw" -> {
                if (args.length < 2) return true;
                if (args[1].equalsIgnoreCase("ce")) {
                    int amt = Integer.parseInt(args[2]);
                    if (profile.getCursedEnergy() >= amt) {
                        profile.setCursedEnergy(profile.getCursedEnergy() - amt);
                        player.getInventory().addItem(createCEItem(amt));
                    }
                }
            }
            case "ability" -> player.sendMessage("§aUsing current Technique...");
            case "domain" -> DomainEngine.launchDomain(player, profile);
            case "givespear" -> {
                if (!player.hasPermission("jjk.admin")) return true;
                ItemStack spear = new ItemStack(Material.NETHERITE_SWORD);
                ItemMeta m = spear.getItemMeta();
                m.setDisplayName("§f§lInverted Spear of Heaven");
                m.addEnchant(Enchantment.SHARPNESS, 6, true);
                spear.setItemMeta(m);
                player.getInventory().addItem(spear);
            }
            case "clearcooldown" -> profile.clearAllCooldowns();
        }
        return true;
    }

    private ItemStack createCEItem(int amt) {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName("§d§lCE Crystal §7(" + amt + ")");
        m.getPersistentDataContainer().set(new NamespacedKey(plugin, "dropped_ce_amount"), PersistentDataType.INTEGER, amt);
        item.setItemMeta(m);
        return item;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String a, @NotNull String[] args) {
        return Arrays.asList("info", "withdraw", "ability", "domain", "givespear", "clearcooldown");
    }
}
