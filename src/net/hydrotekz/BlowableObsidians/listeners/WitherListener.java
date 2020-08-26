package net.hydrotekz.BlowableObsidians.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import net.hydrotekz.BlowableObsidians.BlowablePlugin;
import net.hydrotekz.BlowableObsidians.handlers.ConfigHandler;

public class WitherListener implements Listener {

	private BlowablePlugin plugin;

	public WitherListener(BlowablePlugin blowablePlugin){
		plugin = blowablePlugin;
	}

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWitherGrief(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof Wither)) return;
		Block block = plugin.Listener.damageBlock(event.getBlock(), null, ConfigHandler.getWitherMoveDamage());
		if (block == null) {
			event.setCancelled(true);
		}
	}
}