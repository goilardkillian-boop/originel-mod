package fr.lycania.originel.block;

import fr.lycania.originel.item.OriginelItems;
import fr.lycania.originel.scellement.ScellementRitualManager;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The Calice: deposit the three Rituel de Scellement components (see
 * ScellementComponent), then light it with the Briquet special to seal the
 * current Hybride for a few minutes without needing staff to run
 * /originel scellement.
 */
public class CaliceBlock extends BaseEntityBlock {

    public static final com.mojang.serialization.MapCodec<CaliceBlock> CODEC = simpleCodec(CaliceBlock::new);

    public CaliceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CaliceBlockEntity(pos, state);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack heldStack, @NotNull BlockState state, @NotNull Level level,
                                                        @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                        @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof CaliceBlockEntity calice)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (heldStack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (heldStack.is(OriginelItems.BRIQUET_SPECIAL.get())) {
            if (!calice.isComplete()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                ScellementRitualManager.light(serverLevel, pos, serverPlayer);
                calice.consumeAll();
            }
            return ItemInteractionResult.CONSUME;
        }

        if (calice.tryInsert(heldStack)) {
            MutableComponent message = Component.translatable("originel.msg.calice_component_deposited");
            if (calice.isComplete()) {
                message = message.append(Component.translatable("originel.msg.calice_complete"));
            }
            player.sendSystemMessage(OriginelText.prefixed(message));
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
