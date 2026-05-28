package com.crimsonwarpedcraft.exampleplugin;

import com.crimsonwarpedcraft.exampleplugin.commands.TechniqueCommands;
import com.crimsonwarpedcraft.exampleplugin.data.ProfileManager;
import com.crimsonwarpedcraft.exampleplugin.listeners.CombatListener;
import com.crimsonwarpedcraft.exampleplugin.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        this.profileManager = new ProfileManager();
        
        saveDefaultConfig();

        if (getCommand("jjk") != null) {
            TechniqueCommands handler = new TechniqueCommands(this);
            getCommand("jjk").setExecutor(handler);
            getCommand("jjk").setTabCompleter(handler);
        }

        // Register both the Combat core AND the brand new Join setup!
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        
        getLogger().info("JJK Plugin Core fully initialized. First-join mechanics are active!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JJK Plugin Core safely suspended.");
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
