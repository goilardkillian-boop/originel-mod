package fr.lycania.originel.block;

import fr.lycania.originel.OriginelMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginelBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OriginelMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutelDuVoileBlockEntity>> AUTEL_DU_VOILE =
            BLOCK_ENTITY_TYPES.register("autel_du_voile", () -> BlockEntityType.Builder.of(
                    AutelDuVoileBlockEntity::new, OriginelBlocks.AUTEL_DU_VOILE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CaliceBlockEntity>> CALICE =
            BLOCK_ENTITY_TYPES.register("calice", () -> BlockEntityType.Builder.of(
                    CaliceBlockEntity::new, OriginelBlocks.CALICE.get()).build(null));

    private OriginelBlockEntities() {
    }
}
