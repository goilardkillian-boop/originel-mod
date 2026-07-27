package fr.lycania.originel.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import fr.lycania.originel.OriginelMod;
import fr.lycania.originel.config.GeneralConfig;
import fr.lycania.originel.config.OriginelConfig;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = OriginelMod.MODID)
public final class OriginelCommand {

    private OriginelCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(root());
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> root() {
        return Commands.literal("originel")
                .then(Commands.literal("reload")
                        .requires(OriginelCommand::isStaff)
                        .executes(OriginelCommand::executeReload));
    }

    static boolean isStaff(CommandSourceStack source) {
        return source.hasPermission(GeneralConfig.get().staffPermissionLevel());
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        OriginelConfig.reloadAll();
        context.getSource().sendSuccess(() -> OriginelText.prefixed("Configuration rechargee."), true);
        return Command.SINGLE_SUCCESS;
    }
}
