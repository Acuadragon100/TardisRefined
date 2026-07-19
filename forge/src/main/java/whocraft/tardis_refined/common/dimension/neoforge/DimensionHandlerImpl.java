package whocraft.tardis_refined.common.dimension.neoforge;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

public class DimensionHandlerImpl {

    public static <T> void unfreezeRegistry(MappedRegistry<T> registry) {
        registry.unfreeze(); // This Neoforge method is deprecated so we may need to use an Accessor Mixin in the future.
    }

    public static void onDimensionLoaded(ServerLevel level) {
        level.getServer().markWorldsDirty();
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
    }

    public static void onDimensionUnloaded(ServerLevel level) {
        level.getServer().markWorldsDirty();
        NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
    }

}
