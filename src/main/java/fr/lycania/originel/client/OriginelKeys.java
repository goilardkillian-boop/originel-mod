package fr.lycania.originel.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.lycania.originel.client.gui.SkillTreeScreen;
import fr.lycania.originel.client.gui.SkillWheelScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class OriginelKeys {

    private static final String CATEGORY = "key.categories.originel";

    public static final KeyMapping SKILL_WHEEL =
            new KeyMapping("key.originel.skill_wheel", InputConstants.Type.KEYSYM, InputConstants.KEY_K, CATEGORY);

    public static final KeyMapping SKILL_TREE =
            new KeyMapping("key.originel.skill_tree", InputConstants.Type.KEYSYM, InputConstants.KEY_L, CATEGORY);

    private OriginelKeys() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SKILL_WHEEL);
        event.register(SKILL_TREE);
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        if (event.getAction() != InputConstants.PRESS) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }
        if (SKILL_WHEEL.isDown()) {
            SkillWheelScreen.show();
        } else if (SKILL_TREE.isDown()) {
            SkillTreeScreen.show();
        }
    }
}
