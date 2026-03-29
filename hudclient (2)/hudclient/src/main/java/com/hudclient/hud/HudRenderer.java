package com.hudclient.hud;

import com.hudclient.config.HudConfig;
import com.hudclient.waypoints.Waypoint;
import com.hudclient.waypoints.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    // CPS tracking
    private static final List<Long> leftClicks  = new ArrayList<>();
    private static final List<Long> rightClicks = new ArrayList<>();

    public static void registerLeftClick()  { leftClicks.add(System.currentTimeMillis()); }
    public static void registerRightClick() { rightClicks.add(System.currentTimeMillis()); }

    private static int getCps(List<Long> clicks) {
        long now = System.currentTimeMillis();
        clicks.removeIf(t -> now - t > 1000);
        return clicks.size();
    }

    public static void render(DrawContext ctx, float tickDelta) {
        if (MC.options.debugEnabled) return; // don't render over F3 screen
        ClientPlayerEntity player = MC.player;
        if (player == null) return;

        HudConfig cfg = HudConfig.get();
        TextRenderer tr = MC.textRenderer;
        int sw = MC.getWindow().getScaledWidth();
        int sh = MC.getWindow().getScaledHeight();

        // ── LEFT INFO PANEL ──────────────────────────────────
        int lx = cfg.infoX;
        int ly = cfg.infoY;
        int lineH = tr.fontHeight + 2;

        List<String[]> lines = new ArrayList<>(); // [label, value, colorHex]

        if (cfg.showFps) {
            int fps = MC.getCurrentFps();
            String fpsColor = fps > 100 ? "#55FF55" : fps > 60 ? "#FFFF55" : "#FF5555";
            lines.add(new String[]{"FPS: ", String.valueOf(fps), fpsColor});
        }

        if (cfg.showCoords) {
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            lines.add(new String[]{"XYZ: ",
                String.format("%.1f / %.1f / %.1f", x, y, z), "#55FFFF"});
            BlockPos bp = player.getBlockPos();
            lines.add(new String[]{"Block: ",
                String.format("%d / %d / %d", bp.getX(), bp.getY(), bp.getZ()), "#AAAAAA"});
        }

        if (cfg.showDirection) {
            float yaw = MathHelper.wrapDegrees(player.getYaw());
            lines.add(new String[]{"Facing: ", getDirection(yaw) +
                String.format("  (%.1f°)", yaw), "#FFFF55"});
        }

        if (cfg.showSpeed) {
            double vx = player.getX() - player.prevX;
            double vz = player.getZ() - player.prevZ;
            double speed = Math.sqrt(vx * vx + vz * vz) * 20.0;
            lines.add(new String[]{"Speed: ",
                String.format("%.2f m/s", speed), "#FFFFFF"});
        }

        if (cfg.showBiome && MC.world != null) {
            BlockPos bp = player.getBlockPos();
            String biome = MC.world.getBiome(bp).getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("unknown");
            lines.add(new String[]{"Biome: ", biome, "#88FF88"});
        }

        if (cfg.showTime && MC.world != null) {
            long time = MC.world.getTimeOfDay() % 24000;
            int hours   = (int)((time / 1000 + 6) % 24);
            int minutes = (int)((time % 1000) * 60 / 1000);
            lines.add(new String[]{"Time: ",
                String.format("%02d:%02d (%s)", hours, minutes, getDayNight(time)), "#AAAAFF"});
        }

        if (cfg.showCps) {
            lines.add(new String[]{"CPS: ",
                "L:" + getCps(leftClicks) + "  R:" + getCps(rightClicks), "#FFAA55"});
        }

        // Draw left panel background
        if (cfg.showBackground && !lines.isEmpty()) {
            int maxW = 0;
            for (String[] l : lines) {
                int w = tr.getWidth(l[0] + l[1]);
                if (w > maxW) maxW = w;
            }
            int pad = cfg.backgroundPadding;
            ctx.fill(lx - pad, ly - pad,
                     lx + maxW + pad, ly + lines.size() * lineH + pad,
                     cfg.colorBackground);
        }

        for (String[] l : lines) {
            if (cfg.showShadow) {
                ctx.drawTextWithShadow(tr, l[0], lx, ly, 0xFFAAAAAA);
                ctx.drawTextWithShadow(tr, l[1], lx + tr.getWidth(l[0]), ly, parseColor(l[2]));
            } else {
                ctx.drawText(tr, l[0], lx, ly, 0xFFAAAAAA, false);
                ctx.drawText(tr, l[1], lx + tr.getWidth(l[0]), ly, parseColor(l[2]), false);
            }
            ly += lineH;
        }

        // ── ENTITY COUNTER ───────────────────────────────────
        if (cfg.showEntityCounter && MC.world != null) {
            int ex = cfg.entityX;
            int ey = cfg.entityY;
            World world = MC.world;

            int total = 0, mobs = 0, players = 0, items = 0;
            for (Entity e : world.getEntities()) {
                total++;
                if (e instanceof net.minecraft.entity.mob.MobEntity) mobs++;
                else if (e instanceof net.minecraft.entity.player.PlayerEntity) players++;
                else if (e instanceof net.minecraft.entity.ItemEntity) items++;
            }

            List<String> eLines = new ArrayList<>();
            eLines.add("§7Entities: §f" + total);
            eLines.add("§7Mobs: §c"     + mobs);
            eLines.add("§7Players: §a"  + players);
            eLines.add("§7Items: §e"    + items);

            if (cfg.showBackground) {
                int maxW = 0;
                for (String s : eLines) {
                    int w = tr.getWidth(s);
                    if (w > maxW) maxW = w;
                }
                int pad = cfg.backgroundPadding;
                ctx.fill(ex - pad, ey - pad,
                         ex + maxW + pad, ey + eLines.size() * lineH + pad,
                         cfg.colorBackground);
            }

            for (String s : eLines) {
                if (cfg.showShadow) ctx.drawTextWithShadow(tr, s, ex, ey, 0xFFFFFFFF);
                else ctx.drawText(tr, s, ex, ey, 0xFFFFFFFF, false);
                ey += lineH;
            }
        }

        // ── WAYPOINTS ────────────────────────────────────────
        if (cfg.showWaypoints) {
            List<Waypoint> wps = WaypointManager.getWaypointsForDisplay(
                    player.getBlockPos(), cfg.waypointMaxDisplay);

            int wx = cfg.waypointX < 0 ? sw + cfg.waypointX : cfg.waypointX;
            int wy = cfg.waypointY;

            if (!wps.isEmpty() && cfg.showBackground) {
                int maxW = 0;
                for (Waypoint wp : wps) {
                    int w = tr.getWidth(wp.getDisplayString(player.getBlockPos()));
                    if (w > maxW) maxW = w;
                }
                maxW += 8; // icon space
                int pad = cfg.backgroundPadding;
                ctx.fill(wx - maxW - pad, wy - pad,
                         wx + pad, wy + wps.size() * lineH + pad,
                         cfg.colorBackground);
            }

            for (Waypoint wp : wps) {
                String display = wp.getDisplayString(player.getBlockPos());
                int textX = wx - tr.getWidth(display) - 8;
                if (cfg.showShadow)
                    ctx.drawTextWithShadow(tr, display, textX, wy, wp.getColor());
                else
                    ctx.drawText(tr, display, textX, wy, wp.getColor(), false);
                wy += lineH;
            }
        }

        // ── KEYSTROKES ───────────────────────────────────────
        if (cfg.showKeystrokes) {
            int kbx = cfg.keystrokesX;
            int kby = cfg.keystrokesY < 0 ? sh + cfg.keystrokesY : cfg.keystrokesY;
            KeystrokeRenderer.render(ctx, tr, kbx, kby, cfg);
        }
    }

    private static String getDirection(float yaw) {
        float y = ((yaw % 360) + 360) % 360;
        if (y < 22.5f  || y >= 337.5f) return "North (-Z) ";
        if (y < 67.5f)                  return "NE        ";
        if (y < 112.5f)                 return "East (+X) ";
        if (y < 157.5f)                 return "SE        ";
        if (y < 202.5f)                 return "South (+Z)";
        if (y < 247.5f)                 return "SW        ";
        if (y < 292.5f)                 return "West (-X) ";
        return "NW        ";
    }

    private static String getDayNight(long time) {
        return (time < 12000) ? "Day" : "Night";
    }

    private static int parseColor(String hex) {
        if (hex.startsWith("#")) {
            try { return (int)(Long.parseLong("FF" + hex.substring(1), 16)); }
            catch (NumberFormatException ignored) {}
        }
        return 0xFFFFFFFF;
    }
}
