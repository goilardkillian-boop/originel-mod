package fr.lycania.originel.faction;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.HybrideConfig;
import fr.lycania.originel.skill.PassiveSkill;
import fr.lycania.originel.skill.Skill;
import fr.lycania.originel.skill.SkillRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = OriginelMod.MODID)
public final class HybrideLevelHandler {

    private static final ResourceLocation HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "hybride_level_health");
    private static final ResourceLocation DAMAGE_MODIFIER = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "hybride_level_damage");
    private static final ResourceLocation SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "hybride_level_speed");

    private HybrideLevelHandler() {
    }

    @SubscribeEvent
    public static void onFactionLevelChanged(PlayerFactionEvent.FactionLevelChanged event) {
        IFactionPlayerHandler handler = event.getPlayer();
        Player player = handler.asEntity();
        boolean isHybride = handler.getCurrentFaction() == HybrideFaction.get();
        boolean wasHybride = event.getOldFaction() == HybrideFaction.get();

        if (isHybride) {
            applyLevelStats(player, event.getNewLevel());
            int gained = event.getNewLevel() - event.getOldLevel();
            if (gained > 0) {
                player.getData(HybrideAttachments.HYBRIDE_PLAYER).addSkillPoints(gained);
            }
        } else if (wasHybride) {
            clearLevelStats(player);
            if (player instanceof ServerPlayer serverPlayer) {
                clearPassiveSkills(serverPlayer);
            }
        }
    }

    private static void clearPassiveSkills(ServerPlayer player) {
        HybridePlayer data = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
        for (Skill skill : SkillRegistry.all().values()) {
            if (skill instanceof PassiveSkill passive && data.hasSkill(skill.id())) {
                passive.onLock(player, data);
            }
        }
        SkillRegistry.clearColereModifiers(player);
    }

    private static void applyLevelStats(Player player, int level) {
        HybrideConfig config = HybrideConfig.get();
        setModifier(player, Attributes.MAX_HEALTH, HEALTH_MODIFIER, level * config.bonusHealthPerLevel(), AttributeModifier.Operation.ADD_VALUE);
        setModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER, level * config.bonusDamagePerLevel(), AttributeModifier.Operation.ADD_VALUE);
        setModifier(player, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER, level * config.bonusSpeedPerLevel(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static void clearLevelStats(Player player) {
        removeModifier(player, Attributes.MAX_HEALTH, HEALTH_MODIFIER);
        removeModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER);
        removeModifier(player, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
    }

    private static void setModifier(Player player, Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        if (amount == 0) {
            instance.removeModifier(id);
            return;
        }
        instance.addOrReplacePermanentModifier(new AttributeModifier(id, amount, operation));
    }

    private static void removeModifier(Player player, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!HybrideConfig.get().xpPerKillEnabled()) {
            return;
        }
        LivingEntity killed = event.getEntity();
        if (killed instanceof Player) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!HybrideFaction.isHybride(player)) {
            return;
        }
        HybridePlayer hybridePlayer = player.getData(HybrideAttachments.HYBRIDE_PLAYER);
        hybridePlayer.incrementKillProgress();
        int required = Math.max(1, HybrideConfig.get().killsPerLevel());
        if (hybridePlayer.getKillProgress() < required) {
            return;
        }
        hybridePlayer.resetKillProgress();
        IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(player);
        int current = handler.getCurrentLevel(HybrideFaction.get());
        int max = HybrideFaction.get().getHighestReachableLevel();
        if (current < max) {
            handler.setFactionLevel(HybrideFaction.get(), current + 1);
        }
    }
}
