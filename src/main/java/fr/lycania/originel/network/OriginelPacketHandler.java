package fr.lycania.originel.network;

import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.skill.SkillActivation;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = OriginelMod.MODID)
public final class OriginelPacketHandler {

    private OriginelPacketHandler() {
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ServerboundUseSkillPacket.TYPE, ServerboundUseSkillPacket.CODEC,
                OriginelPacketHandler::handleUseSkill);
    }

    private static void handleUseSkill(ServerboundUseSkillPacket packet, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            SkillActivation.Outcome outcome = SkillActivation.tryActivate(player, packet.skillId());
            if (!outcome.success()) {
                player.sendSystemMessage(OriginelText.prefixed(outcome.message()));
            }
        });
    }
}
