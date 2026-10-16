package fr.hyping.clientlanguagespoofer.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/** Vanilla language list, search, narration and keyboard behavior; no global language change. */
public final class ServerLanguageScreen extends LanguageSelectScreen {
    private final String selectedLanguage;
    private final Consumer<String> selected;

    public ServerLanguageScreen(Screen parent, String initial, Consumer<String> selected) {
        super(parent, Minecraft.getInstance().options, Minecraft.getInstance().getLanguageManager());
        this.selectedLanguage = initial == null ? Minecraft.getInstance().options.languageCode : initial;
        this.selected = selected;
    }

    @Override
    protected void addContents() {
        super.addContents();
        if (this.languageSelectionList == null) return;
        for (LanguageSelectionList.Entry entry : this.languageSelectionList.children()) {
            if (entry.code.equals(this.selectedLanguage)) {
                this.languageSelectionList.setSelected(entry);
                this.languageSelectionList.centerScrollOn(entry);
                break;
            }
        }
    }

    @Override
    protected void addFooter() {
        LinearLayout footer = this.layout.addToFooter(LinearLayout.vertical().spacing(8));
        footer.defaultCellSetting().alignHorizontallyCenter();
        footer.addChild(new StringWidget(Component.translatable("clientlanguagespoofer.picker_hint"), this.font));
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> this.onDone()).width(200).build());
    }

    @Override
    protected void onDone() {
        if (this.languageSelectionList != null && this.languageSelectionList.getSelected() != null) {
            this.selected.accept(this.languageSelectionList.getSelected().code);
        }
        this.minecraft.gui.setScreen(this.lastScreen);
    }
}
