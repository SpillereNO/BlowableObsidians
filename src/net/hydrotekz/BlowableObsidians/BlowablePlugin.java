package net.hydrotekz.BlowableObsidians;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.hydrotekz.BlowableObsidians.handlers.*;
import net.hydrotekz.BlowableObsidians.listeners.*;

public class BlowablePlugin extends JavaPlugin {
	
	public static BlowablePlugin instance;

	public FileConfiguration config;
	public int configVersion = 8;
	public static int mc_version;
	public static String mc_protocol;

	public final Listener Listener = new Listener(this);
	public final Handler Handler = new Handler(this);

	public void onEnable() {
		String pck = getServer().getClass().getPackage().getName();
		mc_protocol = pck.substring(pck.lastIndexOf('.') + 1);
		String[] split = mc_protocol.replaceFirst("v", "").split("_");
		mc_version = Integer.parseInt(split[0] + split[1]);
		System.out.println("BlowableObsidians running on server with " + mc_version + " / " + mc_protocol + ".");

		instance = this;
		loadListeners();
		getDataFolder().mkdir();
		loadConfig();
	}

	public void loadListeners() {
		getServer().getPluginManager().registerEvents(Listener, this);
	}

	private void loadConfig() {
		config = getConfig();
		config.options().copyDefaults(true);
		config.addDefault("Config Version", 0);
		Handler.exportConfig();
	}
}