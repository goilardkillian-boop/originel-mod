package fr.lycania.originel.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.lycania.originel.client.gui.SkillWheelScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Keeps WASD/jump/sneak/sprint working while the skill wheel is open. Vanilla's
 * KeyboardHandler only calls KeyMapping.set(...) when Minecraft.screen == null
 * (see KeyboardHandler#keyPress) - opening ANY Screen, including our own radial
 * wheel, otherwise freezes the player in place exactly like opening the
 * inventory. Re-driving the movement KeyMappings here from a raw GLFW poll,
 * every client tick the wheel is open, undoes exactly that and only that -
 * mouse-look still stops (the cursor is released so it can aim at the wheel),
 * this is only about not stopping dead while selecting a skill mid-walk.
 */
public final class WheelMovementPassthrough {

    private WheelMovementPassthrough() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof SkillWheelScreen)) {
            return;
        }
        long window = mc.getWindow().getWindow();
        sync(mc.options.keyUp, window);
        sync(mc.options.keyDown, window);
        sync(mc.options.keyLeft, window);
        sync(mc.options.keyRight, window);
        sync(mc.options.keyJump, window);
        sync(mc.options.keyShift, window);
        sync(mc.options.keySprint, window);
    }

    private static void sync(KeyMapping mapping, long window) {
        InputConstants.Key key = mapping.getKey();
        if (key.getType() != InputConstants.Type.KEYSYM) {
            return;
        }
        KeyMapping.set(key, InputConstants.isKeyDown(window, key.getValue()));
    }
}
