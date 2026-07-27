package fr.lycania.originel.item;

import fr.lycania.originel.OriginelMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginelItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OriginelMod.MODID);

    public static final DeferredHolder<Item, Item> DAGUE_ORIGINEL = ITEMS.registerItem("dague_originel",
            props -> new Item(props.stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    private OriginelItems() {
    }
}
