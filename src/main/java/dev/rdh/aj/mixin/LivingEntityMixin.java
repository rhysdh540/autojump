package dev.rdh.aj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.rdh.aj.AutoJump;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Shadow
	private int jumpingCooldown;

	@Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;onGround:Z"))
	private boolean tickMovement(LivingEntity livingEntity) {
		return AutoJump.AIRHOP.enabled() || livingEntity.onGround;
	}

	@Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;jumpingCooldown:I", ordinal = 3))
	private int tickMovement2(LivingEntity livingEntity) {
		return AutoJump.AIRHOP.enabled() ? 0 : jumpingCooldown;
	}
}
