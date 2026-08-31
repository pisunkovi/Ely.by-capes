package com.wardrobe.mixin;

import com.wardrobe.auth.ElyAuthManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CapeFeatureRenderer.class)
public abstract class CapeFeatureRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTextures()Lnet/minecraft/client/util/SkinTextures;"))
    private SkinTextures overrideCapeTexture(AbstractClientPlayerEntity player) {
        SkinTextures original = player.getSkinTextures();
        if (original == null) return null;

        Identifier activeCape = ElyAuthManager.getActiveCapeTexture();
        if (activeCape != null) {
            return new SkinTextures(
                    original.texture(),
                    original.textureUrl(),
                    activeCape,
                    original.elytraTexture(),
                    original.model(),
                    original.secure()
            );
        }
        return original;
    }
}
