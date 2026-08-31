package com.wardrobe.mixin;

import com.wardrobe.auth.ElyAuthManager;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    // Усунення відсікання плаща при надягнених елітрах для синхронного рендеру
}
