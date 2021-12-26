package net.hydrotekz.BlowableObsidians.model;

import net.hydrotekz.BlowableObsidians.handlers.ConfigHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DamagedBlock {

    private static List<DamagedBlock> blocks = Collections.synchronizedList(new ArrayList<DamagedBlock>());

    private String world;
    private int x;
    private int y;
    private int z;

    private double health;
    private double lastDamaged;
    private Material material;

    public DamagedBlock(Block b) {

        // Search for existing object
        Optional<DamagedBlock> existing = blocks.stream().filter(dmgBlock ->
                dmgBlock.getX() == b.getX()
                        && dmgBlock.getY() == b.getY()
                        && dmgBlock.getZ() == b.getZ()
                        && dmgBlock.getWorld().equals(b.getWorld().getName()))
                .findFirst();

        // Return existing object
        if (existing.isPresent()) {
            DamagedBlock dmgBlock = existing.get();
            world = dmgBlock.getWorld();
            x = dmgBlock.getX();
            y = dmgBlock.getY();
            z = dmgBlock.getZ();
            health = dmgBlock.getHealth();
            lastDamaged = dmgBlock.getLastDamaged();
            material = dmgBlock.getMaterial();
        }

        // Create and return new object
        else {
            world = b.getWorld().getName();
            x = b.getX();
            y = b.getY();
            z = b.getZ();
            health = ConfigHandler.getDefaultHealth(b.getType());
            lastDamaged = System.currentTimeMillis();
            material = b.getType();
            blocks.add(this);
        }
    }

    public static Optional<DamagedBlock> get(Block b) {
        return
                blocks.stream().filter(dmgBlock ->
                        dmgBlock.getX() == b.getX()
                                && dmgBlock.getY() == b.getY()
                                && dmgBlock.getZ() == b.getZ()
                                && dmgBlock.getWorld().equals(b.getWorld().getName()))
                        .findFirst();
    }

    public static void clean(Block b) {
        blocks.removeIf(dmgBlock ->
                dmgBlock.getX() == b.getX()
                        && dmgBlock.getY() == b.getY()
                        && dmgBlock.getZ() == b.getZ()
                        && dmgBlock.getWorld().equals(b.getWorld().getName()));
    }

    public boolean damage(double damage) {
        setHealth(getHealth() - damage);
        setLastDamaged(System.currentTimeMillis());
        blocks.remove(this);
        blocks.add(this);
        return (int) getHealth() <= 0;
    }

    public double getHealth() {
        double max = ConfigHandler.getDefaultHealth(material);
        double left = max - health;
        long now = System.currentTimeMillis();
        double health = this.health + (left * (((now - lastDamaged) / 1000) / ConfigHandler.getRegenTime()));
        return health >= max ? max : health;
    }

    private void setHealth(double health) {
        this.health = health;
    }

    public double getLastDamaged() {
        return lastDamaged;
    }

    private void setLastDamaged(double lastDamaged) {
        this.lastDamaged = lastDamaged;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DamagedBlock other = (DamagedBlock) obj;
        if (world == null) {
            if (other.world != null)
                return false;
        } else if (!world.equals(other.world))
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        if (z != other.z)
            return false;
        return true;
    }

}