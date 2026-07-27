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

    /** Charges restantes d'un Anneau de Cendre (item quelconque converti via /originel cendre convert). See cendre.toml. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ANNEAU_DE_CENDRE_CHARGES =
            DATA_COMPONENT_TYPES.register("anneau_de_cendre_charges", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    private OriginelDataComponents() {
    }
}
