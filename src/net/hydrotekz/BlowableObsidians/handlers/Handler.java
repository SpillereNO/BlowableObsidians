package net.hydrotekz.BlowableObsidians.handlers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

import net.hydrotekz.BlowableObsidians.BlowablePlugin;

public class Handler {

	BlowablePlugin plugin;

	public Handler(BlowablePlugin blowablePlugin){
		plugin = blowablePlugin;
	}

	public double getHealth(Block b){
		Material m = b.getType();
		Map<String, Object> blowableBlocks = plugin.getConfig().getConfigurationSection("Blowable Blocks Health").getValues(false);
		blowableBlocks = lowerMapKeys(blowableBlocks);
		double damage = (double) blowableBlocks.get(m.toString().toLowerCase());
		return damage;
	}

	public boolean makeBlowable(Block b){
		if (b.getType() == Material.BEDROCK) {
			int bedrockProtectionLevel = plugin.getConfig().getInt("Bedrock protection");
			if (!b.getWorld().getName().endsWith("_nether") && !b.getWorld().getName().endsWith("_the_end")) {
				if (b.getY() <= bedrockProtectionLevel) {
					return false;
				}
			} else if (b.getWorld().getName().endsWith("_nether")) {
				if (b.getY() == 127) {
					return false;
				}
			}
		}

		Material m = b.getType();
		Map<String, Object> blowableBlocks = plugin.getConfig().getConfigurationSection("Blowable Blocks Health").getValues(false);
		blowableBlocks = lowerMapKeys(blowableBlocks);
		return blowableBlocks.containsKey(m.toString().toLowerCase());
	}

	public boolean landUpon(Block b) {
		Material m = b.getType();
		List<String> blocks = plugin.getConfig().getStringList("Falling Blocks Land.Upon Blocks");
		blocks = lowerStringList(blocks);
		return blocks.contains(m.toString().toLowerCase());
	}

	public String getID(Block b){
		StringBuilder sb = new StringBuilder();
		sb.append(b.getWorld().getName() + " ");
		sb.append(b.getX() + " ");
		sb.append(b.getY() + " ");
		sb.append(b.getZ());
		return sb.toString();
	}

	public void exportConfig() {
		try {
			if (plugin.getConfig().getInt("Config Version") < plugin.configVersion){
				URL inputUrl = plugin.getClass().getResource("/config.yml");
				File dest = new File(plugin.getDataFolder() + File.separator + "config.yml");

				if (dest.exists()){
					File renameTo = new File(dest.getParent() + File.separator + "old_config.yml");
					if (renameTo.exists()) renameTo.delete();
					dest.renameTo(renameTo);
					System.out.println("[BlowableObsidians] Previous configuration file was renamed to old_config.yml.");
				}

				FileUtils.copyURLToFile(inputUrl, dest);
				System.out.println("[BlowableObsidians] Configuration file was successfully exported to plugin folder.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Map<String, Object> lowerMapKeys(Map<String, Object> map){
		Map<String, Object> output = new HashMap<String, Object>();
		for (Entry<String, Object> e : map.entrySet()){
			output.put(e.getKey().toLowerCase(), e.getValue());
		}
		return output;
	}

	private List<String> lowerStringList(List<String> list){
		List<String> output = new ArrayList<String>();
		for (String item : list){
			output.add(item.toLowerCase());
		}
		return output;
	}
}