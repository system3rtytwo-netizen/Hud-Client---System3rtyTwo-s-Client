package com.hudclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HudConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("hudclient.json").toFile();
    private static final Logger LOGGER = LoggerFactory.getLogger("hudclient");

    private static HudConfig INSTANCE = new HudConfig();

    public boolean hudEnabled = true;
    public boolean showKeystrokes = true;
    public boolean showFps = true;
    public boolean showCoords = true;
    public boolean showDirection = true;
    public boolean showBiome = true;
    public boolean showTime = true;
    public boolean showEntityCounter = true;
    public boolean showSpeed = true;
    public boolean showWaypoints = true;
    public boolean showCps = true;

    public int keystrokesX = 2;
    public int keystrokesY = -120;
    public int infoX = 2;
    public int infoY = 2;
    public int entityX = 2;
    public int entityY = 100;
    public int waypointMaxDisplay = 5;
    public int waypointX = -2;
    public int waypointY = 2;

    public int colorFps         = 0xFF55FF55;
    public int colorCoords      = 0xFF55FFFF;
    public int colorDefault     = 0xFFFFFFFF;
    public int colorWaypoint    = 0xFFFFAA00;
    public int colorKeyActive   = 0xFF55FF55;
    public int colorKeyInactive = 0xFFAAAAAA;
    public int colorBackground  = 0x88000000;

    public boolean showBackground    = true;
    public boolean showShadow        = true;
    public int     backgroundPadding = 2;

    public static HudConfig get() { return INSTANCE; }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                HudConfig loaded = GSON.fromJson(reader, HudConfig.class);
                if (loaded != null) INSTANCE = loaded;
            } catch (IOException e) { LOGGER.error("Failed to load config", e); }
        } else { save(); }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) { LOGGER.error("Failed to save config", e); }
    }
}
