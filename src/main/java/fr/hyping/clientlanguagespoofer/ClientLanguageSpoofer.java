package fr.hyping.clientlanguagespoofer;

import fr.hyping.clientlanguagespoofer.config.LanguageOverrides;
import fr.hyping.clientlanguagespoofer.config.ServerKey;
import fr.hyping.clientlanguagespoofer.mixin.ClientPacketListenerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.server.level.ClientInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class ClientLanguageSpoofer implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("clientlanguagespoofer");
    private static LanguageOverrides overrides;
    private static volatile String connectingAddress;

    @Override
    public void onInitializeClient() {
        overrides = new LanguageOverrides(FabricLoader.getInstance().getConfigDir().resolve("client-language-spoofer.json"));
        try {
            overrides.load();
        } catch (IOException exception) {
            LOGGER.error("Could not load server language overrides", exception);
        }
    }

    public static LanguageOverrides overrides() {
        return overrides;
    }

    public static void connecting(String address) {
        connectingAddress = address;
    }

    public static void disconnected() {
        connectingAddress = null;
    }

    public static String currentAddress() {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer()) return null;
        ServerData data = client.getCurrentServer();
        return data == null ? connectingAddress : data.ip;
    }

    public static String reportedLanguage(String original) {
        if (overrides == null) return original;
        String override = overrides.get(currentAddress());
        if (override == null || !Minecraft.getInstance().getLanguageManager().getLanguages().containsKey(override)) return original;
        return override;
    }

    public static boolean apply(String address, String language) {
        try {
            overrides.set(address, language);
        } catch (IOException | IllegalArgumentException exception) {
            LOGGER.error("Could not save server language override", exception);
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) client.player.sendSystemMessage(Component.translatable("clientlanguagespoofer.save_failed"));
            return false;
        }
        String key = ServerKey.of(address);
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        if (connection != null && key != null && key.equals(ServerKey.of(currentAddress()))) {
            ClientInformation information = client.options.buildPlayerInformation();
            // Keep vanilla's comparison cache consistent, but always send when the user applies/reset an override.
            ((ClientPacketListenerAccessor) connection).clientLanguageSpoofer$setRemoteInformation(information);
            connection.send(new ServerboundClientInformationPacket(information));
        }
        return true;
    }
}
