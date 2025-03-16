// File: src/main/java/com/craftplaychatindicator/CraftplayChatChannelIndicator.java

package com.craftplaychatindicator;

import com.mojang.authlib.minecraft.UserApiService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftplayChatChannelIndicator implements ClientModInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger("craftplay-chat-channel-indicator");
	// Static reference to the mod instance
	private static CraftplayChatChannelIndicator instance;

	// Cache for current plot coordinates
	private String currentPlot = "0;0";

	// Track plot chat state
	private boolean plotChatEnabled = false;

	// Chat message constants
	private static final String PLOT_CHAT_ENABLED_MESSAGE = "Dzialki » Wlaczono ustawienie: chat.";
	private static final String PLOT_CHAT_DISABLED_MESSAGE = "Dzialki » Wylaczono ustawienie: chat.";
	// Server warning constants
	private static final String TARGET_SERVER_IP = "craftplay.pl";

	@Override
	public void onInitializeClient() {
		instance = this;

		// Register tick event to update coordinates
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				int x = (int) client.player.getX();
				int z = (int) client.player.getZ();
				currentPlot = PlotUtils.getPlotCoordinates(x, z);
			}
		});

		// Register chat message event to detect plot chat state changes
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			String messageText = message.getString();

			if (messageText.contains(PLOT_CHAT_ENABLED_MESSAGE)) {
				LOGGER.info("Plot chat enabled");
				plotChatEnabled = true;
			} else if (messageText.contains(PLOT_CHAT_DISABLED_MESSAGE)) {
				LOGGER.info("Plot chat disabled");
				plotChatEnabled = false;
			}
		});

		// Reset plot chat state when connecting to a server (since it's off by default)
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			LOGGER.info("Connected to server, resetting plot chat state to disabled");
			plotChatEnabled = false;
			String serverAddress = handler.getConnection().getAddress().toString();
			if (serverAddress.contains(TARGET_SERVER_IP)) {
				LOGGER.warn("Connected to target server: " + TARGET_SERVER_IP);

				// Schedule warning message to appear after player fully joins
				// This ensures the message isn't lost during connection
				client.execute(() -> {
					if (client.player != null) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        client.player.sendMessage(Text.of("§4§lUWAGA!"), false);
						client.player.sendMessage(Text.of("§cSerwer CraftPlay.PL znany jest z niesprawiedliwej administracji i banowania graczy z rangą bez powodu."), false);
						client.player.sendMessage(Text.of("§cZalecamy nie wydawać na niego pieniędzy, gdyż nigdy nie wiesz kiedy stracisz konto przez widzimisię admina."), false);
						client.player.sendMessage(Text.of("§c - xDeon_, Kacperpr"), false);

						// Optional: Make a sound to get player's attention
						// Uncomment the following line if you want a sound alert
						// client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
					}
				});
			}
		});
	}

	/**
	 * Gets the current plot coordinates as a string
	 * @return The plot coordinates
	 */
	public String getCurrentPlot() {
		return currentPlot;
	}

	/**
	 * Checks if plot chat is currently enabled
	 * @return true if plot chat is enabled, false otherwise
	 */
	public boolean isPlotChat() {
		return plotChatEnabled;
	}

	/**
	 * Gets the mod instance
	 * @return The mod instance
	 */
	public static CraftplayChatChannelIndicator getInstance() {
		return instance;
	}
}