package com.crimsonwarpedcraft.exampleplugin.data;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DomainManager {
    private final Map<UUID, Location> activeDomains = new HashMap<>();
    private final int RADIUS = 10;

    public void registerDomain(UUID id, Location loc) { activeDomains.put(id, loc); }
    public void unregisterDomain(UUID id) { activeDomains.remove(id); }
    public boolean hasActiveDomain(UUID id) { return activeDomains.containsKey(id); }
    
    public boolean isInside(UUID id, Location current) {
        Location center = activeDomains.get(id);
        if (center == null || !current.getWorld().equals(center.getWorld())) return false;
        return current.distance(center) <= RADIUS;
    }
    
    public void clearAll() { activeDomains.clear(); }
}
