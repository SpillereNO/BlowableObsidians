package net.hydrotekz.BlowableObsidians.handlers;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import net.hydrotekz.BlowableObsidians.BlowablePlugin;
import net.hydrotekz.BlowableObsidians.util.Util;

public class ConfigHandler {

	public static double getDefaultHealth(Material mat){
		Map<String, Object> blowableBlocks = BlowablePlugin.instance.getConfig().getConfigurationSection("Blocks Health").getValues(false);
		blowableBlocks = lowerMapKeys(blowableBlocks);
		double damage = 1.0;

		if (blowableBlocks.containsKey(mat.toString().toLowerCase()))
			damage = (double) ((Double)blowableBlocks.get(mat.toString().toLowerCase())).doubleValue();

		else if (blowableBlocks.containsKey("<blocks>") && mat != Material.LAVA && mat != Material.WATER && mat != Material.AIR)
			damage = (double) ((Double)blowableBlocks.get("<blocks>")).doubleValue();

		return damage;
	}

	public static int getRegenTime() {
		return BlowablePlugin.instance.getConfig().getInt("Regeneration Time");
	}

	public static double getWitherMoveDamage() {

		FileConfiguration config = BlowablePlugin.instance.getConfig();

		if (config.contains("Explosion Settings.Wither Block Eating.Damage")) {
			return config.getDouble("Explosion Settings.Wither Block Eating.Damage");
		} else {
			return config.getDouble("Explosion Settings.Default.Damage");
		}

	}

	public static double getDefaultRadius(EntityType type){
		return getExplosionSetting(type, "Radius");
	}

	public static double getDefaultDamage(EntityType type) {
		return getExplosionSetting(type, "Damage");
	}

	private static double getExplosionSetting(EntityType type, String setting){

		FileConfiguration config = BlowablePlugin.instance.getConfig();

		if (type == null) {
			if (config.contains("Explosion Settings.Custom Explosions." + setting)) {
				return config.getDouble("Explosion Settings.Custom Explosions." + setting);
			} else {
				return config.getDouble("Explosion Settings.Default." + setting);
			}
		}

		double damage;

		switch (type) {
		default:
			damage = config.getDouble("Explosion Settings.Default." + setting);
		case PRIMED_TNT:
			damage = config.getDouble("Explosion Settings.TNT." + setting);
			break;
		case CREEPER:
			damage = config.getDouble("Explosion Settings.Creeper." + setting);
			break;
		case WITHER:
			damage = config.getDouble("Explosion Settings.Wither Creation." + setting);
			break;
		case WITHER_SKULL:
			damage = config.getDouble("Explosion Settings.Wither Projectile." + setting);
			break;
		}

		return damage;
	}

	public static boolean makeBlowable(Block b){

		if (b.getType() == Material.BEDROCK) {
			int bedrockProtectionLevel = BlowablePlugin.instance.getConfig().getInt("Bedrock Protection");
			if (b.getWorld().getEnvironment() == Environment.NORMAL) {
				if (b.getY() <= bedrockProtectionLevel) {
					return false;
				}
			} else if (b.getWorld().getEnvironment() == Environment.NETHER) {
				if (b.getY() == 127 || b.getY() <= bedrockProtectionLevel) {
					return false;
				}
			}
		}

		Material m = b.getType();
		Map<String, Object> blowableBlocks = BlowablePlugin.instance.getConfig().getConfigurationSection("Blocks Health").getValues(false);
		blowableBlocks = lowerMapKeys(blowableBlocks);
		return blowableBlocks.containsKey(m.toString().toLowerCase())
				|| (!b.isLiquid() && b.getType() != Material.AIR && blowableBlocks.containsKey("<blocks>"));
	}

	public static void exportConfig() {
		try {
			if (BlowablePlugin.instance.getConfig().getInt("Config Version") < BlowablePlugin.instance.configVersion){
				URL inputUrl = BlowablePlugin.instance.getClass().getResource("/config.yml");
				File dest = new File(BlowablePlugin.instance.getDataFolder() + File.separator + "config.yml");

				if (dest.exists()){
					File renameTo = new File(dest.getParent() + File.separator + "old_config.yml");
					if (renameTo.exists()) renameTo.delete();
					dest.renameTo(renameTo);
					BlowablePlugin.instance.getLogger().info("Previous configuration file was renamed to old_config.yml.");
				}

				Util.copyUrlToFile(inputUrl, dest);

				BlowablePlugin.instance.getLogger().info("Configuration file was successfully exported to plugin folder.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Map<String, Object> lowerMapKeys(Map<String, Object> map){
		Map<String, Object> output = new HashMap<String, Object>();
		for (Entry<String, Object> e : map.entrySet()){
			String key = e.getKey().toLowerCase();
			key = key.replace(" ", "_");
			key = key.replace("wither_projectile", "wither_skull");
			if (key.equals("tnt")) key = key.replace("tnt", "primed_tnt");
			key = key.replace("wither_creation", "wither");
			output.put(key, e.getValue());
		}
		return output;
	}
}