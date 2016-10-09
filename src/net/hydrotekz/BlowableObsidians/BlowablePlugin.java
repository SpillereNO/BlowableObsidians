package net.hydrotekz.BlowableObsidians;

import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.hydrotekz.BlowableObsidians.handlers.*;
import net.hydrotekz.BlowableObsidians.listeners.*;

public class BlowablePlugin extends JavaPlugin {

	public FileConfiguration config;

	public final Listener Listener = new Listener(this);
	public final Handler Handler = new Handler(this);

	public void onEnable() {
		loadListeners();
		getDataFolder().mkdir();
		loadConfig();
		saveConfig();
	}

	public void loadListeners() {
		getServer().getPluginManager().registerEvents(Listener, this);
	}

	private void loadConfig() {
		config = getConfig();
		config.options().copyDefaults(true);
		config.addDefault("Config Version", 3);
		config.addDefault("Damage Radius", 3.0);
		config.addDefault("Block Health", 5.0);
		config.addDefault("Only TNT", false);
		config.addDefault("Scan Through Blocks", false);
		config.addDefault("Liquid Multiplier", 0.0);
		config.addDefault("Check.Item", "*");
		config.addDefault("Check.Type", "LEFT_CLICK_BLOCK");
		config.addDefault("Always Send Health", false);
		config.addDefault("Message.Block health", "&9Block health: &c<percent>% hp");
		int[] ids = {116, 26, 54, 130, 146, 12};
		config.addDefault("Falling Blocks Land.Enabled", true);
		config.addDefault("Falling Blocks Land.Upon Blocks", ids);
	}
	
	public int getRandomNumber(int start, int stop){
		Random r = new Random();
		int Low = start;
		int High = stop;
		int R = r.nextInt(High-Low) + Low;
		return R;
	}
}