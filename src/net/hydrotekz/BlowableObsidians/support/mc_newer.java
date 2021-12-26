package net.hydrotekz.BlowableObsidians.support;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class mc_newer extends MultiVersion implements VersionSupport {

    public mc_newer() {
        super();
    }

    /*
     * 1.9.* - 1.15.*
     */

    @Override
    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

}