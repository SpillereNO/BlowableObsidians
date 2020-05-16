package net.hydrotekz.BlowableObsidians.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import net.hydrotekz.BlowableObsidians.BlowablePlugin;
import net.hydrotekz.BlowableObsidians.handlers.ConfigHandler;

public class WitherListener implements Listener {

	private BlowablePlugin plugin;

	public WitherListener(BlowablePlugin blowablePlugin){
		plugin = blowablePlugin;
	}

	private List<LivingEntity> entities = new ArrayList<>();

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWitherGrief(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof Wither)) return;
		Block block = plugin.Listener.damageBlock(event.getBlock(), null, ConfigHandler.getWitherMoveDamage());
		if (block == null) {
			event.setCancelled(true);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWitherSpawn(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof Wither)) return;
		Wither wither = (Wither) event.getEntity();
		entities.add(wither);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			wither.remove();
			entities.remove(wither);
		}, plugin.getConfig().getInt("Wither Lifetime")*20);
	}

	public void onDisable() {
		cleanUpEntities();
	}

	private void cleanUpEntities() {
		entities.forEach(entity -> {
			entity.setRemoveWhenFarAway(true);
		});
	}
}