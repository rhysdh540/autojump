package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.aj.AutoJump;

import net.minecraft.entity.living.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Shadow
	private int jumpingCooldown;

	@ModifyExpressionValue(method = "tickAi", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;onGround:Z"))
	private boolean tickMovement(boolean original) {
		return original || AutoJump.AIRHOP.enabled();
	}

	@ModifyExpressionValue(method = "tickAi", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/LivingEntity;jumpingCooldown:I", ordinal = 3))
	private int tickMovement2(int original) {
		return AutoJump.AIRHOP.enabled() ? original : jumpingCooldown;
	}
}
