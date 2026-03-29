package com.hudclient.config;

import com.hudclient.waypoints.Waypoint;
import com.hudclient.waypoints.WaypointManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

public class HudConfigScreen extends Screen {

    private final Screen parent;
    private int scrollOffset = 0;
    private static final int ENTRY_H = 22;

    // Waypoint editing state
    private TextFieldWidget wpNameField;
    private int editingWpIndex = -1;

    public HudConfigScreen(Screen parent) {
        super(Text.literal("§6HudClient Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int startY = 40;
        int bw = 180;
        int bh = 18;

        // ── Module toggles ─────────────────────────────────
        HudConfig cfg = HudConfig.get();

        addToggle(cx - bw - 2, startY,      bw, bh, "FPS Display",     cfg.showFps,         v -> { cfg.showFps = v;         save(); });
        addToggle(cx + 2,      startY,      bw, bh, "Coordinates",     cfg.showCoords,      v -> { cfg.showCoords = v;      save(); });
        addToggle(cx - bw - 2, startY + 22, bw, bh, "Direction",       cfg.showDirection,   v -> { cfg.showDirection = v;   save(); });
        addToggle(cx + 2,      startY + 22, bw, bh, "Biome",           cfg.showBiome,       v -> { cfg.showBiome = v;       save(); });
        addToggle(cx - bw - 2, startY + 44, bw, bh, "Game Time",       cfg.showTime,        v -> { cfg.showTime = v;        save(); });
        addToggle(cx + 2,      startY + 44, bw, bh, "Speed",           cfg.showSpeed,       v -> { cfg.showSpeed = v;       save(); });
        addToggle(cx - bw - 2, startY + 66, bw, bh, "Entity Counter",  cfg.showEntityCounter, v -> { cfg.showEntityCounter = v; save(); });
        addToggle(cx + 2,      startY + 66, bw, bh, "Keystrokes",      cfg.showKeystrokes,  v -> { cfg.showKeystrokes = v;  save(); });
        addToggle(cx - bw - 2, startY + 88, bw, bh, "Waypoints",       cfg.showWaypoints,   v -> { cfg.showWaypoints = v;  save(); });
        addToggle(cx + 2,      startY + 88, bw, bh, "CPS Counter",     cfg.showCps,         v -> { cfg.showCps = v;        save(); });
        addToggle(cx - bw - 2, startY + 110, bw, bh, "Backgrounds",    cfg.showBackground,  v -> { cfg.showBackground = v; save(); });
        addToggle(cx + 2,      startY + 110, bw, bh, "Text Shadows",   cfg.showShadow,      v -> { cfg.showShadow = v;     save(); });

        // ── Waypoint controls ──────────────────────────────
        int wpY = startY + 140;
        addDrawableChild(ButtonWidget.builder(Text.literal("§e+ Add Waypoint at Feet"), btn -> {
            if (client != null && client.player != null) {
                WaypointManager.addWaypointAtPlayer(client.player);
                clearAndInit();
            }
        }).dimensions(cx - bw - 2, wpY, bw, bh).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("§cClear All Waypoints"), btn -> {
            WaypointManager.clear();
            clearAndInit();
        }).dimensions(cx + 2, wpY, bw, bh).build());

        // List current waypoints
        List<Waypoint> wps = WaypointManager.getAll();
        for (int i = 0; i < wps.size() && i < 6; i++) {
            final int idx = i;
            Waypoint wp = wps.get(i);
            String label = "§f" + wp.name + " §7(" + wp.x + "," + wp.y + "," + wp.z + ")";
            addDrawableChild(ButtonWidget.builder(Text.literal("§c✕ " + label), btn -> {
                WaypointManager.removeWaypoint(idx);
                clearAndInit();
            }).dimensions(cx - bw - 2, wpY + 22 + i * ENTRY_H, bw * 2 + 4, bh).build());
        }

        // ── Close button ───────────────────────────────────
        addDrawableChild(ButtonWidget.builder(Text.literal("§aDone"), btn -> close())
                .dimensions(cx - 50, height - 28, 100, 20).build());
    }

    private void addToggle(int x, int y, int w, int h, String label, boolean current,
                            java.util.function.Consumer<Boolean> setter) {
        String prefix = current ? "§a[ON]  " : "§c[OFF] ";
        addDrawableChild(ButtonWidget.builder(
                Text.literal(prefix + "§f" + label),
                btn -> {
                    boolean newVal = !current;
                    setter.accept(newVal);
                    // Rebuild widgets to reflect new state
                    clearAndInit();
                }
        ).dimensions(x, y, w, h).build());
    }

    private void save() { HudConfig.save(); }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("§7F4 = toggle HUD  |  B = add waypoint  |  F8 = this screen"),
                width / 2, 24, 0xAAAAAA);
        super.render(ctx, mx, my, delta);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
