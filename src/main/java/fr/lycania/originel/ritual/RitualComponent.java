package fr.lycania.originel.ritual;

import fr.lycania.originel.item.OriginelItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public enum RitualComponent {
    PIERRE_CLAIR_DE_LUNE("pierre_clair_de_lune", () -> OriginelItems.PIERRE_CLAIR_DE_LUNE.get()),
    SANG_GARDIEN("sang_gardien", () -> OriginelItems.SANG_GARDIEN.get()),
    ECLAT_VOILE("eclat_voile", () -> OriginelItems.ECLAT_VOILE.get()),
    CARNET_CORVIN("carnet_corvin", () -> OriginelItems.CARNET_CORVIN.get());

    public static final RitualComponent[] VALUES = values();

    private final String id;
    private final Supplier<Item> item;

    RitualComponent(String id, Supplier<Item> item) {
        this.id = id;
        this.item = item;
    }

    public String id() {
        return id;
    }

    public Item item() {
        return item.get();
    }
}
