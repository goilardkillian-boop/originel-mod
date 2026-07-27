package fr.lycania.originel.scellement;

import de.teamlapen.vampirism.core.ModItems;
import fr.lycania.originel.item.OriginelItems;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * The three trophies deposited in the Calice to perform the Rituel de
 * Scellement (a survival alternative to the staff-only /originel scellement):
 * a vampire fang, a werewolf tooth, and a Sang de Gardien - Marcus's own
 * blood, kept hidden because he knew he'd have a role to play.
 */
public enum ScellementComponent {
    CROC_VAMPIRE("croc_vampire", () -> ModItems.VAMPIRE_FANG.get()),
    CROC_LOUP_GAROU("croc_loup_garou", () -> de.teamlapen.werewolves.core.ModItems.WEREWOLF_TOOTH.get()),
    SANG_GARDIEN("sang_gardien", () -> OriginelItems.SANG_GARDIEN.get());

    public static final ScellementComponent[] VALUES = values();

    private final String id;
    private final Supplier<Item> item;

    ScellementComponent(String id, Supplier<Item> item) {
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
