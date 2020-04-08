package net.hydrotekz.BlowableObsidians.support;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mc_legacy extends MultiVersion implements VersionSupport {
	
	public mc_legacy() {
		super();
	}

	/*
	 * 1.7.* - 1.8.*
	 */
	
	@Override
	public ItemStack getItemInHand(Player player) {
		return player.getInventory().getItemInHand();
	}
}