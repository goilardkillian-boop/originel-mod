package fr.lycania.originel.block;

import fr.lycania.originel.OriginelMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginelBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OriginelMod.MODID);

    public static final DeferredBlock<AutelDuVoileBlock> AUTEL_DU_VOILE = BLOCKS.register("autel_du_voile",
            () -> new AutelDuVoileBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    private OriginelBlocks() {
    }
}
