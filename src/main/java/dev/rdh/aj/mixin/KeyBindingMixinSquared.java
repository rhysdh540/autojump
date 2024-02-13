package dev.rdh.aj.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.option.KeyBinding;

@Mixin(value = KeyBinding.class, priority = 1001)
public class KeyBindingMixinSquared {

	@TargetHandler(
			mixin = "io.github.axolotlclient.mixin.KeyBindingMixin",
			name = "axolotlclient$noMovementFixAfterInventory"
	)
	@Inject(
			method = "@MixinSquared:Handler",
			at = @At("HEAD"),
			cancellable = true,
			require = 0,
			remap = false
	)
	private void aj$noAxolotlClientYouMayNotTurnOffInvWalk(CallbackInfoReturnable<Boolean> cir, CallbackInfo ci) {
		ci.cancel();
	}
}
