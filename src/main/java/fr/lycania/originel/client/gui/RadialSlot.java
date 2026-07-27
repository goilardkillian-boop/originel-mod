package fr.lycania.originel.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** One selectable wedge of a {@link GuiRadialMenu}. */
public record RadialSlot(String id, Component name, Component description, ResourceLocation icon,
                          boolean onCooldown, int cooldownSecondsRemaining) {
}
