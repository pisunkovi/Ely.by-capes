package com.wardrobe.mixin;

import com.wardrobe.auth.ElyAuthManager;
import com.wardrobe.client.gui.WardrobeScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private int wardrobeBtnX;
    @Unique
    private int wardrobeBtnY;

    @Inject(method = "init", at = @At("RETURN"))
    private void addWardrobeButton(CallbackInfo ci) {
        this.wardrobeBtnX = this.width / 2 + 104;
        this.wardrobeBtnY = this.height / 4 + 48;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("🧥 Гардероб"), btn -> {
            if (this.client != null) {
                this.client.setScreen(new WardrobeScreen(this));
            }
        }).dimensions(wardrobeBtnX, wardrobeBtnY, 84, 20).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderPlayerSkinHead(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Identifier skin = ElyAuthManager.getSkinTextureId();
        int headSize = 24;
        int headX = this.wardrobeBtnX + (84 - headSize) / 2;
        int headY = this.wardrobeBtnY - headSize - 4;

        if (skin != null) {
            // Базовий шар голови
            context.drawTexture(skin, headX, headY, headSize, headSize, 8.0f, 8.0f, 8, 8, 64, 64);
            // Шар шолома / волосся
            context.drawTexture(skin, headX, headY, headSize, headSize, 40.0f, 8.0f, 8, 8, 64, 64);
            context.drawBorder(headX, headY, headSize, headSize, 0xFF22C55E);
        } else {
            context.fill(headX, headY, headX + headSize, headY + headSize, 0x88000000);
            context.drawBorder(headX, headY, headSize, headSize, 0x88AAAAAA);
        }
    }
}
