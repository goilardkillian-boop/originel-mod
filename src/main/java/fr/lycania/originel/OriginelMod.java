package fr.lycania.originel;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * Lycania : L'Originel.
 * Corvin, banni il y a 900 ans en marge du rituel de la nuit ecarlate, revient voler
 * la Pierre de Clair de Lune qui maintient le Voile protecteur.
 */
@Mod(OriginelMod.MODID)
public class OriginelMod {

    public static final String MODID = "originel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OriginelMod(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("Le Voile gemit... Lycania : L'Originel est charge.");
    }
}
