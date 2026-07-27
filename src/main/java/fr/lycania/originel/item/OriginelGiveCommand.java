package fr.lycania.originel.item;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.lycania.originel.command.CommandUtil;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * /originel give <composant> : delivers mod items by command, the primary
 * (and for the dagger, only) way to obtain them - see README/step 6 and 8.
 */
public final class OriginelGiveCommand {

    private OriginelGiveCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("give")
                .requires(CommandUtil::isStaff)
                .then(Commands.literal("dague_originel")
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .executes(context -> giveDague(context, false))
                                .then(Commands.argument("sang_gardien", BoolArgumentType.bool())
                                        .executes(context -> giveDague(context, BoolArgumentType.getBool(context, "sang_gardien"))))));
    }

    private static int giveDague(CommandContext<CommandSourceStack> context, boolean withSangGardien) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        ItemStack stack = new ItemStack(OriginelItems.DAGUE_ORIGINEL.get());
        if (withSangGardien) {
            stack.set(OriginelDataComponents.SANG_GARDIEN.get(), true);
        }
        target.getInventory().placeItemBackInInventory(stack);
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                "Dague de l'Originel remise a " + target.getName().getString()
                        + (withSangGardien ? " (imbibee de Sang de Gardien)." : ".")), true);
        return Command.SINGLE_SUCCESS;
    }
}
