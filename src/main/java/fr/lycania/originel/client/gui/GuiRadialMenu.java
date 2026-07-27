package fr.lycania.originel.client.gui;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * A minimal from-scratch radial ("wheel") selection menu: N wedges arranged
 * in a ring around the screen centre, hit-tested by the cursor's angle and
 * distance from that centre. Modeled after (but not copied from - Vampirism's
 * own equivalent lives in an internal, non-api "lib" package we don't depend
 * on) the general shape of Vampirism's own action-selection wheel: a short
 * open animation, translucent GUI-buffer wedges (no textures for the ring
 * itself, only for the per-slot icons), and a centred name/description for
 * whichever wedge is currently hovered.
 *
 * <p>Movement (WASD/jump/sneak/sprint) keeps working while this screen is open -
 * see fr.lycania.originel.client.WheelMovementPassthrough, which re-drives those
 * KeyMappings from a raw key poll since vanilla stops updating them the moment
 * any Screen is open. Mouse-look still doesn't (the cursor is released to aim
 * at the wheel), only forward motion.
 */
public abstract class GuiRadialMenu extends Screen {

    private static final float OPEN_ANIMATION_SECONDS = 0.2f;
    private static final float MAX_RADIUS_IN = 26f;
    private static final float MAX_RADIUS_OUT = 54f;
    private static final float ICON_SIZE = 16f;

    protected final List<RadialSlot> slots;
    private final Consumer<RadialSlot> onSelect;
    private final long openedAtMillis;
    private int hoveredIndex = -1;

    protected GuiRadialMenu(Component title, List<RadialSlot> slots, Consumer<RadialSlot> onSelect) {
        super(title);
        this.slots = slots;
        this.onSelect = onSelect;
        this.openedAtMillis = System.currentTimeMillis();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static double sliceCenterDeg(int index, int count) {
        return (index / (double) count - 0.25) * 360.0;
    }

    private static double sliceLeftDeg(int index, int count) {
        return ((index - 0.5) / count - 0.25) * 360.0;
    }

    private static double sliceRightDeg(int index, int count) {
        return ((index + 0.5) / count - 0.25) * 360.0;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = width / 2;
        int centerY = height / 2;
        int count = slots.size();

        float elapsed = (System.currentTimeMillis() - openedAtMillis) / 1000f;
        float t = Math.min(1f, elapsed / OPEN_ANIMATION_SECONDS);
        float eased = 1f - (1f - t) * (1f - t) * (1f - t);
        float radiusIn = MAX_RADIUS_IN * eased;
        float radiusOut = MAX_RADIUS_OUT * eased;

        hoveredIndex = findHoveredSlot(mouseX, mouseY, centerX, centerY, count, radiusIn, radiusOut);

        for (int i = 0; i < count; i++) {
            boolean highlighted = i == hoveredIndex;
            int[] rgba = colorFor(slots.get(i), highlighted);
            drawWedge(graphics, centerX, centerY, radiusIn, radiusOut,
                    sliceLeftDeg(i, count), sliceRightDeg(i, count), rgba[0], rgba[1], rgba[2], rgba[3]);
        }

        float itemRadius = (radiusIn + radiusOut) / 2f;
        for (int i = 0; i < count; i++) {
            double angleRad = Math.toRadians(sliceCenterDeg(i, count));
            int iconX = (int) (centerX - ICON_SIZE / 2 + itemRadius * Math.cos(angleRad));
            int iconY = (int) (centerY - ICON_SIZE / 2 + itemRadius * Math.sin(angleRad));
            graphics.blit(slots.get(i).icon(), iconX, iconY, 0, 0f, 0f, 16, 16, 16, 16);
        }

        if (hoveredIndex >= 0) {
            RadialSlot hovered = slots.get(hoveredIndex);
            graphics.drawCenteredString(font, hovered.name(), centerX, centerY - 10, 0xFFFFFFFF);
            graphics.drawCenteredString(font, hovered.description(), centerX, centerY + 2, 0xFFAAAAAA);
            if (hovered.onCooldown()) {
                graphics.drawCenteredString(font, Component.literal(hovered.cooldownSecondsRemaining() + "s"),
                        centerX, centerY + 14, 0xFFFF5555);
            }
        } else if (count == 0) {
            graphics.drawCenteredString(font, title, centerX, centerY, 0xFFAAAAAA);
        }
    }

    private int findHoveredSlot(int mouseX, int mouseY, int centerX, int centerY, int count, float radiusIn, float radiusOut) {
        if (count == 0) {
            return -1;
        }
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < radiusIn || dist >= radiusOut) {
            return -1;
        }
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        double base = sliceLeftDeg(0, count);
        if (angle < base) {
            angle += 360.0;
        }
        for (int i = 0; i < count; i++) {
            double left = sliceLeftDeg(i, count);
            double right = sliceRightDeg(i, count);
            if (angle >= left && angle < right) {
                return i;
            }
        }
        return -1;
    }

    /** RGBA (0-255) for a wedge; override to color-code by cooldown/availability. */
    protected int[] colorFor(RadialSlot slot, boolean highlighted) {
        if (slot.onCooldown()) {
            return highlighted ? new int[]{200, 70, 70, 140} : new int[]{140, 40, 40, 110};
        }
        return highlighted ? new int[]{90, 70, 160, 150} : new int[]{20, 15, 30, 110};
    }

    private void drawWedge(GuiGraphics graphics, float centerX, float centerY, float radiusIn, float radiusOut,
                            double startDeg, double endDeg, int r, int g, int b, int a) {
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.gui());
        int sections = Math.max(1, (int) Math.ceil((endDeg - startDeg) / 5.0));
        double step = (endDeg - startDeg) / sections;
        for (int i = 0; i < sections; i++) {
            double a1 = Math.toRadians(startDeg + step * i);
            double a2 = Math.toRadians(startDeg + step * (i + 1));
            float outX1 = centerX + (float) (radiusOut * Math.cos(a1));
            float outY1 = centerY + (float) (radiusOut * Math.sin(a1));
            float inX1 = centerX + (float) (radiusIn * Math.cos(a1));
            float inY1 = centerY + (float) (radiusIn * Math.sin(a1));
            float inX2 = centerX + (float) (radiusIn * Math.cos(a2));
            float inY2 = centerY + (float) (radiusIn * Math.sin(a2));
            float outX2 = centerX + (float) (radiusOut * Math.cos(a2));
            float outY2 = centerY + (float) (radiusOut * Math.sin(a2));
            buffer.addVertex(outX1, outY1, 0).setColor(r, g, b, a);
            buffer.addVertex(inX1, inY1, 0).setColor(r, g, b, a);
            buffer.addVertex(inX2, inY2, 0).setColor(r, g, b, a);
            buffer.addVertex(outX2, outY2, 0).setColor(r, g, b, a);
        }
        graphics.flush();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            onClose();
            return true;
        }
        if (button == 0 && hoveredIndex >= 0) {
            RadialSlot selected = slots.get(hoveredIndex);
            onClose();
            onSelect.accept(selected);
        }
        return true;
    }
}
