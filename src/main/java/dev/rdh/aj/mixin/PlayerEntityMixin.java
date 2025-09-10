package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.aj.AutoJump;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(World arg) { super(arg); }

	@Override
	protected float getJumpStrength() {
		return AutoJump.HIGHJUMP.enabled() ? 0.8F : super.getJumpStrength();
	}

	@ModifyExpressionValue(method = "attack", at =
			@At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/entity/living/effect/StatusEffect;)Z", ordinal = 0)
	)
	private boolean idkWhatThisDoesButIShouldProbablyTurnItOff(boolean original) {
		return !AutoJump.ANTIBLINDNESS.enabled() && original;
	}
}
