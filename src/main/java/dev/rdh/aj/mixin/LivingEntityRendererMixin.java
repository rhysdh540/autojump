package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@ModifyExpressionValue(method = "renderModel", at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvisible()Z")})
	private boolean modifyIsInvisibleToPlayer(boolean original, net.minecraft.entity.LivingEntity entity) {
		return !(entity instanceof PlayerEntity) && original;
	}
}
