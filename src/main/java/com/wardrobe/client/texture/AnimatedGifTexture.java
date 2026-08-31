package com.wardrobe.client.texture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AnimatedGifTexture {
    private final List<Identifier> frameIdentifiers = new ArrayList<>();
    private final List<Integer> frameDelays = new ArrayList<>();
    private int currentFrame = 0;
    private long lastFrameTime = 0;

    public void addFrame(NativeImage image, int delayMs, String prefix, int index) {
        Identifier id = MinecraftClient.getInstance().getTextureManager()
                .registerDynamicTexture(prefix + "_frame_" + index, new NativeImageBackedTexture(image));
        frameIdentifiers.add(id);
        frameDelays.add(delayMs > 0 ? delayMs : 100);
    }

    public Identifier getCurrentTexture() {
        if (frameIdentifiers.isEmpty()) return null;
        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= frameDelays.get(currentFrame)) {
            currentFrame = (currentFrame + 1) % frameIdentifiers.size();
            lastFrameTime = now;
        }
        return frameIdentifiers.get(currentFrame);
    }

    public boolean isEmpty() {
        return frameIdentifiers.isEmpty();
    }
}
