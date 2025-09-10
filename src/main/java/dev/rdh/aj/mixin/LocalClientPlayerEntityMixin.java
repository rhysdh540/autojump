package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.aj.AutoJump;

import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;

@Mixin(LocalClientPlayerEntity.class)
public class LocalClientPlayerEntityMixin {
	@ModifyExpressionValue(method = "tickAi", at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/living/effect/StatusEffect;)Z", ordinal = 1),
			@At(value = "INVOKE", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/living/effect/StatusEffect;)Z", ordinal = 2)
	})
	private boolean allowSprinting(boolean original) {
		return !AutoJump.ANTIBLINDNESS.enabled() && original;
	}
}
