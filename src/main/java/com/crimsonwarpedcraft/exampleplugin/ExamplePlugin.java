package com.crimsonwarpedcraft.exampleplugin;

import com.crimsonwarpedcraft.exampleplugin.commands.TechniqueCommands;
import com.crimsonwarpedcraft.exampleplugin.data.DomainManager;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.listeners.CombatListener;
import com.crimsonwarpedcraft.exampleplugin.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    private ProfileManager profileManager;
    private DomainManager domainManager; // Added tracker reference slot

    @Override
    public void onEnable() {
        this.profileManager = new ProfileManager(this);
        this.domainManager = new DomainManager(); // Instantiated tracker
        
        saveDefaultConfig();

        if (getCommand("jjk") != null) {
            TechniqueCommands handler = new TechniqueCommands(this);
            getCommand("jjk").setExecutor(handler);
            getCommand("jjk").setTabCompleter(handler);
        }

        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        
        getLogger().info("JJK Plugin Core fully initialized with Domain Clash tracking!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JJK Plugin Core safely suspended.");
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public DomainManager getDomainManager() {
        return domainManager;
    }
}
