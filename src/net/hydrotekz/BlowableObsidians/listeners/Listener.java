package net.hydrotekz.BlowableObsidians.listeners;

import java.util.List;
import java.util.Optional;

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
import net.hydrotekz.BlowableObsidians.handlers.ConfigHandler;
import net.hydrotekz.BlowableObsidians.model.DamagedBlock;
import net.hydrotekz.BlowableObsidians.support.MultiVersion;

public class Listener implements org.bukkit.event.Listener {

	private BlowablePlugin plugin;

	public Listener(BlowablePlugin blowablePlugin){
		plugin = blowablePlugin;
	}

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

		blocks.removeIf((block) -> ConfigHandler.makeBlowable(block));

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Location loc = new Location(source.getWorld(), x + source.getX(), y + source.getY(), z + source.getZ());
					if (source.distance(loc) <= dmgRadius) {
						Block block = loc.getBlock();
						if (ConfigHandler.makeBlowable(block)){

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

								DamagedBlock dmgBlock = new DamagedBlock(block);
								if (dmgBlock.damage(damage)) {
									blocks.add(block);
									DamagedBlock.clean(block);
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
			Block block = e.getBlock();
			DamagedBlock.clean(block);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent e){
		if (e.useInteractedBlock() == Result.ALLOW){
			Player p = e.getPlayer();
			if (e.getAction().toString().equalsIgnoreCase(plugin.getConfig().getString("Check.Type"))){
				String required = plugin.getConfig().getString("Check.Item");
				if (required.equals("*") || (MultiVersion.get().getItemInHand(p) != null && MultiVersion.get().getItemInHand(p).getType().toString().equalsIgnoreCase(required))){
					Block block = e.getClickedBlock();
					Optional<DamagedBlock> optDmgBlock = DamagedBlock.get(block);
					if (optDmgBlock.isPresent()){
						DamagedBlock dmgBlock = optDmgBlock.get();
						int percent = (int)(((dmgBlock.getHealth() * 100) / ConfigHandler.getDefaultHealth(block.getType())));
						int health = (int) Math.round(dmgBlock.getHealth());
						String msg = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Message.Block Health")
								.replaceFirst("<percent>", String.valueOf(percent))
								.replaceFirst("<health>", String.valueOf(health)));
						p.sendMessage(msg);
					} else if (ConfigHandler.makeBlowable(block) && plugin.getConfig().getBoolean("Always Send Health")){
						int health = (int) Math.round(ConfigHandler.getDefaultHealth(block.getType()));
						String msg = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Message.Block Health")
								.replaceFirst("<percent>", "100")
								.replaceFirst("<health>", String.valueOf(health)));
						p.sendMessage(msg);
					}
				}
			}
		}
	}
}