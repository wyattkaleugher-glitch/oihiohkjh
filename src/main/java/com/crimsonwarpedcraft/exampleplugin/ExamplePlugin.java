package com.crimsonwarpedcraft.exampleplugin;

import com.crimsonwarpedcraft.exampleplugin.commands.TechniqueCommands;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.listeners.CombatListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        this.profileManager = new ProfileManager();
        
        saveDefaultConfig();

        // Register the class as both the Command Executor AND the Tab Completer
        if (getCommand("jjk") != null) {
            TechniqueCommands handler = new TechniqueCommands(this);
            getCommand("jjk").setExecutor(handler);
            getCommand("jjk").setTabCompleter(handler);
        }

        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getLogger().info("JJK Plugin Core fully initialized with custom Tab-Completion suggestions!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JJK Plugin Core safely suspended.");
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
