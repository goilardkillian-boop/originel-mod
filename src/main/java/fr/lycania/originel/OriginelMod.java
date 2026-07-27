package fr.lycania.originel;

import com.mojang.logging.LogUtils;
import fr.lycania.originel.block.OriginelBlockEntities;
import fr.lycania.originel.block.OriginelBlocks;
import fr.lycania.originel.config.OriginelConfig;
import fr.lycania.originel.faction.HybrideAttachments;
import fr.lycania.originel.faction.HybrideFaction;
import fr.lycania.originel.item.OriginelCreativeTab;
import fr.lycania.originel.item.OriginelDataComponents;
import fr.lycania.originel.item.OriginelItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(OriginelMod.MODID)
public class OriginelMod {

    public static final String MODID = "originel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OriginelMod(IEventBus modEventBus, ModContainer modContainer) {
        OriginelConfig.loadAll();
        HybrideAttachments.register(modEventBus);
        OriginelDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        OriginelBlocks.BLOCKS.register(modEventBus);
        OriginelBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        OriginelItems.ITEMS.register(modEventBus);
        OriginelCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        // Vampirism's own faction registry is only populated once its mod
        // constructor has run; RegisterEvent is the first event we're
        // guaranteed to receive after that, so the faction is created there.
        modEventBus.addListener(HybrideFaction::onRegisterEvent);
        LOGGER.info("Le Voile gemit... Lycania : L'Originel est charge.");
    }
}
