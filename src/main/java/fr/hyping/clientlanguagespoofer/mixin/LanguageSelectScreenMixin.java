package fr.hyping.clientlanguagespoofer.mixin;

import fr.hyping.clientlanguagespoofer.ClientLanguageSpoofer;
import fr.hyping.clientlanguagespoofer.ui.LanguageButtons;
import fr.hyping.clientlanguagespoofer.ui.ServerLanguageScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageSelectScreen.class)
public abstract class LanguageSelectScreenMixin extends OptionsSubScreen {
    @Unique private boolean clientLanguageSpoofer$hasControls;

    @Unique private LanguageButtons clientLanguageSpoofer$buttons;

    protected LanguageSelectScreenMixin(Screen parent, Options options, Component title) { super(parent, options, title); }

    @Override
    public void added() {
        super.added();
        if (this.clientLanguageSpoofer$buttons != null) this.clientLanguageSpoofer$buttons.refresh();
    }

    @Inject(method = "addTitle", at = @At("TAIL"))
    private void clientLanguageSpoofer$controls(CallbackInfo callback) {
        this.clientLanguageSpoofer$hasControls = false;
        if ((Object) this instanceof ServerLanguageScreen || this.minecraft.getConnection() == null || this.minecraft.hasSingleplayerServer()) return;
        String address = ClientLanguageSpoofer.currentAddress();
        if (address == null) return;
        this.clientLanguageSpoofer$hasControls = true;
        this.layout.setHeaderHeight(this.layout.getHeaderHeight() + 24);
        this.clientLanguageSpoofer$buttons = new LanguageButtons(this::addRenderableWidget, Math.max(4, this.width - 254), 4, Math.min(250, this.width - 8),
            () -> ClientLanguageSpoofer.overrides().get(address), () -> this.minecraft.gui.setScreen(
                new ServerLanguageScreen((Screen) (Object) this, ClientLanguageSpoofer.overrides().get(address),
                    code -> ClientLanguageSpoofer.apply(address, code))), () -> ClientLanguageSpoofer.apply(address, null));
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void clientLanguageSpoofer$headerSpacing(CallbackInfo callback) {
        if (!this.clientLanguageSpoofer$hasControls) return;
        for (GuiEventListener child : this.children()) {
            if (child instanceof EditBox search) search.setY(search.getY() + 12);
            else if (child instanceof StringWidget text && text.getMessage().equals(this.title)) text.setY(text.getY() + 12);
        }
    }
}
