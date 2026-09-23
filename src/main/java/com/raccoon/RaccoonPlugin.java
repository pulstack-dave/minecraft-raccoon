package com.raccoon;

import com.raccoon.ai.RaccoonAITask;
import com.raccoon.command.RaccoonCommand;
import com.raccoon.config.RaccoonConfig;
import com.raccoon.entity.RaccoonEgg;
import com.raccoon.entity.RaccoonManager;
import com.raccoon.listener.RaccoonListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

/**
 * Main plugin class for MinecraftRaccoon.
 */
public class RaccoonPlugin extends JavaPlugin {

    private static RaccoonPlugin instance;
    private RaccoonConfig raccoonConfig;
    private RaccoonManager raccoonManager;
    private RaccoonEgg raccoonEgg;
    private RaccoonAITask aiTask;

    public RaccoonPlugin() {
        super();
    }

    public RaccoonPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        instance = this;

        // 1. Config
        this.raccoonConfig = new RaccoonConfig(this);
        this.raccoonConfig.load();

        // 2. Managers
        this.raccoonManager = new RaccoonManager(this);
        this.raccoonEgg = new RaccoonEgg(this);
        this.raccoonEgg.registerRecipe();

        // 3. Listeners
        getServer().getPluginManager().registerEvents(new RaccoonListener(this), this);

        // 4. Commands
        PluginCommand cmd = getCommand("raccoon");
        if (cmd != null) {
            RaccoonCommand raccoonCommand = new RaccoonCommand(this);
            cmd.setExecutor(raccoonCommand);
            cmd.setTabCompleter(raccoonCommand);
        }

        // 5. Periodic AI Task
        int interval = Math.max(5, raccoonConfig.getHuntIntervalTicks());
        this.aiTask = new RaccoonAITask(this);
        this.aiTask.runTaskTimer(this, 20L, interval);

        getLogger().info("=========================================");
        getLogger().info(" Minecraft Raccoon Plugin v" + getDescription().getVersion() + " Enabled!");
        getLogger().info(" Raccoons hatch from eggs, grow up, hunt animals, and love humans!");
        getLogger().info("=========================================");
    }

    @Override
    public void onDisable() {
        if (aiTask != null) {
            aiTask.cancel();
            aiTask = null;
        }
        getLogger().info("Minecraft Raccoon Plugin disabled.");
    }

    public static RaccoonPlugin getInstance() {
        return instance;
    }

    public RaccoonConfig getRaccoonConfig() {
        return raccoonConfig;
    }

    public RaccoonManager getRaccoonManager() {
        return raccoonManager;
    }

    public RaccoonEgg getRaccoonEgg() {
        return raccoonEgg;
    }
}
