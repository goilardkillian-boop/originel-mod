package fr.lycania.originel.faction;

import fr.lycania.originel.OriginelMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class HybrideAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, OriginelMod.MODID);

    public static final class Keys {
        public static final ResourceLocation HYBRIDE_PLAYER = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "hybride_player");

        private Keys() {
        }
    }

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HybridePlayer>> HYBRIDE_PLAYER =
            ATTACHMENT_TYPES.register(Keys.HYBRIDE_PLAYER.getPath(), () -> AttachmentType.builder(new HybridePlayer.Factory())
                    .serialize(new HybridePlayer.Serializer())
                    .copyOnDeath()
                    .build());

    private HybrideAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
