package whocraft.tardis_refined.command.sub;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import whocraft.tardis_refined.constants.ModMessages;

import java.util.Collection;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class ConfirmCommand {

	public static final SuggestionProvider<CommandSourceStack> SUGGEST_KEYS = (context, builder) -> {
		if (context.getSource().source instanceof ConfirmationStorageDuck confirmations) {
			return SharedSuggestionProvider.suggest(confirmations.tardisRefined$getActiveConfirmations(), builder);
		} else {
			return Suggestions.empty();
		}
	};

	public static final DynamicCommandExceptionType NO_SUCH_CONFIRMATION = new DynamicCommandExceptionType(
			err -> Component.translatable(ModMessages.CMD_CONFIRM_INVALID, err)
	);

	private static boolean sourceSupportsConfirmation(CommandSourceStack sourceStack) {
		CommandSource source = sourceStack.source;
		// Don't require confirmation for Minecraft servers when output is suppressed.
		// This is because the game loop sender (used by /schedule and tags that automatically invoke functions) uses the MinecraftServer as command source.
		// However, the MinecraftServer is also used as a source for the server console, and there we do want to require confirmation.
		if (source instanceof MinecraftServer) {
			return !sourceStack.silent;
		}
		return source instanceof ConfirmationStorageDuck;
	}

	private static boolean hasPendingConfirmation(CommandSourceStack sourceStack) {
		return sourceStack.source instanceof ConfirmationStorageDuck duck && !duck.tardisRefined$getActiveConfirmations().isEmpty();
	}

	private static final String VALID_CHARS;

	static {
		StringBuilder string = new StringBuilder();
		for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
			if (StringReader.isAllowedInUnquotedString(c)) {
				string.append(c);
			}
		}
		VALID_CHARS = string.toString();
	}

	private static String generateKey(RandomSource random) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			builder.append(VALID_CHARS.charAt(random.nextInt(VALID_CHARS.length())));
		}
		return builder.toString();
	}

	private static void updateCommandVisibility(CommandSourceStack source) {
		if (source.getPlayer() != null) {
			source.getServer().getCommands().sendCommands(source.getPlayer());
		}
	}

	public static int runWithConfirmation(
			CommandSourceStack source, CachedCommand command,
			Supplier<Component> onConfirmationNecessaryMessage,
			IntSupplier fallbackResultSupplier
	) throws CommandSyntaxException {
		if (sourceSupportsConfirmation(source)) {
			String key = generateKey(source.getPlayer() != null ? source.getPlayer().getRandom() : source.getLevel().getRandom());
			boolean shouldUpdate = ((ConfirmationStorageDuck) source.source).tardisRefined$getActiveConfirmations().isEmpty();
			((ConfirmationStorageDuck) source.source).tardisRefined$addConfirmation(key, command);

			if (shouldUpdate) {
				updateCommandVisibility(source);
			}

			String c = "/tardis_refined confirm " + key + " with-certainty";
			source.sendSuccess(onConfirmationNecessaryMessage, false);
			source.sendSuccess(
					() -> Component.translatable(
							ModMessages.CMD_CONFIRM_INFO, Component.literal(c).withStyle(
									style -> style.withClickEvent(
											new ClickEvent(
													ClickEvent.Action.SUGGEST_COMMAND, c
											)
									).withUnderlined(true).withColor(
											ChatFormatting.GOLD
									)
							)
					), false
			);
			return fallbackResultSupplier.getAsInt();
		} else {
			return command.run();
		}
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("confirm").requires(
				ConfirmCommand::hasPendingConfirmation
		).then(
				Commands.argument("key", StringArgumentType.word()).suggests(SUGGEST_KEYS).then(
						Commands.literal("with-certainty").executes(ctx -> {
							if (ctx.getSource().source instanceof ConfirmationStorageDuck confirmer) {
								boolean shouldUpdate = confirmer.tardisRefined$getActiveConfirmations().size() == 1;
								int res = confirmer.tardisRefined$confirm(StringArgumentType.getString(ctx, "key"));
								if (shouldUpdate) {
									updateCommandVisibility(ctx.getSource());
								}
								return res;
							}
							return 0; // Should never happen.
						})
				)
		);
	}

	// We want cancellation to be easy to make it hard to confirm by accident.
	// It's better that users cancel by accident than that they confirm by accident.
	public static ArgumentBuilder<CommandSourceStack, ?> registerCancellation(CommandDispatcher<CommandSourceStack> dispatcher) {
		return Commands.literal("cancel").requires(
				ConfirmCommand::hasPendingConfirmation
		).executes(ctx -> {
			if (ctx.getSource().source instanceof ConfirmationStorageDuck confirmer) {
				int pendingConfirmations = confirmer.tardisRefined$getActiveConfirmations().size();
				confirmer.tardisRefined$cancel();
				updateCommandVisibility(ctx.getSource());
				if (pendingConfirmations == 1) {
					ctx.getSource().sendSuccess(
							() -> Component.translatable(ModMessages.CMD_CANCEL_SINGLE), false
					);
				} else {
					ctx.getSource().sendSuccess(
							() -> Component.translatable(ModMessages.CMD_CANCEL_MULTI, pendingConfirmations),
							false
					);
				}
				return pendingConfirmations;
			}
			return 0; // Should never happen.
		});
	}

	public interface ConfirmationStorageDuck {
		void tardisRefined$addConfirmation(String key, CachedCommand action);
		Collection<String> tardisRefined$getActiveConfirmations();
		int tardisRefined$confirm(String key) throws CommandSyntaxException;
		void tardisRefined$cancel();
	}

	public interface CachedCommand {
		int run() throws CommandSyntaxException;
	}

}
