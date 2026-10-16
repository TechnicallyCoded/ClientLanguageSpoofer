package fr.hyping.clientlanguagespoofer.mixin;

import fr.hyping.clientlanguagespoofer.ClientLanguageSpoofer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At("HEAD"))
    private void clientLanguageSpoofer$disconnected(CallbackInfo callback) {
        ClientLanguageSpoofer.disconnected();
    }
}
