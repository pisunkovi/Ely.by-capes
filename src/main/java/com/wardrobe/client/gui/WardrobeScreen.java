package com.wardrobe.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wardrobe.auth.ElyAuthManager;
import com.wardrobe.client.community.CommunityLibrary;
import com.wardrobe.client.gui.widget.ProfileButtonWidget;
import com.wardrobe.client.texture.AnimatedGifTexture;
import com.wardrobe.util.GifDecoder;
import com.wardrobe.util.NativeFileDialog;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class WardrobeScreen extends Screen {
    private final Screen parent;
    private int currentTab = 0; // 0 = Capes, 1 = Elytras, 2 = Community Catalog
    private ButtonWidget capesBtn;
    private ButtonWidget elytrasBtn;
    private ButtonWidget communityBtn;
    private ButtonWidget uploadBtn;
    private ButtonWidget syncToggleBtn;
    private float playerRotation = 0f;

    public WardrobeScreen(Screen parent) {
        super(Text.literal("Гардероб"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Кастомна преміальна кнопка профілю
        this.addDrawableChild(new ProfileButtonWidget(12, 12, 130, 26, btn -> {
            this.client.setScreen(new ElyProfileScreen(this));
        }));

        // Вкладки по центру
        int tabW = 85;
        int startX = this.width / 2 - (tabW * 3 + 10) / 2;

        this.capesBtn = this.addDrawableChild(ButtonWidget.builder(Text.literal("Плащі"), btn -> {
            this.currentTab = 0;
            updateButtons();
        }).dimensions(startX, 15, tabW, 20).build());

        this.elytrasBtn = this.addDrawableChild(ButtonWidget.builder(Text.literal("Елітри"), btn -> {
            this.currentTab = 1;
            updateButtons();
        }).dimensions(startX + tabW + 5, 15, tabW, 20).build());

        this.communityBtn = this.addDrawableChild(ButtonWidget.builder(Text.literal("Каталог"), btn -> {
            this.currentTab = 2;
            updateButtons();
        }).dimensions(startX + (tabW + 5) * 2, 15, tabW, 20).build());

        // Кнопка завантаження свого файла
        this.uploadBtn = this.addDrawableChild(ButtonWidget.builder(Text.literal("📁 Завантажити PNG/GIF"), btn -> {
            File file = NativeFileDialog.openImagePicker();
            if (file != null) {
                if (file.getName().toLowerCase().endsWith(".gif")) {
                    AnimatedGifTexture gif = GifDecoder.decode(file, "custom_cape_gif");
                    if (gif != null) ElyAuthManager.setAnimatedCape(gif);
                } else {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        NativeImage img = NativeImage.read(fis);
                        Identifier id = this.client.getTextureManager().registerDynamicTexture("custom_user_cosmetic", new NativeImageBackedTexture(img));
                        if (currentTab == 0) ElyAuthManager.setCustomCapeId(id);
                        else ElyAuthManager.setCustomElytraId(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).dimensions(this.width / 2 - 170, this.height - 40, 160, 22).build());

        // Перемикач одночасного рендеру плаща та крил
        this.syncToggleBtn = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Одночасно: " + (ElyAuthManager.isSimultaneousRender() ? "§aВКЛ" : "§cВИКЛ")),
                btn -> {
                    ElyAuthManager.setSimultaneousRender(!ElyAuthManager.isSimultaneousRender());
                    this.syncToggleBtn.setMessage(Text.literal("Одночасно: " + (ElyAuthManager.isSimultaneousRender() ? "§aВКЛ" : "§cВИКЛ")));
                }
        ).dimensions(this.width / 2 - 2, this.height - 40, 110, 22).build());

        // Кнопка Назад
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Закрити"), btn -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 + 115, this.height - 40, 65, 22).build());

        updateButtons();
    }

    private void updateButtons() {
        boolean auth = ElyAuthManager.isAuthenticated();
        if (capesBtn != null) capesBtn.active = auth;
        if (elytrasBtn != null) elytrasBtn.active = auth;
        if (communityBtn != null) communityBtn.active = auth;
        if (uploadBtn != null) uploadBtn.active = auth;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Фоновий градієнт
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        if (!ElyAuthManager.isAuthenticated()) {
            int bannerW = 320;
            int bannerH = 70;
            int bx = (this.width - bannerW) / 2;
            int by = this.height / 2 - 35;
            context.fill(bx, by, bx + bannerW, by + bannerH, 0xEE1B1E26);
            context.drawBorder(bx, by, bannerW, bannerH, 0xFFEF4444);

            context.drawCenteredTextWithShadow(this.textRenderer, "§c⚠ Обов'язкова авторизація Ely.by!", this.width / 2, by + 16, 0xFF5555);
            context.drawCenteredTextWithShadow(this.textRenderer, "§7Натисніть на картку профілю у лівому кутку,", this.width / 2, by + 32, 0xAAAAAA);
            context.drawCenteredTextWithShadow(this.textRenderer, "§7щоб увійти або зареєструватись.", this.width / 2, by + 46, 0xAAAAAA);
            return;
        }

        // 3D Модель персонажа у лівій частині екрана
        int playerBoxX = this.width / 4;
        int playerBoxY = this.height / 2 + 45;
        playerRotation += delta * 1.5f;

        if (this.client.player != null) {
            InventoryScreen.drawEntity(context, playerBoxX - 25, playerBoxY - 70, playerBoxX + 25, playerBoxY + 10, 42, 0.0625f, (float)(playerBoxX - mouseX), (float)(playerBoxY - 50 - mouseY), this.client.player);
        }

        // Права робоча область
        int panelX = this.width / 2 - 30;
        int panelY = 48;
        int panelW = this.width / 2 + 15;
        int panelH = this.height - 100;
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xAA12141A);
        context.drawBorder(panelX, panelY, panelW, panelH, 0x44FFFFFF);

        if (currentTab == 0) {
            context.drawTextWithShadow(this.textRenderer, "§6✦ Каталог плащів (GIF / PNG):", panelX + 15, panelY + 15, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "§7Активний плащ: " + (ElyAuthManager.getActiveCapeTexture() != null ? "§aВстановлено" : "§8Не обрано"), panelX + 15, panelY + 35, 0xAAAAAA);
        } else if (currentTab == 1) {
            context.drawTextWithShadow(this.textRenderer, "§b✦ Скіни для Елітр:", panelX + 15, panelY + 15, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "§7Активні крила: " + (ElyAuthManager.getCustomElytraId() != null ? "§aВстановлено" : "§8Стандартні"), panelX + 15, panelY + 35, 0xAAAAAA);
        } else {
            context.drawTextWithShadow(this.textRenderer, "§d✦ Пресети спільноти Ely.by:", panelX + 15, panelY + 15, 0xFFFFFF);
            List<CommunityLibrary.CosmeticItem> list = CommunityLibrary.getItems("cape");
            int itemY = panelY + 40;
            for (CommunityLibrary.CosmeticItem item : list) {
                context.fill(panelX + 15, itemY, panelX + panelW - 15, itemY + 24, 0x44222630);
                context.drawTextWithShadow(this.textRenderer, "§f" + item.name + " §7(by " + item.author + ")", panelX + 22, itemY + 8, 0xFFFFFF);
                itemY += 28;
            }
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
