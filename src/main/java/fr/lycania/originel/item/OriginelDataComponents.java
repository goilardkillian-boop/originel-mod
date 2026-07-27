package fr.lycania.originel.item;

import com.mojang.serialization.Codec;
import fr.lycania.originel.OriginelMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginelDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OriginelMod.MODID);

    /** Marks a Dague de l'Originel as ritually imbibed with Guardian's blood. See faiblesse.toml. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SANG_GARDIEN =
            DATA_COMPONENT_TYPES.register("sang_gardien", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    private OriginelDataComponents() {
    }
}
