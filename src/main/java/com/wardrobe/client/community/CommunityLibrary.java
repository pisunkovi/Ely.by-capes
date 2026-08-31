package com.wardrobe.client.community;

import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

public class CommunityLibrary {
    public static class CosmeticItem {
        public final String name;
        public final String type; // "cape" або "elytra"
        public final String author;
        public final Identifier textureId;
        public final boolean isGif;

        public CosmeticItem(String name, String type, String author, Identifier textureId, boolean isGif) {
            this.name = name;
            this.type = type;
            this.author = author;
            this.textureId = textureId;
            this.isGif = isGif;
        }
    }

    private static final List<CosmeticItem> items = new ArrayList<>();

    public static void loadCommunityPresets() {
        items.clear();
        // Пресети спільноти за замовчуванням
        items.add(new CosmeticItem("Ely.by Original", "cape", "Ely.by", Identifier.ofVanilla("textures/entity/cape.png"), false));
        items.add(new CosmeticItem("Dragon Wings", "elytra", "Mojang", Identifier.ofVanilla("textures/entity/elytra.png"), false));
    }

    public static List<CosmeticItem> getItems(String type) {
        List<CosmeticItem> filtered = new ArrayList<>();
        for (CosmeticItem item : items) {
            if (item.type.equalsIgnoreCase(type)) {
                filtered.add(item);
            }
        }
        return filtered;
    }
}
