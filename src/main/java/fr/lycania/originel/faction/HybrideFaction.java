package fr.lycania.originel.faction;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.HybrideConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class HybrideFaction {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OriginelMod.MODID, "hybride");

    private static IPlayableFaction<IHybridePlayer> HYBRIDE_FACTION;
    private static boolean setup = false;

    private HybrideFaction() {
    }

    public static void onRegisterEvent(RegisterEvent event) {
        setupFaction();
    }

    public static synchronized void setupFaction() {
        if (setup) {
            return;
        }
        HYBRIDE_FACTION = VampirismAPI.factionRegistry()
                .createPlayableFaction(ID, IHybridePlayer.class,
                        () -> (AttachmentType<IHybridePlayer>) (Object) HybrideAttachments.HYBRIDE_PLAYER.get())
                .color(HybrideConfig.get().factionColor())
                .chatColor(ChatFormatting.DARK_PURPLE)
                .highestLevel(Math.max(1, HybrideConfig.get().maxLevel()))
                .name("faction.originel.hybride")
                .namePlural("faction.originel.hybride.plural")
                .register();
        setup = true;
        OriginelMod.LOGGER.info("Faction Hybride enregistree aupres de Vampirism.");
    }

    public static IPlayableFaction<IHybridePlayer> get() {
        if (HYBRIDE_FACTION == null) {
            throw new IllegalStateException("La faction Hybride n'est pas encore enregistree.");
        }
        return HYBRIDE_FACTION;
    }

    public static boolean isHybride(Player player) {
        IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(player);
        return handler.getCurrentFaction() == HYBRIDE_FACTION;
    }

    public enum AssignResult {
        SUCCESS,
        NOT_WHITELISTED
    }

    /**
     * Force-assigns the Hybride faction to the given player, overriding whatever
     * faction (vampire, werewolf, ...) they may currently be in. Vampirism's own
     * faction exclusivity then guarantees the player can't simultaneously be in
     * another faction, which is what avoids the player-model conflict with
     * Werewolves/Vampirism.
     */
    public static AssignResult assign(ServerPlayer player) {
        if (!HybrideConfig.get().isWhitelisted(player.getUUID())) {
            return AssignResult.NOT_WHITELISTED;
        }
        IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(player);
        handler.setFactionAndLevel(get(), 1);
        return AssignResult.SUCCESS;
    }

    public static void remove(ServerPlayer player) {
        IFactionPlayerHandler handler = VampirismAPI.factionPlayerHandler(player);
        if (handler.getCurrentFaction() == HYBRIDE_FACTION) {
            handler.leaveFaction(false);
        }
    }
}
