package com.hudclient.waypoints;

import net.minecraft.util.math.BlockPos;

public class Waypoint {

    public String name;
    public int x, y, z;
    public int color;       // ARGB
    public String dimension; // "overworld" / "nether" / "the_end"
    public boolean enabled;

    public Waypoint() {} // for Gson

    public Waypoint(String name, int x, int y, int z, int color, String dimension) {
        this.name      = name;
        this.x         = x;
        this.y         = y;
        this.z         = z;
        this.color     = color;
        this.dimension = dimension;
        this.enabled   = true;
    }

    public double distanceTo(BlockPos pos) {
        double dx = x - pos.getX();
        double dy = y - pos.getY();
        double dz = z - pos.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public String getDisplayString(BlockPos playerPos) {
        int dist = (int) distanceTo(playerPos);
        return String.format("■ %s  [%d, %d, %d]  %dm", name, x, y, z, dist);
    }

    public int getColor() { return color; }

    @Override
    public String toString() {
        return String.format("Waypoint{%s @ %d,%d,%d}", name, x, y, z);
    }
}
