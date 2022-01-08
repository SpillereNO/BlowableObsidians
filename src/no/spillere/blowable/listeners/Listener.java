package no.spillere.blowable.listeners;

import no.spillere.blowable.BlowablePlugin;
import no.spillere.blowable.handlers.ConfigHandler;
import no.spillere.blowable.model.DamagedBlock;
import no.spillere.blowable.support.MultiVersion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Optional;

public class Listener implements org.bukkit.event.Listener {

    private BlowablePlugin plugin;

    public Listener(BlowablePlugin blowablePlugin) {
        plugin = blowablePlugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        List<Block> result = onBoom(e.getBlock().getLocation(), e.blockList(), ConfigHandler.getDefaultDamage(null), ConfigHandler.getDefaultRadius(null));
        for (Block b : e.blockList()) {
            if (!result.contains(b)) {
                e.blockList().remove(b);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() == null) return;
        List<Block> result = onBoom(e.getLocation(), e.blockList(), ConfigHandler.getDefaultDamage(e.getEntityType()), ConfigHandler.getDefaultRadius(e.getEntityType()));
        for (Block b : e.blockList()) {
            if (!result.contains(b)) {
                e.blockList().remove(b);
            }
        }
    }

    private List<Block> onBoom(Location source, List<Block> blocks, double damage, double dmgRadius) {
        int radius = (int) Math.ceil(dmgRadius);

        blocks.removeIf((block) -> ConfigHandler.makeBlowable(block));

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = new Location(source.getWorld(), x + source.getX(), y + source.getY(), z + source.getZ());
                    if (source.distance(loc) <= dmgRadius) {
                        Block block = damageBlock(loc.getBlock(), source, damage);
                        if (block != null) blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public Block damageBlock(Block block, Location source, double damage) {
        if (ConfigHandler.makeBlowable(block)) {

            // Check if source is liquid
            if (source != null && source.getBlock().isLiquid()) {
                damage = damage * plugin.getConfig().getDouble("Liquid Multiplier");
            }

            // Damage the block
            if (damage > 0) {

                DamagedBlock dmgBlock = new DamagedBlock(block);
                if (dmgBlock.damage(damage)) {
                    DamagedBlock.clean(block);
                    return block;
                }
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.isCancelled()) {
            Block block = e.getBlock();
            DamagedBlock.clean(block);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.useInteractedBlock() != Result.ALLOW) return;
        Player p = e.getPlayer();
        if (e.getAction().toString().equalsIgnoreCase(plugin.getConfig().getString("Check.Type"))) {
            String required = plugin.getConfig().getString("Check.Item");
            if (required.equals("*") || (MultiVersion.get().getItemInHand(p) != null && MultiVersion.get().getItemInHand(p).getType().toString().equalsIgnoreCase(required))) {
                Block block = e.getClickedBlock();
                Optional<DamagedBlock> optDmgBlock = DamagedBlock.get(block);
                if (optDmgBlock.isPresent()) {
                    DamagedBlock dmgBlock = optDmgBlock.get();
                    int percent = (int) (((dmgBlock.getHealth() * 100) / ConfigHandler.getDefaultHealth(block.getType())));
                    int health = (int) Math.round(dmgBlock.getHealth());
                    String msg = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Message.Block Health")
                            .replaceFirst("<percent>", String.valueOf(percent))
                            .replaceFirst("<health>", String.valueOf(health)));
                    p.sendMessage(msg);
                } else if (ConfigHandler.makeBlowable(block) && plugin.getConfig().getBoolean("Always Send Health")) {
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