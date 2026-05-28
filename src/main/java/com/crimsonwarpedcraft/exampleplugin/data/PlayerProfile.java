package com.crimsonwarpedcraft.exampleplugin.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProfile {

    private final UUID uuid;
    private int cursedEnergy;
    private int maxCursedEnergy;
    private TechniqueType technique;
    private String jujutsuGrade;
    
    // Tracks individual ability cooldowns using expiration timestamps
    private final Map<String, Long> cooldowns;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        this.cursedEnergy = 1000;
        this.maxCursedEnergy = 1000;
        this.technique = TechniqueType.NONE;
        this.jujutsuGrade = "Grade 4"; // Default starting rank milestone
        this.cooldowns = new HashMap<>();
    }

    // 🏆 GRADE ADVANCEMENT FLOW
    public void promoteToNextGrade() {
        // Defines the exact step-by-step ladder progression
        switch (this.jujutsuGrade) {
            case "Grade 4" -> this.jujutsuGrade = "Grade 3";
            case "Grade 3" -> this.jujutsuGrade = "Grade 2";
            case "Grade 2" -> this.jujutsuGrade = "Grade 1";
            case "Grade 1" -> this.jujutsuGrade = "Special Grade";
            default -> {
                // If they are already Special Grade or have a custom title, do not overwrite it
            }
        }
    }

    // 🔍 GETTERS & SETTERS: CORE FIELDS
    public UUID getUuid() {
        return uuid;
    }

    public int getCursedEnergy() {
        return cursedEnergy;
    }

    public void setCursedEnergy(int cursedEnergy) {
        // Keeps energy within safety boundaries (bounds: 0 to Max Limit)
        if (cursedEnergy < 0) {
            this.cursedEnergy = 0;
        } else if (cursedEnergy > this.maxCursedEnergy) {
            this.cursedEnergy = this.maxCursedEnergy;
        } else {
            this.cursedEnergy = cursedEnergy;
        }
    }

    public int getMaxCursedEnergy() {
        return maxCursedEnergy;
    }

    public void setMaxCursedEnergy(int maxCursedEnergy) {
        this.maxCursedEnergy = Math.max(0, maxCursedEnergy);
        // Secondary safety check: downscale active pool if it exceeds the new cap
        if (this.cursedEnergy > this.maxCursedEnergy) {
            this.cursedEnergy = this.maxCursedEnergy;
        }
    }

    public TechniqueType getTechnique() {
        return technique;
    }

    public void setTechnique(TechniqueType technique) {
        this.technique = technique != null ? technique : TechniqueType.NONE;
    }

    public String getGrade() {
        return jujutsuGrade;
    }

    public void setGrade(String grade) {
        this.jujutsuGrade = grade != null ? grade : "Grade 4";
    }

    // ⏱️ COOLDOWN ENGINE LOGIC ROUTINES
    public void setCooldown(String actionKey, int seconds) {
        if (seconds <= 0) {
            cooldowns.remove(actionKey.toLowerCase());
        } else {
            // Converts the expiration time into a permanent future millisecond stamp
            cooldowns.put(actionKey.toLowerCase(), System.currentTimeMillis() + (seconds * 1000L));
        }
    }

    public int getRemainingCooldown(String actionKey) {
        Long expireTime = cooldowns.get(actionKey.toLowerCase());
        if (expireTime == null) return 0;

        long remainingMillis = expireTime - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            cooldowns.remove(actionKey.toLowerCase()); // Cleanup memory profile leaks
            return 0;
        }

        // Round up to the nearest whole second parameter
        return (int) Math.ceil(remainingMillis / 1000.0);
    }

    public boolean hasCooldown(String actionKey) {
        return getRemainingCooldown(actionKey) > 0;
    }
}
