package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import com.crimsonwarpedcraft.exampleplugin.techniques.DomainEngine;
import org.bukkit.Bukkit;
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

    public TechniqueCommands(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage("§e/jjk <info|withdraw|ability|domain>");
            return true;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "info" -> {
                player.sendMessage("§b§m--- §e§lJUJUTSU PROFILE §b§m---");
                player.sendMessage("§7Grade: §d" + profile.getGrade());
                player.sendMessage("§7Technique: §a" + profile.getTechnique().name());
                player.sendMessage("§7Energy: §b" + profile.getCursedEnergy() + "/" + profile.getMaxCursedEnergy());
            }

            case "withdraw" -> {
                if (args.length < 2) return true;
                if (args[1].equalsIgnoreCase("ce")) {
                    int amt = Integer.parseInt(args[2]);
                    if (profile.getCursedEnergy() >= amt) {
                        profile.setCursedEnergy(profile.getCursedEnergy() - amt);
                        player.getInventory().addItem(createCEItem(amt));
                    }
                } else if (args[1].equalsIgnoreCase("class")) {
                    if (profile.getTechnique() != TechniqueType.NONE) {
                        TechniqueType t = profile.getTechnique();
                        profile.setTechnique(TechniqueType.NONE);
                        player.getInventory().addItem(createClassItem(t));
                    }
                }
            }

            case "ability" -> {
                player.sendMessage("§aActivating Cursed Technique...");
                // Call your technique logic here
            }

            case "domain" -> {
                DomainEngine.launchDomain(player, profile);
            }

            case "givespear" -> {
                if (!player.hasPermission("jjk.admin")) return true;
                ItemStack spear = new ItemStack(Material.NETHERITE_SWORD);
                ItemMeta m = spear.getItemMeta();
                m.setDisplayName("§f§lInverted Spear of Heaven");
                m.setLore(List.of("§7Sharpness VI", "§7Nullifies all Techniques on hit."));
                m.addEnchant(Enchantment.SHARPNESS, 6, true);
                spear.setItemMeta(m);
                player.getInventory().addItem(spear);
            }

            case "clearcooldown" -> {
                if (!player.hasPermission("jjk.admin")) return true;
                profile.clearAllCooldowns();
                player.sendMessage("§aCooldowns reset.");
            }
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

    private ItemStack createClassItem(TechniqueType tech) {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName("§6§lScroll: §d" + tech.name());
        m.getPersistentDataContainer().set(new NamespacedKey(plugin, "technique_scroll"), PersistentDataType.STRING, tech.name());
        item.setItemMeta(m);
        return item;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String a, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("info", "withdraw", "ability", "domain", "givespear", "clearcooldown");
        return new ArrayList<>();
    }
}
