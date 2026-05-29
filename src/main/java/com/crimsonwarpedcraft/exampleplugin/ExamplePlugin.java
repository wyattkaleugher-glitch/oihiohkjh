package com.crimsonwarpedcraft.exampleplugin;

import com.crimsonwarpedcraft.exampleplugin.commands.TechniqueCommands;
import com.crimsonwarpedcraft.exampleplugin.data.DomainManager;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.listeners.CombatListener;
import com.crimsonwarpedcraft.exampleplugin.listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class ExamplePlugin extends JavaPlugin {

    private ProfileManager profileManager;
    private DomainManager domainManager;

    @Override
    public void onEnable() {
        // Create plugin folders
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File playerFolder = new File(getDataFolder(), "players");
        if (!playerFolder.exists()) playerFolder.mkdirs();

        this.profileManager = new ProfileManager(this);
        this.domainManager = new DomainManager();

        getCommand("jjk").setExecutor(new TechniqueCommands(this));
        getCommand("jjk").setTabCompleter(new TechniqueCommands(this));

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(this), this);

        getLogger().info("Jujutsu Kills enabled. Managers and Listeners started.");
    }

    @Override
    public void onDisable() {
        if (profileManager != null) profileManager.saveAll();
        if (domainManager != null) domainManager.clearAll();
    }

    public ProfileManager getProfileManager() { return profileManager; }
    public DomainManager getDomainManager() { return domainManager; }
}
