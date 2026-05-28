@EventHandler
    public void onTechniqueLearn(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.KNOWLEDGE_BOOK) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey scrollKey = new NamespacedKey(plugin, "technique_scroll");

        if (meta.getPersistentDataContainer().has(scrollKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
            
            String techName = meta.getPersistentDataContainer().get(scrollKey, PersistentDataType.STRING);
            TechniqueType newTech = TechniqueType.valueOf(techName);
            
            PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            
            // Learning logic
            profile.setTechnique(newTech);
            item.setAmount(item.getAmount() - 1); // Consume item
            
            player.sendMessage("§b§m---------------------------------------------");
            player.sendMessage("§a§lTECHNIQUE ACQUIRED!");
            player.sendMessage("§7You have mastered: §d§l" + newTech.name());
            player.sendMessage("§b§m---------------------------------------------");
            
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            plugin.getProfileManager().saveProfile(player.getUniqueId());
        }
    }
