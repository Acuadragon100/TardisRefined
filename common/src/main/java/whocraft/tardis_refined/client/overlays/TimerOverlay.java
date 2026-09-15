package whocraft.tardis_refined.client.overlays;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import whocraft.tardis_refined.client.TardisClientData;
import whocraft.tardis_refined.constants.ModMessages;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TimerOverlay {

    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.00");

    private static final DecimalFormat WHOLE_FORMATTER = new DecimalFormat("00.#");

    static {
        DECIMAL_FORMATTER.setRoundingMode(RoundingMode.FLOOR);
        WHOLE_FORMATTER.setRoundingMode(RoundingMode.FLOOR);
    }

    public static void renderOverlay(GuiGraphics guiGraphics) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player != null) {
            var data = TardisClientData.getInstance(player.level().dimension());
            var deletionTimer = data.getDeletionTimerValue();
            if (deletionTimer.isPresent()) {
                float timeOffset = (System.currentTimeMillis() - data.getLastSyncTime()) / 1000.0f;
                float ticks = deletionTimer.getAsInt() + timeOffset * 20;
                PoseStack poseStack = guiGraphics.pose();

                int seconds = Mth.floor(ticks / 20);
                int minutes = seconds / 60;

                String time = WHOLE_FORMATTER.format(minutes) + ":" + WHOLE_FORMATTER.format(seconds % 60) + DECIMAL_FORMATTER.format((ticks % 20 / 20));

                var msg = Component.translatable(
                        ModMessages.DELETION_TIMER,
                        time
                );

                int x = guiGraphics.guiWidth() - 15 - mc.font.width(msg);
                int y = 15;


                poseStack.pushPose();
                guiGraphics.drawString(
                        mc.font, msg,
                        x, y, ChatFormatting.DARK_RED.getColor()
                );
                poseStack.popPose();
            }
        }
    }

}
