package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
	@ModifyExpressionValue(method = "renderHand", at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;isInvisible()Z")})
	private boolean modifyIsInvisibleToPlayer(boolean original, LivingEntity entity) {
		return !(entity instanceof PlayerEntity) && original;
	}
}
