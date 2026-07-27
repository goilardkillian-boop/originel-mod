package fr.lycania.originel.cendre;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModDamageTypes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.CendreConfig;
import fr.lycania.originel.item.OriginelDataComponents;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Anneau de Cendre (etape 9). Armourer's Workshop has no dedicated ring item
 * or equipment slot in the versions available to this mod, so "worn" is
 * implemented as "held in either hand" and /originel cendre convert attaches
 * the charge component to whatever item is already held, preserving it
 * entirely (including any Armourer's Workshop skin data it may carry) -
 * see README "Limitations connues".
 */
@EventBusSubscriber(modid = OriginelMod.MODID)
public final class CendreManager {

    private static final ResourceLocation POWER_WEAKEN_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "cendre_power_weaken");

    private CendreManager() {
    }

    public record RingLocation(InteractionHand hand, ItemStack stack) {
    }

    public static RingLocation findRing(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.has(OriginelDataComponents.ANNEAU_DE_CENDRE_CHARGES.get())) {
            return new RingLocation(InteractionHand.MAIN_HAND, mainHand);
        }
        ItemStack offHand = player.getOffhandItem();
        if (offHand.has(OriginelDataComponents.ANNEAU_DE_CENDRE_CHARGES.get())) {
            return new RingLocation(InteractionHand.OFF_HAND, offHand);
        }
        return null;
    }

    public static boolean isVampire(LivingEntity entity) {
        return VampirismAPI.factionRegistry().getFaction(entity) == VReference.VAMPIRE_FACTION;
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }
        if (!event.getSource().is(ModDamageTypes.SUN_DAMAGE)) {
            return;
        }
        RingLocation ring = findRing(victim);
        if (ring == null || !isVampire(victim)) {
            return;
        }

        CendreConfig cfg = CendreConfig.get();
        double reduction = Math.min(1.0, Math.max(0.0, cfg.sunDamageReductionPercent()));
        if (reduction >= 1.0) {
            event.setCanceled(true);
        } else {
            event.setAmount((float) (event.getAmount() * (1 - reduction)));
        }
        consumeCharge(victim, ring);
    }

    private static void consumeCharge(ServerPlayer player, RingLocation ring) {
        CendreConfig cfg = CendreConfig.get();
        Integer current = ring.stack().get(OriginelDataComponents.ANNEAU_DE_CENDRE_CHARGES.get());
        int charges = current == null ? 0 : current;
        int next = charges - Math.max(0, cfg.chargeLossPerExposure());

        if (next <= 0) {
            player.setItemInHand(ring.hand(), ItemStack.EMPTY);
            player.sendSystemMessage(OriginelText.lore(cfg.messageDestroyed()));
            return;
        }

        int threshold = (int) (cfg.maxCharges() * cfg.chargeLowThresholdPercent());
        if (charges > threshold && next <= threshold) {
            player.sendSystemMessage(OriginelText.lore(cfg.messageLowCharges()));
        }
        ring.stack().set(OriginelDataComponents.ANNEAU_DE_CENDRE_CHARGES.get(), next);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        boolean active = findRing(player) != null && isVampire(player);

        if (!active) {
            clearPowerWeaken(player);
            return;
        }
        applyPowerWeaken(player);

        int interval = Math.max(1, CendreConfig.get().thirstDrainIntervalTicks());
        if (player.tickCount % interval == 0) {
            drainThirst(player);
        }
    }

    private static void applyPowerWeaken(ServerPlayer player) {
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage == null) {
            return;
        }
        double weaken = Math.min(1.0, Math.max(0.0, CendreConfig.get().powerWeakenPercent()));
        damage.addOrReplacePermanentModifier(new AttributeModifier(POWER_WEAKEN_MODIFIER,
                -weaken, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static void clearPowerWeaken(ServerPlayer player) {
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(POWER_WEAKEN_MODIFIER);
        }
    }

    private static void drainThirst(ServerPlayer player) {
        float percent = (float) Math.min(1.0, Math.max(0.0, CendreConfig.get().thirstDrainPercent()));
        if (percent <= 0) {
            return;
        }
        VampirePlayer.get(player).removeBlood(percent);
    }
}
