package com.crimsonwarpedcraft.exampleplugin.data;

import java.util.UUID;

public class PlayerProfile {
    private final UUID uuid;
    private int cursedEnergy = 100; // Starts with 100 CE by default
    private int maxCursedEnergy = 250; // Standard baseline cap
    private TechniqueType technique = TechniqueType.NONE; // Default to no kit
    private boolean isBurnedOut = false;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { 
        return this.uuid; 
    }

    public int getCursedEnergy() { 
        return this.cursedEnergy; 
    }
    
    public void setCursedEnergy(int ce) { 
        // Prevents energy from dipping below 0 or exceeding the max allowed limit
        this.cursedEnergy = Math.max(0, Math.min(ce, this.maxCursedEnergy)); 
    }

    public int getMaxCursedEnergy() { 
        return this.maxCursedEnergy; 
    }
    
    public void setMaxCursedEnergy(int max) { 
        this.maxCursedEnergy = max; 
    }

    public TechniqueType getTechnique() { 
        return this.technique; 
    }
    
    public void setTechnique(TechniqueType technique) { 
        this.technique = technique; 
    }

    public boolean isBurnedOut() { 
        return this.isBurnedOut; 
    }
    
    public void setBurnedOut(boolean b) { 
        this.isBurnedOut = b; 
    }
}
