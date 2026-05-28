package com.crimsonwarpedcraft.exampleplugin.data;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private final ExamplePlugin plugin;
    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    public ProfileManager(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    public PlayerProfile getProfile(UUID uuid) {
        return profiles.computeIfAbsent(uuid, k -> loadProfile(uuid));
    }

    // 💾 LOADS PLAYER VALUES FROM COMPILER MEMORY STORAGE
    private PlayerProfile loadProfile(UUID uuid) {
        PlayerProfile profile = new PlayerProfile(uuid);
        File file = new File(plugin.getDataFolder() + "/playerdata", uuid.toString() + ".yml");
        
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            profile.setCursedEnergy(config.getInt("cursed-energy", 1000));
            profile.setMaxCursedEnergy(config.getInt("max-cursed-energy", 1000));
            
            try {
                profile.setTechnique(TechniqueType.valueOf(config.getString("technique", "NONE")));
            } catch (Exception e) {
                profile.setTechnique(TechniqueType.NONE);
            }
            
            // Reads their custom assigned Jujutsu rank grade string
            profile.setGrade(config.getString("jujutsu-grade", "Grade 4"));
        } else {
            // Raw Default Initializations
            profile.setGrade("Grade 4");
        }
        return profile;
    }

    // 💾 SAVES THE STRUCTURAL DATA STACK TO STORAGE DISK
    public void saveProfile(UUID uuid) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile == null) return;

        File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File file = new File(dataFolder, uuid.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("cursed-energy", profile.getCursedEnergy());
        config.set("max-cursed-energy", profile.getMaxCursedEnergy());
        config.set("technique", profile.getTechnique().name());
        config.set("jujutsu-grade", profile.getGrade());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not freeze player data stack for: " + uuid);
        }
    }

    public void unloadProfile(UUID uuid) {
        saveProfile(uuid);
        profiles.remove(uuid);
    }
}
