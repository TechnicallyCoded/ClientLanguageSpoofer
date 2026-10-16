package fr.hyping.clientlanguagespoofer.mixin;

import fr.hyping.clientlanguagespoofer.ClientLanguageSpoofer;
import fr.hyping.clientlanguagespoofer.ui.LanguageButtons;
import fr.hyping.clientlanguagespoofer.ui.ServerLanguageScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ManageServerScreen.class)
public abstract class ManageServerScreenMixin extends Screen {
    @Shadow @Final private ServerData serverData;
    @Shadow private EditBox ipEdit;
    @Shadow private EditBox nameEdit;
    @Unique private boolean clientLanguageSpoofer$initialized;
    @Unique private String clientLanguageSpoofer$selection;
    @Unique private String clientLanguageSpoofer$draftIp;
    @Unique private String clientLanguageSpoofer$draftName;

    protected ManageServerScreenMixin(Component title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void clientLanguageSpoofer$controls(CallbackInfo callback) {
        if (!this.clientLanguageSpoofer$initialized) {
            this.clientLanguageSpoofer$selection = ClientLanguageSpoofer.overrides().get(this.serverData.ip);
            this.clientLanguageSpoofer$initialized = true;
        }
        if (this.clientLanguageSpoofer$draftIp != null) {
            this.ipEdit.setValue(this.clientLanguageSpoofer$draftIp);
            this.nameEdit.setValue(this.clientLanguageSpoofer$draftName);
            this.clientLanguageSpoofer$draftIp = null;
            this.clientLanguageSpoofer$draftName = null;
        }
        // Add one row before the resource-pack and Done/Cancel controls.
        int row = this.height / 4 + 72;
        for (GuiEventListener child : this.children()) {
            if (child instanceof AbstractWidget widget && widget != this.ipEdit && widget != this.nameEdit && widget.getY() >= row) {
                widget.setY(widget.getY() + 24);
            }
        }
        new LanguageButtons(this::addRenderableWidget, this.width / 2 - 100, row, 200,
            () -> this.clientLanguageSpoofer$selection, () -> {
                this.clientLanguageSpoofer$draftIp = this.ipEdit.getValue();
                this.clientLanguageSpoofer$draftName = this.nameEdit.getValue();
                this.minecraft.gui.setScreen(new ServerLanguageScreen((Screen) (Object) this,
                    this.clientLanguageSpoofer$selection, code -> this.clientLanguageSpoofer$selection = code));
            }, () -> this.clientLanguageSpoofer$selection = null);
    }

    @Inject(method = "onAdd", at = @At("HEAD"), cancellable = true)
    private void clientLanguageSpoofer$save(CallbackInfo callback) {
        if (!ClientLanguageSpoofer.apply(this.ipEdit.getValue(), this.clientLanguageSpoofer$selection)) callback.cancel();
    }
}
