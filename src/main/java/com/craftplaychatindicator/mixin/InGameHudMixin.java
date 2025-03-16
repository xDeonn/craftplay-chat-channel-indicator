// File: src/main/java/com/craftplaychatindicator/mixin/InGameHudMixin.java

package com.craftplaychatindicator.mixin;

import com.craftplaychatindicator.CraftplayChatChannelIndicator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client.options.hudHidden) return;

        // Only render if player exists and game is not paused
        if (this.client.player != null && !this.client.isPaused()) {
            String plotCoords = CraftplayChatChannelIndicator.getInstance().getCurrentPlot();
            Boolean plotChatEnabled = CraftplayChatChannelIndicator.getInstance().isPlotChat();
            String displayText = "Plot Chat: " + plotCoords;
            int color =  0xFF55FF55; // Red for border, green for plot
            if (plotCoords.equals("Road") || !plotChatEnabled) {
                color =  0xFFFF5555;
                displayText = "Global";
            };

            TextRenderer textRenderer = this.client.textRenderer;

            // Get chat position - 14 pixels above the chat input line
            // This ensures it's always right above the chat regardless of UI scale
            int x = 2; // Same horizontal position as chat
            int y = this.client.getWindow().getScaledHeight() - 27; // Position above chat


            // Draw text background (semi-transparent black)
            int textWidth = textRenderer.getWidth(displayText);
            context.fill(x - 1, y - 1, x + textWidth + 3, y + textRenderer.fontHeight + 1, 0x80000000);

            // Draw text
            context.drawTextWithShadow(textRenderer, Text.literal(displayText), x + 1, y, color);
        }
    }
}