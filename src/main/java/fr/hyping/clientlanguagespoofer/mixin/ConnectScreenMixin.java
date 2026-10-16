package fr.hyping.clientlanguagespoofer.mixin;

import fr.hyping.clientlanguagespoofer.ClientLanguageSpoofer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {
    @Inject(method = "connect", at = @At("HEAD"))
    private void clientLanguageSpoofer$connecting(Minecraft client, ServerAddress address, ServerData data,
                                                TransferState transfer, CallbackInfo callback) {
        ClientLanguageSpoofer.connecting(data.ip);
    }
}
