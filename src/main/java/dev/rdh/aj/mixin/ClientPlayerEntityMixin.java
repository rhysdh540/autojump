package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import dev.rdh.aj.AutoJump;

import net.minecraft.entity.player.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
	@ModifyExpressionValue(method = "tickMovement", at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 1),
			@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 2)
	})
	private boolean allowSprinting(boolean original) {
		return !AutoJump.ANTIBLINDNESS.enabled() && original;
	}
}
