package fr.lycania.originel.command;

import fr.lycania.originel.config.GeneralConfig;
import net.minecraft.commands.CommandSourceStack;

public final class CommandUtil {

    private CommandUtil() {
    }

    public static boolean isStaff(CommandSourceStack source) {
        return source.hasPermission(GeneralConfig.get().staffPermissionLevel());
    }
}
