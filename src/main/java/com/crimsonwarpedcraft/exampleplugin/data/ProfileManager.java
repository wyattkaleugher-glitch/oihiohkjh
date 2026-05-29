package com.crimsonwarpedcraft.exampleplugin.data;

import com.crimsonwarpedcraft.exampleplugin.ExamplePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
        if (!profiles.containsKey(uuid)) {
            loadProfile(uuid);
        }
        return profiles.get(uuid);
    }

    public void loadProfile(UUID uuid) {
        File file = new File(plugin.getDataFolder() + "/players", uuid + ".yml");
        PlayerProfile profile = new PlayerProfile(uuid);

        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            profile.setCursedEnergy(config.getInt("ce"));
            profile.setMaxCursedEnergy(config.getInt("max-ce"));
            profile.setTechnique(TechniqueType.valueOf(config.getString("technique")));
            profile.setGrade(config.getString("grade"));
        }
        profiles.put(uuid, profile);
    }

    public void saveProfile(UUID uuid) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile == null) return;

        File file = new File(plugin.getDataFolder() + "/players", uuid + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("ce", profile.getCursedEnergy());
        config.set("max-ce", profile.getMaxCursedEnergy());
        config.set("technique", profile.getTechnique().name());
        config.set("grade", profile.getGrade());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        for (UUID uuid : profiles.keySet()) {
            saveProfile(uuid);
        }
    }

    public void unloadProfile(UUID uuid) {
        saveProfile(uuid);
        profiles.remove(uuid);
    }
}
