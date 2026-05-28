package com.crimsonwarpedcraft.exampleplugin.data;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DomainManager {

    // Tracks the center location of every active domain by the caster's UUID
    private final Map<UUID, Location> activeDomains = new HashMap<>();

    public void registerDomain(UUID casterUuid, Location center) {
        activeDomains.put(casterUuid, center);
    }

    public void unregisterDomain(UUID casterUuid) {
        activeDomains.remove(casterUuid);
    }

    /**
     * Checks if a new domain center overlaps with an already existing domain.
     * @return The UUID of the rival caster if an overlap occurs, otherwise null.
     */
    public UUID checkOverlap(Location newCenter, int radius) {
        for (Map.Entry<UUID, Location> entry : activeDomains.entrySet()) {
            Location existingCenter = entry.getValue();
            
            // Check if they are in the same world before running distance math
            if (!existingCenter.getWorld().equals(newCenter.getWorld())) continue;

            // Mathematical distance check between the two domain centers
            double distance = existingCenter.distance(newCenter);
            
            // If the centers are closer than double the radius, their barriers clash!
            if (distance < (radius * 2)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
