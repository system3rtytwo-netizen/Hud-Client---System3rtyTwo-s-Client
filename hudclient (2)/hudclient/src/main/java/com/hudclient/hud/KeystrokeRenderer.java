package com.hudclient.hud;

import com.hudclient.config.HudConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.glfw.GLFW;

public class KeystrokeRenderer {

    private static final int KEY_SIZE   = 18;
    private static final int KEY_GAP    = 2;
    private static final int MOUSE_W    = 28;
    private static final int MOUSE_H    = 18;

    /**
     * Layout (each cell = KEY_SIZE x KEY_SIZE, gap = KEY_GAP):
     *
     *   [  ] [W ] [  ]
     *   [A ] [S ] [D ]
     *        [SPACE   ]
     *   [LMB] [RMB]
     */
    public static void render(DrawContext ctx, TextRenderer tr, int x, int y, HudConfig cfg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions opt = mc.options;

        long window = mc.getWindow().getHandle();

        boolean wDown     = isKeyDown(window, opt.forwardKey.getBoundKeyCode());
        boolean aDown     = isKeyDown(window, opt.leftKey.getBoundKeyCode());
        boolean sDown     = isKeyDown(window, opt.backKey.getBoundKeyCode());
        boolean dDown     = isKeyDown(window, opt.rightKey.getBoundKeyCode());
        boolean spaceDown = isKeyDown(window, opt.jumpKey.getBoundKeyCode());
        boolean lmbDown   = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT)  == GLFW.GLFW_PRESS;
        boolean rmbDown   = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        int col = KEY_SIZE + KEY_GAP;
        int row = KEY_SIZE + KEY_GAP;

        // Row 0: W
        drawKey(ctx, tr, x + col, y,        KEY_SIZE, KEY_SIZE, "W",   wDown, cfg);

        // Row 1: A S D
        drawKey(ctx, tr, x,       y + row,  KEY_SIZE, KEY_SIZE, "A",   aDown, cfg);
        drawKey(ctx, tr, x + col, y + row,  KEY_SIZE, KEY_SIZE, "S",   sDown, cfg);
        drawKey(ctx, tr, x + col*2, y + row, KEY_SIZE, KEY_SIZE, "D",  dDown, cfg);

        // Row 2: SPACE (spans 3 cols)
        int spaceW = KEY_SIZE * 3 + KEY_GAP * 2;
        drawKey(ctx, tr, x, y + row * 2, spaceW, KEY_SIZE, "SPACE", spaceDown, cfg);

        // Row 3: LMB RMB
        drawKey(ctx, tr, x,            y + row * 3, MOUSE_W, MOUSE_H, "LMB", lmbDown, cfg);
        drawKey(ctx, tr, x + MOUSE_W + KEY_GAP, y + row * 3, MOUSE_W, MOUSE_H, "RMB", rmbDown, cfg);
    }

    private static void drawKey(DrawContext ctx, TextRenderer tr,
                                 int x, int y, int w, int h,
                                 String label, boolean pressed, HudConfig cfg) {
        int bgColor  = pressed ? 0xCC55FF55 : 0x88000000;
        int border   = pressed ? 0xFF55FF55 : 0x88FFFFFF;
        int textColor = pressed ? cfg.colorKeyActive : cfg.colorKeyInactive;

        // Background
        ctx.fill(x, y, x + w, y + h, bgColor);

        // Border (1px)
        ctx.fill(x,       y,       x + w,     y + 1,     border); // top
        ctx.fill(x,       y + h-1, x + w,     y + h,     border); // bottom
        ctx.fill(x,       y,       x + 1,     y + h,     border); // left
        ctx.fill(x + w-1, y,       x + w,     y + h,     border); // right

        // Label centered
        int tw = tr.getWidth(label);
        int th = tr.fontHeight;
        int tx = x + (w - tw) / 2;
        int ty = y + (h - th) / 2;
        ctx.drawTextWithShadow(tr, label, tx, ty, textColor);
    }

    private static boolean isKeyDown(long window, int keyCode) {
        if (keyCode == -1) return false;
        // Mouse buttons are negative in GLFW bindings
        if (keyCode < 0) {
            return GLFW.glfwGetMouseButton(window, keyCode + 100) == GLFW.GLFW_PRESS;
        }
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }
}
