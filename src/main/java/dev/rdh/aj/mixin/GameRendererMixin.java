package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.aj.AutoJump;

import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyExpressionValue(method = "renderFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/living/effect/StatusEffect;)Z", ordinal = 0))
	private boolean disableBlindnessFog(boolean original) {
		return original && !AutoJump.ANTIBLINDNESS.enabled();
	}
}
