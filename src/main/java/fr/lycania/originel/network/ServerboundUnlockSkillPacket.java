package fr.lycania.originel.network;

import fr.lycania.originel.OriginelMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent by the client skill tree (see fr.lycania.originel.client.gui.SkillTreeScreen)
 * when the player clicks an unlockable skill node, asking the server to spend
 * their skill points on it. All validation happens server-side in
 * SkillUnlock.tryUnlock - the client only ever proposes.
 */
public record ServerboundUnlockSkillPacket(String skillId) implements CustomPacketPayload {

    public static final Type<ServerboundUnlockSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "unlock_skill"));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ServerboundUnlockSkillPacket> CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ServerboundUnlockSkillPacket::skillId, ServerboundUnlockSkillPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
