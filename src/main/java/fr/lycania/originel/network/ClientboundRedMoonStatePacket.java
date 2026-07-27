package fr.lycania.originel.network;

import fr.lycania.originel.OriginelMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent by the server whenever the Lune Rouge starts/stops (RedMoonManager),
 * and once to a player on login if it's already active - the client's own
 * RedMoonState.active flag (fr.lycania.originel.redmoon) only ever changes
 * server-side otherwise, so a dedicated-server client would never see the
 * event's visuals without this.
 */
public record ClientboundRedMoonStatePacket(boolean active) implements CustomPacketPayload {

    public static final Type<ClientboundRedMoonStatePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "red_moon_state"));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ClientboundRedMoonStatePacket> CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, ClientboundRedMoonStatePacket::active, ClientboundRedMoonStatePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
