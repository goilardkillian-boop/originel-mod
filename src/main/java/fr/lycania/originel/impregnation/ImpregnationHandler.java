package fr.lycania.originel.impregnation;

import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.ImpregnationConfig;
import fr.lycania.originel.item.OriginelDataComponents;
import fr.lycania.originel.item.OriginelItems;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Ritual to imbibe the Dague de l'Originel with Sang de Gardien in survival,
 * without staff intervention (see FaiblesseHandler - the dagger only pierces
 * the Hybride's invincibility once it carries this component). Anyone
 * wielding the dagger can perform it, not just the Hybride - it's the
 * anti-Hybride weapon.
 */
@EventBusSubscriber(modid = OriginelMod.MODID)
public final class ImpregnationHandler {

    private ImpregnationHandler() {
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isShiftKeyDown()) {
            return;
        }
        ItemStack mainHand = event.getItemStack();
        if (!mainHand.is(OriginelItems.DAGUE_ORIGINEL.get())) {
            return;
        }
        if (Boolean.TRUE.equals(mainHand.get(OriginelDataComponents.SANG_GARDIEN.get()))) {
            player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.impregnation_already")));
            return;
        }

        int cost = ImpregnationConfig.get().bloodCost();
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.is(OriginelItems.SANG_GARDIEN.get()) || offHand.getCount() < cost) {
            player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.impregnation_not_enough_blood", cost)));
            return;
        }

        offHand.shrink(cost);
        mainHand.set(OriginelDataComponents.SANG_GARDIEN.get(), true);
        mainHand.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        player.sendSystemMessage(OriginelText.prefixed(Component.translatable("originel.msg.impregnation_success")));
        playRitualEffect(player);
    }

    private static void playRitualEffect(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        ImpregnationConfig cfg = ImpregnationConfig.get();

        ResourceLocation soundId = ResourceLocation.tryParse(cfg.sound());
        SoundEvent sound = soundId != null ? BuiltInRegistries.SOUND_EVENT.get(soundId) : null;
        if (sound != null) {
            level.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 1.0f, 1.0f);
        }

        ResourceLocation particleId = ResourceLocation.tryParse(cfg.particle());
        if (particleId != null && BuiltInRegistries.PARTICLE_TYPE.get(particleId) instanceof ParticleOptions particleOptions) {
            level.sendParticles(particleOptions, player.getX(), player.getEyeY(), player.getZ(), 20, 0.3, 0.4, 0.3, 0.02);
        }
    }
}
