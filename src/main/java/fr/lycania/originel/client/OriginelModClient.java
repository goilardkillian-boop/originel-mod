package fr.lycania.originel.client;

import fr.lycania.originel.OriginelMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-only entry point (never constructed on a dedicated server - see
 * NeoForge's dist-based @Mod loading), separate from the common OriginelMod
 * class. This is where the skill-wheel keybinding lives; keeping it out of
 * the common class is what lets a dedicated server load this mod at all
 * without ever touching client-only classes like Screen/KeyMapping.
 */
@Mod(value = OriginelMod.MODID, dist = Dist.CLIENT)
public final class OriginelModClient {

    public OriginelModClient(IEventBus modEventBus) {
        modEventBus.addListener(OriginelKeys::register);
        NeoForge.EVENT_BUS.register(OriginelKeys.class);
        NeoForge.EVENT_BUS.register(RedMoonSkyRenderer.class);
        NeoForge.EVENT_BUS.register(WheelMovementPassthrough.class);
    }
}
