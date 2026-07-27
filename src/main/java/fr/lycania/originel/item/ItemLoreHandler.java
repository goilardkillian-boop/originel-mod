package fr.lycania.originel.item;

import fr.lycania.originel.OriginelMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Adds a lore line under any Originel item that has one in the lang file
 * (`<description_id>.lore`), without needing a dedicated Item subclass per
 * item - see fr_fr.json/en_us.json for the actual text. The Dague de
 * l'Originel gets an extra line on top, varying with whether it's been
 * imbibed with Sang de Gardien yet (see ImpregnationHandler).
 */
@EventBusSubscriber(modid = OriginelMod.MODID)
public final class ItemLoreHandler {

    private ItemLoreHandler() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!OriginelMod.MODID.equals(itemId.getNamespace())) {
            return;
        }

        Language language = Language.getInstance();
        String loreKey = stack.getItem().getDescriptionId(stack) + ".lore";
        if (language.has(loreKey)) {
            event.getToolTip().add(Component.translatable(loreKey).withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
        }

        if (stack.is(OriginelItems.DAGUE_ORIGINEL.get())) {
            boolean imbibed = Boolean.TRUE.equals(stack.get(OriginelDataComponents.SANG_GARDIEN.get()));
            String extraKey = imbibed ? "item.originel.dague_originel.lore_imbibee" : "item.originel.dague_originel.lore_ritual";
            if (language.has(extraKey)) {
                event.getToolTip().add(Component.translatable(extraKey).withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
            }
        }
    }
}
