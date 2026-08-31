package com.wardrobe.util;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;

public class NativeFileDialog {
    public static File openImagePicker() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer filterPatterns = stack.mallocPointer(2);
            filterPatterns.put(stack.UTF8("*.png"));
            filterPatterns.put(stack.UTF8("*.gif"));
            filterPatterns.flip();

            String result = TinyFileDialogs.tinyfd_openFileDialog(
                    "Оберіть текстуру плаща / крил (PNG / GIF)",
                    "",
                    filterPatterns,
                    "Зображення (*.png, *.gif)",
                    false
            );

            if (result != null && !result.isEmpty()) {
                return new File(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
