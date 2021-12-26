package net.hydrotekz.BlowableObsidians;

import com.google.common.collect.Lists;
import net.hydrotekz.BlowableObsidians.handlers.ConfigHandler;
import net.hydrotekz.BlowableObsidians.handlers.ReloadCommand;
import net.hydrotekz.BlowableObsidians.listeners.Listener;
import net.hydrotekz.BlowableObsidians.listeners.WitherListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class BlowablePlugin extends JavaPlugin {

    public static BlowablePlugin instance;

    public FileConfiguration config;
    public int configVersion = 12;
    public static int mc_version;
    public static String mc_protocol;

    public final Listener Listener = new Listener(this);
    public final WitherListener WitherListener = new WitherListener(this);

    public void onEnable() {
        String pck = getServer().getClass().getPackage().getName();
        mc_protocol = pck.substring(pck.lastIndexOf('.') + 1);
        String[] split = mc_protocol.replaceFirst("v", "").split("_");
        mc_version = Integer.parseInt(split[0] + split[1]);
        getLogger().info("Running on server with " + mc_version + " / " + mc_protocol + ".");

        instance = this;
        loadListeners();
        getDataFolder().mkdir();
        loadConfig();
        registerCommands();
    }

    public void loadListeners() {
        getServer().getPluginManager().registerEvents(Listener, this);
        getServer().getPluginManager().registerEvents(WitherListener, this);
    }

    private void loadConfig() {
        config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("Config Version", 0);
        ConfigHandler.exportConfig();
    }

    private void registerCommands() {
        registerCommand("blowable", new ReloadCommand(), "blowableobsidian", "blowableobsidians");
    }

    public void registerCommand(String name, CommandExecutor executor, String... aliases) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand command = constructor.newInstance(name, this);

            command.setExecutor(executor);
            command.setAliases(Lists.newArrayList(aliases));
            if (executor instanceof TabCompleter) {
                command.setTabCompleter((TabCompleter) executor);
            }
            this.getCommandMap().register("blowableobsidians", command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CommandMap getCommandMap() {
        try {
            org.bukkit.Server server = Bukkit.getServer();
            Field commandMap = server.getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);
            return (CommandMap) commandMap.get(server);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}