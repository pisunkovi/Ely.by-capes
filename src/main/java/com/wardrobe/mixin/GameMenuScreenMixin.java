package com.wardrobe.mixin;

import com.wardrobe.client.gui.WardrobeScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    private void addWardrobeButtonToPauseMenu(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🧥 Гардероб"), btn -> {
            this.client.setScreen(new WardrobeScreen(this));
        }).dimensions(10, 10, 90, 20).build());
    }
}
