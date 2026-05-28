package com.crimsonwarpedcraft.exampleplugin.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProfile {

    private final UUID uuid;
    private int cursedEnergy;
    private int maxCursedEnergy;
    private TechniqueType technique;
    private String jujutsuGrade;
    private final Map<String, Long> cooldowns;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        this.cursedEnergy = 1000;
        this.maxCursedEnergy = 1000;
        this.technique = TechniqueType.NONE;
        this.jujutsuGrade = "Grade 4";
        this.cooldowns = new HashMap<>();
    }

    // 🏆 GRADE ADVANCEMENT & BUFF ENGINE
    public void promoteToNextGrade() {
        switch (this.jujutsuGrade) {
            case "Grade 4" -> this.jujutsuGrade = "Grade 3";
            case "Grade 3" -> this.jujutsuGrade = "Grade 2";
            case "Grade 2" -> this.jujutsuGrade = "Grade 1";
            case "Grade 1" -> this.jujutsuGrade = "Special Grade";
        }
        applyGradeBuffs();
    }

    public void applyGradeBuffs() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;

        // Clear existing JJK buffs
        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.RESISTANCE);

        // Apply buffs based on current rank (Amplitudes: 0 = Level 1, 1 = Level 2)
        switch (this.jujutsuGrade) {
            case "Special Grade" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, -1, 2, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 1, false, false));
            }
            case "Grade 1" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, -1, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 1, false, false));
            }
            case "Grade 2" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, -1, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
            }
            case "Grade 3" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
            }
        }
    }

    // Getters and Setters
    public UUID getUuid() { return uuid; }
    public int getCursedEnergy() { return cursedEnergy; }
    public void setCursedEnergy(int cursedEnergy) { this.cursedEnergy = cursedEnergy; }
    public int getMaxCursedEnergy() { return maxCursedEnergy; }
    public void setMaxCursedEnergy(int maxCursedEnergy) { this.maxCursedEnergy = maxCursedEnergy; }
    public TechniqueType getTechnique() { return technique; }
    public void setTechnique(TechniqueType technique) { this.technique = technique; }
    public String getGrade() { return jujutsuGrade; }
    public void setGrade(String grade) { this.jujutsuGrade = grade; applyGradeBuffs(); }

    public void setCooldown(String key, int seconds) {
        cooldowns.put(key.toLowerCase(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public int getRemainingCooldown(String key) {
        Long expire = cooldowns.get(key.toLowerCase());
        if (expire == null) return 0;
        long rem = expire - System.currentTimeMillis();
        return rem <= 0 ? 0 : (int) Math.ceil(rem / 1000.0);
    }
}
