package fr.lycania.originel.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.block.OriginelBlocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginelItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OriginelMod.MODID);

    public static final DeferredHolder<Item, Item> DAGUE_ORIGINEL = ITEMS.registerItem("dague_originel",
            props -> new DagueOriginelItem(props.stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    // Composants du Rituel d'Hybridation (etape 8) - non craftables, delivres par /originel give.
    public static final DeferredHolder<Item, Item> PIERRE_CLAIR_DE_LUNE = ITEMS.registerItem("pierre_clair_de_lune",
            props -> new Item(props.stacksTo(1).rarity(Rarity.RARE)));

    public static final DeferredHolder<Item, Item> SANG_GARDIEN = ITEMS.registerItem("sang_gardien",
            props -> new Item(props.stacksTo(16).rarity(Rarity.UNCOMMON)));

    public static final DeferredHolder<Item, Item> ECLAT_VOILE = ITEMS.registerItem("eclat_voile",
            props -> new Item(props.stacksTo(16).rarity(Rarity.UNCOMMON)));

    public static final DeferredHolder<Item, Item> CARNET_CORVIN = ITEMS.registerItem("carnet_corvin",
            props -> new CarnetCorvinItem(props.stacksTo(1).rarity(Rarity.RARE)));

    public static final DeferredItem<BlockItem> AUTEL_DU_VOILE = ITEMS.registerSimpleBlockItem(
            "autel_du_voile", OriginelBlocks.AUTEL_DU_VOILE, new Item.Properties().rarity(Rarity.UNCOMMON));

    // Anneau de Cendre (etape 9) - egalement obtenu en convertissant, via
    // /originel cendre convert, un item quelconque deja en main (voir CendreCommand).
    public static final DeferredHolder<Item, Item> ANNEAU_DE_CENDRE = ITEMS.registerItem("anneau_de_cendre",
            props -> new Item(props.stacksTo(1).rarity(Rarity.RARE)));

    private OriginelItems() {
    }
}
