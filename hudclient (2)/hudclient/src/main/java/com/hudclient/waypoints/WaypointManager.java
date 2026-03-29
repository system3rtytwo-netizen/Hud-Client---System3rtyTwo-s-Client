package com.hudclient.waypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class WaypointManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("hudclient");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File WAYPOINT_FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("hudclient_waypoints.json").toFile();

    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();

    // Preset colors for new waypoints (cycles)
    private static final int[] COLORS = {
        0xFFFFAA00, 0xFF55FFFF, 0xFF55FF55, 0xFFFF5555,
        0xFFFF55FF, 0xFF5555FF, 0xFFFFFF55, 0xFFFFFFFF
    };
    private static int colorIndex = 0;

    public static void init() {
        load();
    }

    public static List<Waypoint> getAll() {
        return Collections.unmodifiableList(WAYPOINTS);
    }

    /**
     * Returns the N closest enabled waypoints in the current dimension,
     * sorted by distance ascending.
     */
    public static List<Waypoint> getWaypointsForDisplay(BlockPos playerPos, int maxCount) {
        String dim = getCurrentDimension();
        return WAYPOINTS.stream()
                .filter(w -> w.enabled && w.dimension.equals(dim))
                .sorted(Comparator.comparingDouble(w -> w.distanceTo(playerPos)))
                .limit(maxCount)
                .collect(Collectors.toList());
    }

    public static void addWaypointAtPlayer(ClientPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        String dim = getCurrentDimension();
        String name = "WP " + (WAYPOINTS.size() + 1);
        int color = COLORS[colorIndex % COLORS.length];
        colorIndex++;
        Waypoint wp = new Waypoint(name, pos.getX(), pos.getY(), pos.getZ(), color, dim);
        WAYPOINTS.add(wp);
        save();
        LOGGER.info("Added waypoint: {}", wp);
    }

    public static void addWaypoint(Waypoint wp) {
        WAYPOINTS.add(wp);
        save();
    }

    public static void removeWaypoint(int index) {
        if (index >= 0 && index < WAYPOINTS.size()) {
            WAYPOINTS.remove(index);
            save();
        }
    }

    public static void removeWaypoint(Waypoint wp) {
        WAYPOINTS.remove(wp);
        save();
    }

    public static void clear() {
        WAYPOINTS.clear();
        save();
    }

    private static String getCurrentDimension() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return "overworld";
        String id = mc.world.getRegistryKey().getValue().getPath();
        return id; // "overworld", "the_nether", "the_end"
    }

    public static void load() {
        if (!WAYPOINT_FILE.exists()) {
            // Add a couple of example waypoints
            WAYPOINTS.add(new Waypoint("Spawn", 0, 64, 0, 0xFF55FF55, "overworld"));
            save();
            return;
        }
        try (FileReader reader = new FileReader(WAYPOINT_FILE)) {
            Type type = new TypeToken<List<Waypoint>>(){}.getType();
            List<Waypoint> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                WAYPOINTS.clear();
                WAYPOINTS.addAll(loaded);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load waypoints", e);
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(WAYPOINT_FILE)) {
            GSON.toJson(WAYPOINTS, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save waypoints", e);
        }
    }
}
