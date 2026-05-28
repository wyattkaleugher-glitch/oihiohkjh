// 🏆 GRADE ADVANCEMENT & BUFF ENGINE
    public void promoteToNextGrade() {
        switch (this.jujutsuGrade) {
            case "Grade 4" -> this.jujutsuGrade = "Grade 3";
            case "Grade 3" -> this.jujutsuGrade = "Grade 2";
            case "Grade 2" -> this.jujutsuGrade = "Grade 1";
            case "Grade 1" -> this.jujutsuGrade = "Special Grade";
        }
        applyGradeBuffs(); // Re-apply buffs whenever the grade changes
    }

    public void applyGradeBuffs() {
        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;

        // Clear existing JJK buffs first to prevent stacking
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.SPEED);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE);

        // Apply buffs based on current rank
        switch (this.jujutsuGrade) {
            case "Special Grade" -> {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH, -1, 2)); // Strength 3
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, -1, 1));    // Speed 2
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, -1, 1)); // Resistance 2
            }
            case "Grade 1" -> {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH, -1, 1)); // Strength 2
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, -1, 1));    // Speed 2
            }
            case "Grade 2" -> {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH, -1, 0)); // Strength 1
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, -1, 0));    // Speed 1
            }
            case "Grade 3" -> {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, -1, 0));    // Speed 1
            }
            // Grade 4 gets no natural buffs
        }
    }
