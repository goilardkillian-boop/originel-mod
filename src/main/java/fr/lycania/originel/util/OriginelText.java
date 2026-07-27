package fr.lycania.originel.util;

import fr.lycania.originel.config.GeneralConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class OriginelText {

    private OriginelText() {
    }

    public static MutableComponent prefixed(String message) {
        return prefixed(Component.literal(message));
    }

    public static MutableComponent prefixed(Component message) {
        return Component.literal("[" + GeneralConfig.get().chatPrefix() + "] ")
                .withStyle(ChatFormatting.DARK_PURPLE)
                .append(message.copy().withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    public static MutableComponent lore(String message) {
        return Component.literal(message).withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC);
    }
}
