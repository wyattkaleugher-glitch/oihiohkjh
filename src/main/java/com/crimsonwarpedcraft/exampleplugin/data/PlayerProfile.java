package com.crimsonwarpedcraft.exampleplugin.data;

import java.util.UUID;

public class PlayerProfile {
    private final UUID uuid;
    private int cursedEnergy = 100; // Starts with 100 CE by default
    private int maxCursedEnergy = 250; 
    private TechniqueType technique = TechniqueType.NONE; // Default to no kit
    private boolean isBurnedOut = false;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }

    public int getCursedEnergy() { return cursedEnergy; }
    
    public void setCursedEnergy(int ce) { 
        // Ensures CE never goes below 0 or above their current maximum cap
        this.cursedEnergy = Math.max(0, Math.min(ce, this.maxCursedEnergy)); 
    }

    public int getMaxCursedEnergy() { return maxCursedEnergy; }
    public void setMaxCursedEnergy(int max) { this.maxCursedEnergy = max; }

    public TechniqueType getTechnique() { return technique; }
    public void setTechnique(TechniqueType technique) { this.technique = technique; }

    public boolean isBurnedOut() { return isBurnedOut; }
    public void setBurnedOut(boolean b) { this.isBurnedOut = b; }
}
