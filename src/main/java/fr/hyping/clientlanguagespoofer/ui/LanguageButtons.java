package fr.hyping.clientlanguagespoofer.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LanguageButtons {
    private final Button choose;
    private final Button reset;
    private final Supplier<String> selection;
    private final int x;
    private final int width;

    public LanguageButtons(Consumer<Button> add, int x, int y, int width, Supplier<String> selection,
                           Runnable open, Runnable clear) {
        this.selection = selection;
        this.x = x;
        this.width = width;
        this.choose = Button.builder(Component.empty(), button -> open.run()).bounds(x, y, width, 20).build();
        this.reset = Button.builder(Component.translatable("controls.reset"), button -> {
            clear.run();
            refresh();
        }).bounds(x + width - 44, y, 44, 20).build();
        add.accept(this.choose);
        add.accept(this.reset);
        refresh();
    }

    public void refresh() {
        String code = this.selection.get();
        LanguageInfo info = code == null ? null : Minecraft.getInstance().getLanguageManager().getLanguage(code);
        Component name = code == null ? Component.translatable("clientlanguagespoofer.default")
            : Component.literal(info == null ? code : info.name());
        this.choose.setMessage(Component.translatable("clientlanguagespoofer.button", name));
        this.choose.setTooltip(Tooltip.create(Component.translatable("clientlanguagespoofer.tooltip")));
        this.reset.visible = code != null;
        this.reset.active = code != null;
        this.choose.setX(this.x);
        this.choose.setWidth(this.width - (code == null ? 0 : 48));
    }
}
