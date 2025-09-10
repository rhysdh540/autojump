package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import dev.rdh.aj.AutoJump;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@ModifyExpressionValue(
			method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/KeyBinding;consumeClick()Z"),
			slice = @Slice(
					from = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;attackKey:Lnet/minecraft/client/options/KeyBinding;", ordinal = 1)
			)
	)
	private boolean twiceTheClick(boolean original) {
		return original || AutoJump.ATTACK_2.consumeClick();
	}
}
