package com.wardrobe.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wardrobe.auth.ElyAuthManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ProfileButtonWidget extends ButtonWidget {

    public ProfileButtonWidget(int x, int y, int width, int height, PressAction onPress) {
        super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean hovered = this.isHovered();
        boolean auth = ElyAuthManager.isAuthenticated();

        // Фон кнопки з градієнтом і заокругленим виглядом
        int bgColor = hovered ? 0xDD2A2D34 : 0xBB181A20;
        int borderColor = hovered ? (auth ? 0xFF38EF7D : 0xFF58A6FF) : 0x66FFFFFF;

        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
        context.drawBorder(this.getX(), this.getY(), this.width, this.height, borderColor);

        // Аватарка голови гравця
        Identifier skin = ElyAuthManager.getSkinTextureId();
        int avatarX = this.getX() + 4;
        int avatarY = this.getY() + 4;
        int avatarSize = this.height - 8;

        if (skin != null) {
            RenderSystem.setShaderTexture(0, skin);
            // Лице
            context.drawTexture(skin, avatarX, avatarY, avatarSize, avatarSize, 8, 8, 8, 8, 64, 64);
            // Шолом
            context.drawTexture(skin, avatarX, avatarY, avatarSize, avatarSize, 40, 8, 8, 8, 64, 64);
        } else {
            context.fill(avatarX, avatarY, avatarX + avatarSize, avatarY + avatarSize, 0x55555555);
            context.drawCenteredTextWithShadow(client.textRenderer, "?", avatarX + avatarSize / 2, avatarY + 3, 0xCCCCCC);
        }

        // Текстовий опис та статус
        int textX = avatarX + avatarSize + 6;
        if (auth) {
            String name = ElyAuthManager.getUsername();
            if (name.length() > 9) name = name.substring(0, 7) + "..";
            context.drawTextWithShadow(client.textRenderer, "§f" + name, textX, this.getY() + 4, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "§a● Онлайн (Ely)", textX, this.getY() + 14, 0x55FF55);
        } else {
            context.drawTextWithShadow(client.textRenderer, "§eУвійти", textX, this.getY() + 4, 0xFFFF55);
            context.drawTextWithShadow(client.textRenderer, "§7● Не підключено", textX, this.getY() + 14, 0x888888);
        }
    }
}
