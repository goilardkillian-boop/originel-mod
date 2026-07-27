package fr.lycania.originel.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Displays a different name once the dagger carries the Sang de Gardien
 * component (see ImpregnationHandler) - the matching texture swap is driven
 * by CustomModelData in the item model (see models/item/dague_originel.json
 * overrides), set alongside the component wherever it's applied.
 */
public class DagueOriginelItem extends Item {

    public DagueOriginelItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (Boolean.TRUE.equals(stack.get(OriginelDataComponents.SANG_GARDIEN.get()))) {
            return Component.translatable("item.originel.dague_originel_imbibee");
        }
        return super.getName(stack);
    }
}
