package fr.hyping.clientlanguagespoofer.mixin;

import fr.hyping.clientlanguagespoofer.ClientLanguageSpoofer;
import net.minecraft.client.Options;
import net.minecraft.server.level.ClientInformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Inject(method = "buildPlayerInformation", at = @At("RETURN"), cancellable = true)
    private void clientLanguageSpoofer$language(CallbackInfoReturnable<ClientInformation> callback) {
        ClientInformation original = callback.getReturnValue();
        String language = ClientLanguageSpoofer.reportedLanguage(original.language());
        if (language.equals(original.language())) return;
        callback.setReturnValue(new ClientInformation(language, original.viewDistance(), original.chatVisibility(),
            original.chatColors(), original.modelCustomisation(), original.mainHand(), original.textFilteringEnabled(),
            original.allowsListing(), original.particleStatus()));
    }
}
