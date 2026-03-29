package com.hudclient;

import com.hudclient.config.HudConfig;
import com.hudclient.hud.HudRenderer;
import com.hudclient.waypoints.WaypointManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudClientMod implements ClientModInitializer {

    public static final String MOD_ID = "hudclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static KeyBinding toggleHudKey;
    public static KeyBinding addWaypointKey;
    public static KeyBinding openConfigKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("HudClient initializing...");

        HudConfig.load();
        WaypointManager.init();

        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hudclient.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                "category.hudclient"
        ));

        addWaypointKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hudclient.add_waypoint",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.hudclient"
        ));

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hudclient.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.hudclient"
        ));

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (HudConfig.get().hudEnabled) {
                HudRenderer.render(drawContext, tickDelta);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                HudConfig.get().hudEnabled = !HudConfig.get().hudEnabled;
                HudConfig.save();
            }
            while (addWaypointKey.wasPressed()) {
                if (client.player != null) {
                    WaypointManager.addWaypointAtPlayer(client.player);
                }
            }
            while (openConfigKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new com.hudclient.config.HudConfigScreen(null));
                }
            }
        });

        LOGGER.info("HudClient initialized! Press F4 to toggle HUD, B to add waypoint, F8 for config.");
    }
}
