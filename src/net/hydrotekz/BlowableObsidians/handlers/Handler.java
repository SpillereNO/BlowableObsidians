package net.hydrotekz.BlowableObsidians.handlers;

import org.bukkit.Material;
import org.bukkit.block.Block;

import net.hydrotekz.BlowableObsidians.BlowablePlugin;

public class Handler {

	BlowablePlugin plugin;

	public Handler(BlowablePlugin blowablePlugin){
		plugin = blowablePlugin;
	}

	public double getDamage(Material m){
		double dmg = 1;
		if (m == Material.ENCHANTMENT_TABLE){
			dmg = dmg * 1.1;
		} else if (m == Material.ENDER_CHEST){
			dmg = dmg * 1.2;
		} else if (m == Material.ANVIL){
			dmg = dmg * 1.3;
		} else if (m == Material.OBSIDIAN){
			dmg = dmg * 1;
		}
		return dmg;
	}

	public boolean makeBlowable(Material m){
		return m == Material.OBSIDIAN || m == Material.ANVIL || m == Material.ENCHANTMENT_TABLE || m == Material.ENDER_CHEST;
	}

	public String getID(Block b){
		StringBuilder sb = new StringBuilder();
		sb.append(b.getWorld().getName() + " ");
		sb.append(b.getX() + " ");
		sb.append(b.getY() + " ");
		sb.append(b.getZ());
		return sb.toString();
	}
}