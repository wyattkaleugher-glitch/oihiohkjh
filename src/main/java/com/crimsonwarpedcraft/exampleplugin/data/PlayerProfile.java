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

    public void applyGradeBuffs() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;
        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.RESISTANCE);

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
            case "Grade 3" -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
        }
    }

    public void clearAllCooldowns() {
        this.cooldowns.clear();
    }

    public void reduceAllCooldowns(int seconds) {
        long reductionMillis = seconds * 1000L;
        for (Map.Entry<String, Long> entry : cooldowns.entrySet()) {
            cooldowns.put(entry.getKey(), entry.getValue() - reductionMillis);
        }
    }

    public void setCooldown(String key, int seconds) {
        cooldowns.put(key.toLowerCase(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public int getRemainingCooldown(String key) {
        Long expire = cooldowns.get(key.toLowerCase());
        if (expire == null) return 0;
        long rem = expire - System.currentTimeMillis();
        return rem <= 0 ? 0 : (int) Math.ceil(rem / 1000.0);
    }

    public UUID getUuid() { return uuid; }
    public int getCursedEnergy() { return cursedEnergy; }
    public void setCursedEnergy(int ce) { this.cursedEnergy = Math.max(0, ce); }
    public int getMaxCursedEnergy() { return maxCursedEnergy; }
    public void setMaxCursedEnergy(int max) { this.maxCursedEnergy = max; }
    public TechniqueType getTechnique() { return technique; }
    public void setTechnique(TechniqueType tech) { this.technique = tech; }
    public String getGrade() { return jujutsuGrade; }
    public void setGrade(String grade) { this.jujutsuGrade = grade; applyGradeBuffs(); }
}
