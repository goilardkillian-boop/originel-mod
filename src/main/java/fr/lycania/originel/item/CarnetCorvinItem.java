package fr.lycania.originel.item;

import fr.lycania.originel.client.gui.SkillTreeScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;

/**
 * The Carnet de Corvin doubles as the "inventory" entry point for the skill
 * tree (alongside OriginelKeys.SKILL_TREE), mirroring how Vampirism/Werewolves
 * expose their own skill screens both via a keybind and an item. Sneak-using
 * it opens the tree instead of the normal written-book reading screen;
 * a plain right-click still reads the lore pages (RituelConfig content, set
 * by /originel give carnet_corvin) exactly as before. The SkillTreeScreen
 * reference is only ever touched inside the level.isClientSide() branch, so
 * this class stays safe to load on a dedicated server, same as every other
 * common-side class in this mod that reacts to client input.
 */
public class CarnetCorvinItem extends WrittenBookItem {

    public CarnetCorvinItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(hand);
            if (level.isClientSide()) {
                SkillTreeScreen.show();
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return super.use(level, player, hand);
    }
}
