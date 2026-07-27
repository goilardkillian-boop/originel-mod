package fr.lycania.originel.item;

import fr.lycania.originel.OriginelMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** One creative-mode tab with every item/block the mod adds, in roughly the order they show up in the lore/rituals. */
public final class OriginelCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OriginelMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ORIGINEL_TAB = CREATIVE_MODE_TABS.register("originel",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.originel"))
                    .icon(() -> new ItemStack(OriginelItems.DAGUE_ORIGINEL.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(OriginelItems.DAGUE_ORIGINEL.get());
                        output.accept(imbibedDague());
                        output.accept(OriginelItems.SANG_GARDIEN.get());
                        output.accept(OriginelItems.PIERRE_CLAIR_DE_LUNE.get());
                        output.accept(OriginelItems.ECLAT_VOILE.get());
                        output.accept(OriginelItems.CARNET_CORVIN.get());
                        output.accept(OriginelItems.AUTEL_DU_VOILE.get());
                        output.accept(OriginelItems.CALICE.get());
                        output.accept(OriginelItems.BRIQUET_SPECIAL.get());
                        output.accept(OriginelItems.ANNEAU_DE_CENDRE.get());
                    })
                    .build());

    private OriginelCreativeTab() {
    }

    private static ItemStack imbibedDague() {
        ItemStack stack = new ItemStack(OriginelItems.DAGUE_ORIGINEL.get());
        stack.set(OriginelDataComponents.SANG_GARDIEN.get(), true);
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        return stack;
    }
}
