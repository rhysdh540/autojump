package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.aj.AutoJump;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(World arg) { super(arg); }

	@Override
	protected float getJumpVelocity() {
		return AutoJump.HIGHJUMP.enabled() ? 0.8F : super.getJumpVelocity();
	}

	@ModifyExpressionValue(method = "attack", at =
			@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 0)
	)
	private boolean idkWhatThisDoesButIShouldProbablyTurnItOff(boolean original) {
		return !AutoJump.ANTIBLINDNESS.enabled() && original;
	}
}
