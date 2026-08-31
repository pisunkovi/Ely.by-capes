package com.wardrobe.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wardrobe.auth.ElyAuthManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.net.URI;

public class ElyProfileScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget usernameField;
    private TextFieldWidget tokenField;

    public ElyProfileScreen(Screen parent) {
        super(Text.literal("Акаунт Ely.by"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cardW = 280;
        int cardH = 220;
        int cardX = (this.width - cardW) / 2;
        int cardY = (this.height - cardH) / 2;

        this.usernameField = new TextFieldWidget(this.textRenderer, cardX + 30, cardY + 70, 220, 20, Text.literal("Нікнейм"));
        this.usernameField.setText(ElyAuthManager.getUsername());
        this.usernameField.setMaxLength(32);
        this.addDrawableChild(this.usernameField);

        this.tokenField = new TextFieldWidget(this.textRenderer, cardX + 30, cardY + 110, 220, 20, Text.literal("Токен / Пароль"));
        this.tokenField.setMaxLength(64);
        this.addDrawableChild(this.tokenField);

        // Кнопка Увійти / Зберегти
        this.addDrawableChild(ButtonWidget.builder(Text.literal("✔ Підключити"), btn -> {
            if (!usernameField.getText().trim().isEmpty()) {
                ElyAuthManager.login(usernameField.getText(), tokenField.getText());
                this.client.setScreen(this.parent);
            }
        }).dimensions(cardX + 30, cardY + 140, 105, 22).build());

        // Кнопка Реєстрація на Ely.by
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🌐 Реєстрація"), btn -> {
            Util.getOperatingSystem().open(URI.create("https://account.ely.by/register"));
        }).dimensions(cardX + 145, cardY + 140, 105, 22).build());

        if (ElyAuthManager.isAuthenticated()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("❌ Вийти з акаунту"), btn -> {
                ElyAuthManager.logout();
                this.client.setScreen(this.parent);
            }).dimensions(cardX + 30, cardY + 168, 220, 20).build());
        }

        // Кнопка Назад
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Повернутися"), btn -> {
            this.client.setScreen(this.parent);
        }).dimensions(cardX + 30, cardY + 192, 220, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int cardW = 280;
        int cardH = 220;
        int cardX = (this.width - cardW) / 2;
        int cardY = (this.height - cardH) / 2;

        // Гарна скляна панель-картка
        context.fill(cardX, cardY, cardX + cardW, cardY + cardH, 0xEE14171E);
        context.drawBorder(cardX, cardY, cardW, cardH, 0xFF3B82F6);

        // Заголовок
        context.drawCenteredTextWithShadow(this.textRenderer, "§6★ §fАвторизація Ely.by §6★", this.width / 2, cardY + 15, 0xFFFFFF);
        
        // Аватарка в шапці вікна
        Identifier skin = ElyAuthManager.getSkinTextureId();
        if (skin != null) {
            RenderSystem.setShaderTexture(0, skin);
            context.drawTexture(skin, cardX + cardW - 38, cardY + 10, 24, 24, 8, 8, 8, 8, 64, 64);
            context.drawTexture(skin, cardX + cardW - 38, cardY + 10, 24, 24, 40, 8, 8, 8, 64, 64);
            context.drawBorder(cardX + cardW - 38, cardY + 10, 24, 24, 0xFF22C55E);
        }

        context.drawTextWithShadow(this.textRenderer, "§7Нікнейм користувача:", cardX + 30, cardY + 58, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "§7Токен доступу / Пароль:", cardX + 30, cardY + 98, 0xAAAAAA);
    }
}
