package com.crimsonwarpedcraft.exampleplugin.data;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DomainManager {
    private final Map<UUID, Location> activeDomains = new HashMap<>();
    private final int RADIUS = 8;

    public void registerDomain(UUID id, Location loc) { activeDomains.put(id, loc); }
    public void unregisterDomain(UUID id) { activeDomains.remove(id); }
    public boolean hasActiveDomain(UUID id) { return activeDomains.containsKey(id); }

    public boolean isInsideAnyDomain(Location loc) {
        for (Location center : activeDomains.values()) {
            if (center.getWorld().equals(loc.getWorld())) {
                if (center.distance(loc) <= RADIUS + 1) return true;
            }
        }
        return false;
    }

    public void clearAll() { activeDomains.clear(); }
}
