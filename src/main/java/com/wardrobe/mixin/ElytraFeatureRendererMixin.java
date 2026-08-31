package com.wardrobe.mixin;

import com.wardrobe.auth.ElyAuthManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElytraFeatureRenderer.class)
public abstract class ElytraFeatureRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTextures()Lnet/minecraft/client/util/SkinTextures;"))
    private SkinTextures overrideElytraTexture(AbstractClientPlayerEntity player) {
        SkinTextures original = player.getSkinTextures();
        if (original == null) return null;

        Identifier customElytra = ElyAuthManager.getCustomElytraId();
        if (customElytra != null) {
            return new SkinTextures(
                    original.texture(),
                    original.textureUrl(),
                    original.capeTexture(),
                    customElytra,
                    original.model(),
                    original.secure()
            );
        }
        return original;
    }
}
