@EventHandler
    public void onInvertedSpearHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon == null || !weapon.hasItemMeta()) return;

        // Check if the item is the Inverted Spear by name
        if (weapon.getItemMeta().getDisplayName().contains("Inverted Spear of Heaven")) {
            PlayerProfile victimProfile = plugin.getProfileManager().getProfile(victim.getUniqueId());

            // 1. TECHNIQUE NULLIFICATION
            // Forces abilities into a 5-second lock (even if they weren't on cooldown)
            victimProfile.setCooldown("ability1", 5);
            victimProfile.setCooldown("ability2", 5);

            // 2. ENERGY DRAIN
            // Pulls 150 CE out of their pool per hit
            victimProfile.setCursedEnergy(victimProfile.getCursedEnergy() - 150);

            // 3. VISUALS & FEEDBACK
            victim.sendMessage("§c§lNULLIFIED! §7Your technique was short-circuited!");
            attacker.sendMessage("§b§lHIT! §7You disrupted " + victim.getName() + "'s energy flow.");
            
            victim.getWorld().spawnParticle(Particle.WITCH, victim.getLocation().add(0, 1, 0), 25, 0.5, 0.5, 0.5, 0.1);
            victim.playSound(victim.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 2.0f);
        }
    }

    @EventHandler
    public void onDomainShatter(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta()) return;
        if (!item.getItemMeta().getDisplayName().contains("Inverted Spear of Heaven")) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.BLACK_CONCRETE) {
            // Check if this block is part of a domain
            NamespacedKey blockKey = new NamespacedKey
