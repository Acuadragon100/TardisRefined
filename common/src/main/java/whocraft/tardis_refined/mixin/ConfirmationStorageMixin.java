package whocraft.tardis_refined.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import whocraft.tardis_refined.command.sub.ConfirmCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin({MinecraftServer.class, Player.class, RconConsoleSource.class})
public class ConfirmationStorageMixin implements ConfirmCommand.ConfirmationStorageDuck {

	@Unique
	private final Map<String, ConfirmCommand.CachedCommand> confirmations = new HashMap<>();

	@Override
	public void tardisRefined$addConfirmation(String key, ConfirmCommand.CachedCommand action) {
		confirmations.put(key, action);
	}

	@Override
	public Collection<String> tardisRefined$getActiveConfirmations() {
		return confirmations.keySet();
	}

	@Override
	public int tardisRefined$confirm(String key) throws CommandSyntaxException {
		var confirmation = confirmations.remove(key);
		if (confirmation != null) {
			return confirmation.run();
		} else {
			throw ConfirmCommand.NO_SUCH_CONFIRMATION.create(key);
		}
	}

	@Override
	public void tardisRefined$cancel() {
		confirmations.clear();
	}
}
