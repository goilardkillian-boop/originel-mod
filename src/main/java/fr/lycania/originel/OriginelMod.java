package fr.lycania.originel;

import com.mojang.logging.LogUtils;
import fr.lycania.originel.config.OriginelConfig;
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
        LOGGER.info("Le Voile gemit... Lycania : L'Originel est charge.");
    }
}
