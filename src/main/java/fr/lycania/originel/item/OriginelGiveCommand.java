package fr.lycania.originel.item;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.lycania.originel.command.CommandUtil;
import fr.lycania.originel.config.RituelConfig;
import fr.lycania.originel.util.OriginelText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.List;

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
                                        .executes(context -> giveDague(context, BoolArgumentType.getBool(context, "sang_gardien"))))))
                .then(simpleGive("pierre_clair_de_lune", () -> OriginelItems.PIERRE_CLAIR_DE_LUNE.get()))
                .then(simpleGive("sang_gardien", () -> OriginelItems.SANG_GARDIEN.get()))
                .then(simpleGive("eclat_voile", () -> OriginelItems.ECLAT_VOILE.get()))
                .then(simpleGive("autel_du_voile", () -> OriginelItems.AUTEL_DU_VOILE.get()))
                .then(Commands.literal("carnet_corvin")
                        .then(Commands.argument("joueur", EntityArgument.player())
                                .executes(OriginelGiveCommand::giveCarnet)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> simpleGive(String name, java.util.function.Supplier<net.minecraft.world.item.Item> item) {
        return Commands.literal(name)
                .then(Commands.argument("joueur", EntityArgument.player())
                        .executes(context -> giveSimple(context, name, item.get())));
    }

    private static int giveSimple(CommandContext<CommandSourceStack> context, String name, net.minecraft.world.item.Item item) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        target.getInventory().placeItemBackInInventory(new ItemStack(item));
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                name + " remis a " + target.getName().getString() + "."), true);
        return Command.SINGLE_SUCCESS;
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

    private static int giveCarnet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "joueur");
        ItemStack stack = new ItemStack(OriginelItems.CARNET_CORVIN.get());
        List<Filterable<Component>> pages = RituelConfig.get().bookPages().stream()
                .map(page -> Filterable.<Component>passThrough(Component.literal(page)))
                .toList();
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
                Filterable.passThrough(RituelConfig.get().bookTitle()),
                RituelConfig.get().bookAuthor(),
                0, pages, true));
        target.getInventory().placeItemBackInInventory(stack);
        context.getSource().sendSuccess(() -> OriginelText.prefixed(
                "Carnet de Corvin remis a " + target.getName().getString() + "."), true);
        return Command.SINGLE_SUCCESS;
    }
}
