package fr.lycania.originel.block;

import fr.lycania.originel.ritual.RitualComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ContainerHelper;

public class AutelDuVoileBlockEntity extends BlockEntity {

    private final NonNullList<ItemStack> components = NonNullList.withSize(RitualComponent.VALUES.length, ItemStack.EMPTY);

    public AutelDuVoileBlockEntity(BlockPos pos, BlockState state) {
        super(OriginelBlockEntities.AUTEL_DU_VOILE.get(), pos, state);
    }

    public boolean isComplete() {
        return components.stream().noneMatch(ItemStack::isEmpty);
    }

    public boolean hasComponent(RitualComponent component) {
        return !components.get(component.ordinal()).isEmpty();
    }

    /** Tries to place one item from the given stack into the matching component slot. */
    public boolean tryInsert(ItemStack handStack) {
        for (RitualComponent component : RitualComponent.VALUES) {
            int slot = component.ordinal();
            if (components.get(slot).isEmpty() && handStack.is(component.item())) {
                components.set(slot, handStack.copyWithCount(1));
                handStack.shrink(1);
                setChanged();
                return true;
            }
        }
        return false;
    }

    public void consumeAll() {
        components.replaceAll(stack -> ItemStack.EMPTY);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ContainerHelper.saveAllItems(tag, components, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        components.replaceAll(stack -> ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, components, provider);
    }
}
