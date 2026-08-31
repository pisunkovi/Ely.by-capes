package com.wardrobe.util;

import com.wardrobe.client.texture.AnimatedGifTexture;
import net.minecraft.client.texture.NativeImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public class GifDecoder {
    public static AnimatedGifTexture decode(File gifFile, String prefix) {
        AnimatedGifTexture animatedTexture = new AnimatedGifTexture();
        try (ImageInputStream stream = ImageIO.createImageInputStream(new FileInputStream(gifFile))) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) return null;

            ImageReader reader = readers.next();
            reader.setInput(stream);
            int count = reader.getNumImages(true);

            for (int i = 0; i < count; i++) {
                BufferedImage frame = reader.read(i);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(frame, "png", baos);
                NativeImage nativeImage = NativeImage.read(baos.toByteArray());
                animatedTexture.addFrame(nativeImage, 100, prefix, i);
            }
            reader.dispose();
            return animatedTexture;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
