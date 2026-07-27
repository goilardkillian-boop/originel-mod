package fr.lycania.originel.cendre;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.lycania.originel.command.CommandUtil;
import fr.lycania.originel.config.CendreConfig;
import fr.lycania.originel.item.OriginelDataComponents;
import fr.lycania.originel.item.OriginelItems;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * /originel cendre convert|give <joueur> - see CendreManager for the sun
 * protection / malus / charge logic once the ring exists.
 */
public final class CendreCommand {

    private CendreCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("cendre")
                .requires(CommandUtil::isStaff)
                .then(Commands.literal("convert")
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .executes(CendreCommand::executeConvert)))
                .then(Commands.literal("give")
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .executes(CendreCommand::executeGive)));
    }

    private static int executeConvert(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        ItemStack held = target.getMainHandItem();
        if (held.isEmpty()) {
            context.getSource().sendFailure(OriginelText.prefixed(
                    target.getName().getString() + " ne tient rien en main principale."));
            return 0;
        }
        if (held.has(OriginelDataComponents.ANNEAU_DE_CENDRE_CHARGES.get())) {
            context.getSource().sendFailure(OriginelText.prefixed(
                    "Cet objet est deja un Anneau de Cendre."));
            return 0;
        }
        held.set(OriginelDataComponents.ANNEAU_DE_CENDRE_CHARGES.get(), CendreConfig.get().maxCharges());
        target.sendSystemMessage(OriginelText.lore(CendreConfig.get().messageConverted()));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                "L'objet en main de " + target.getName().getString() + " est devenu un Anneau de Cendre."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGive(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        ItemStack stack = new ItemStack(OriginelItems.ANNEAU_DE_CENDRE.get());
        stack.set(OriginelDataComponents.ANNEAU_DE_CENDRE_CHARGES.get(), CendreConfig.get().maxCharges());
        target.getInventory().placeItemBackInInventory(stack);
        target.sendSystemMessage(OriginelText.lore(CendreConfig.get().messageConverted()));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                "Anneau de Cendre remis a " + target.getName().getString() + "."), true);
        return Command.SINGLE_SUCCESS;
    }
}
