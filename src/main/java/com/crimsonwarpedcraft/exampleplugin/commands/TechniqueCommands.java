package com.crimsonwarpedcraft.exampleplugin.commands;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import com.crimsonwarpedcraft.exampleplugin.data.PlayerProfile;
import com.crimsonwarpedcraft.exampleplugin.data.TechniqueType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
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

        if (args.length == 0) return true;
        String action = args[0].toLowerCase();

        switch (action) {
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
                        TechniqueType tech = profile.getTechnique();
                        profile.setTechnique(TechniqueType.NONE);
                        player.getInventory().addItem(createClassItem(tech));
                        player.sendMessage("§aTechnique withdrawn into a scroll.");
                    }
                }
            }
            case "givespear" -> {
                if (!player.hasPermission("jjk.admin")) return true;
                player.getInventory().addItem(createSpear());
                player.sendMessage("§aInverted Spear of Heaven granted.");
            }
        }
        plugin.getProfileManager().saveProfile(player.getUniqueId());
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

    private ItemStack createSpear() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName("§f§lInverted Spear of Heaven");
        m.setLore(List.of("§7Pierces and nullifies all techniques."));
        item.setItemMeta(m);
        return item;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String a, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("withdraw", "info", "givespear");
        if (args[0].equalsIgnoreCase("withdraw")) return Arrays.asList("ce", "class");
        return null;
    }
}
