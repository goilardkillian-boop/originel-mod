package fr.lycania.originel.client.gui;

import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybridePlayer;
import fr.lycania.originel.network.ServerboundUseSkillPacket;
import fr.lycania.originel.skill.ActiveSkill;
import fr.lycania.originel.skill.Skill;
import fr.lycania.originel.skill.SkillRegistry;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * The Hybride's active-skill wheel (see key.originel.skill_wheel). Only
 * lists ActiveSkill entries the local player has unlocked - passive skills
 * apply on their own and have nothing to "use" here. Whether the local
 * player is actually the Hybride at all is intentionally NOT re-checked
 * client-side (see OriginelKeys) - showing an empty/no-op wheel to a player
 * who isn't the Hybride is harmless, and the server is what actually
 * enforces everything through the exact same SkillActivation.tryActivate
 * used by /originel skill use.
 */
public final class SkillWheelScreen extends GuiRadialMenu {

    private SkillWheelScreen(List<RadialSlot> slots) {
        super(Component.translatable("gui.originel.skill_wheel.title"), slots, SkillWheelScreen::onSelect);
    }

    public static void show() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
        long now = player.level().getGameTime();

        List<RadialSlot> slots = new ArrayList<>();
        for (Skill skill : SkillRegistry.all().values()) {
            if (!(skill instanceof ActiveSkill) || !data.hasSkill(skill.id())) {
                continue;
            }
            long readyAt = data.getCooldownExpiry(skill.id());
            boolean onCooldown = now < readyAt;
            int remaining = onCooldown ? (int) ((readyAt - now) / 20L) : 0;
            ResourceLocation icon = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "textures/skill/" + skill.id() + ".png");
            slots.add(new RadialSlot(skill.id(), skill.displayName(),
                    skill.description(), icon, onCooldown, remaining));
        }

        if (slots.isEmpty()) {
            player.displayClientMessage(OriginelText.prefixed(Component.translatable("gui.originel.skill_wheel.empty")), true);
            return;
        }
        Minecraft.getInstance().setScreen(new SkillWheelScreen(slots));
    }

    private static void onSelect(RadialSlot slot) {
        if (slot.onCooldown()) {
            return;
        }
        PacketDistributor.sendToServer(new ServerboundUseSkillPacket(slot.id()));
    }
}
