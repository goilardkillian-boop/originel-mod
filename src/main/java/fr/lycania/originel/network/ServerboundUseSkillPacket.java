package fr.lycania.originel.network;

import fr.lycania.originel.OriginelMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent by the client skill wheel (see fr.lycania.originel.client) when the
 * player selects an active skill slot, asking the server to activate that
 * skill for the sending player. All validation happens server-side in
 * SkillActivation.tryActivate - the client only ever proposes.
 */
public record ServerboundUseSkillPacket(String skillId) implements CustomPacketPayload {

    public static final Type<ServerboundUseSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "use_skill"));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ServerboundUseSkillPacket> CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ServerboundUseSkillPacket::skillId, ServerboundUseSkillPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
