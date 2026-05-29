package com.crimsonwarpedcraft.exampleplugin.data;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DomainManager {

    // Maps the Owner's UUID to the Center Location of their domain
    private final Map<UUID, Location> activeDomains = new HashMap<>();
    
    // The radius of the domain (matching DomainEngine)
    private final int DOMAIN_RADIUS = 10;

    /**
     * Registers a new domain in the system.
     */
    public void registerDomain(UUID owner, Location center) {
        activeDomains.put(owner, center);
    }

    /**
     * Removes a domain from the system.
     */
    public void unregisterDomain(UUID owner) {
        activeDomains.remove(owner);
    }

    /**
     * Checks if a player currently has an active domain expansion.
     * (Fixes your Gradle 'symbol not found' error)
     */
    public boolean hasActiveDomain(UUID owner) {
        return activeDomains.containsKey(owner);
    }

    /**
     * Gets the center location of a player's domain.
     */
    public Location getDomainLocation(UUID owner) {
        return activeDomains.get(owner);
    }

    /**
     * Checks if a specific location is inside a player's domain.
     * Used for the Sure-Hit cooldown buff and domain logic.
     */
    public boolean isInside(UUID owner, Location currentLocation) {
        Location center = activeDomains.get(owner);
        if (center == null) return false;
        
        // Ensure they are in the same world and within the radius
        if (!currentLocation.getWorld().equals(center.getWorld())) return false;
        
        return currentLocation.distance(center) <= DOMAIN_RADIUS;
    }

    /**
     * Checks if a new domain being cast overlaps with an existing one.
     * Useful for future 'Domain Clash' mechanics.
     */
    public UUID checkOverlap(Location newCenter, int radius) {
        for (Map.Entry<UUID, Location> entry : activeDomains.entrySet()) {
            if (entry.getValue().getWorld().equals(newCenter.getWorld())) {
                if (entry.getValue().distance(newCenter) < (radius * 2)) {
                    return entry.getKey(); // Returns the UUID of the rival domain owner
                }
            }
        }
        return null;
    }

    /**
     * Clears all domains (useful for plugin reloads/stops).
     */
    public void clearAll() {
        activeDomains.clear();
    }
}
