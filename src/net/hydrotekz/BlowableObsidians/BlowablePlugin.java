package net.hydrotekz.BlowableObsidians;

import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.hydrotekz.BlowableObsidians.handlers.*;
import net.hydrotekz.BlowableObsidians.listeners.*;

public class BlowablePlugin extends JavaPlugin {

	public FileConfiguration config;
	public int configVersion = 4;

	public final Listener Listener = new Listener(this);
	public final Handler Handler = new Handler(this);

	public void onEnable() {
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
	
	public int getRandomNumber(int start, int stop){
		Random r = new Random();
		int Low = start;
		int High = stop;
		int R = r.nextInt(High-Low) + Low;
		return R;
	}
}