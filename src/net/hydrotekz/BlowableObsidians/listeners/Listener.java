package net.hydrotekz.BlowableObsidians.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.hydrotekz.BlowableObsidians.BlowablePlugin;
import net.hydrotekz.BlowableObsidians.support.MultiVersion;

public class Listener implements org.bukkit.event.Listener {

	private BlowablePlugin plugin;

	public Listener(BlowablePlugin blowablePlugin){
		plugin = blowablePlugin;
	}

	private HashMap<String, Double> healthMap = new HashMap<String, Double>();

	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockExplode(BlockExplodeEvent e) {
		if (!e.isCancelled()){
			List<Block> result = onBoom(e.getBlock().getLocation(), e.getYield(), e.blockList());
			for (Block b : e.blockList()) {
				if (!result.contains(b)) {
					e.blockList().remove(b);
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityExplode(EntityExplodeEvent e) {
		if (!e.isCancelled() && e.getEntity() != null){
			if (plugin.getConfig().getBoolean("Only TNT") && e.getEntityType() != EntityType.PRIMED_TNT) return;
			List<Block> result = onBoom(e.getLocation(), e.getYield(), e.blockList());
			for (Block b : e.blockList()) {
				if (!result.contains(b)) {
					e.blockList().remove(b);
				}
			}
		}
	}

	private List<Block> onBoom(Location source, float yield, List<Block> blocks) {
		double dmgRadius = plugin.getConfig().getDouble("Damage Radius");
		if (yield > 1) dmgRadius+=yield/10;
		int radius = (int)Math.ceil(dmgRadius);

		for (Block b : new ArrayList<Block>(blocks)){
			if (plugin.Handler.makeBlowable(b)){
				blocks.remove(b);
			}
		}

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Location loc = new Location(source.getWorld(), x + source.getX(), y + source.getY(), z + source.getZ());
					if (source.distance(loc) <= dmgRadius) {
						Block block = loc.getBlock();
						if (plugin.Handler.makeBlowable(block)){

							// Get distance and damage
							double distance = loc.distance(source);
							double damage = 1;

							// Yield
							if (yield > 0.5) damage+=1;
							else if (yield > 8) damage+=2;
							else if (yield > 16) damage+=3;
							else if (yield > 22) damage+=4;
							else if (yield > 28) damage+=5;

							// Check if source is liquid
							if (source.getBlock().isLiquid()){
								damage = damage * plugin.getConfig().getDouble("Liquid Multiplier");
							}

							// Damage the block
							if (damage > 0){
								if (distance > 1) damage = damage / (distance * 0.7);

								String id = plugin.Handler.getID(block);
								if (healthMap.containsKey(id)) healthMap.put(id, healthMap.get(id)-damage);
								else healthMap.put(id, plugin.Handler.getHealth(block)-damage);
								if (healthMap.get(id) <= 0){
									blocks.add(block);
									healthMap.remove(id);
								}
							}
						}
					}
				}
			}
		}
		return blocks;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e){
		if (!e.isCancelled()){
			String id = plugin.Handler.getID(e.getBlock());
			healthMap.remove(id);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent e){
		if (e.useInteractedBlock() == Result.ALLOW){
			Player p = e.getPlayer();
			if (e.getAction().toString().equalsIgnoreCase(plugin.getConfig().getString("Check.Type"))){
				String required = plugin.getConfig().getString("Check.Item");
				if (required.equals("*") || (MultiVersion.get().getItemInHand(p) != null && p.getItemInHand().getType().toString().equalsIgnoreCase(required))){
					Block b = e.getClickedBlock();
					String id = plugin.Handler.getID(b);
					if (healthMap.containsKey(id)){
						int percent = (int)(((healthMap.get(id) * 100) / plugin.Handler.getHealth(b)));
						String msg = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Message.Block health").replaceFirst("<percent>", String.valueOf(percent)));
						p.sendMessage(msg);
					} else if (plugin.Handler.makeBlowable(b) && plugin.getConfig().getBoolean("Always Send Health")){
						String msg = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Message.Block health").replaceFirst("<percent>", "100"));
						p.sendMessage(msg);
					}
				}
			}
		}
	}
}