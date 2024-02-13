package dev.rdh.aj.mixin;

import io.github.axolotlclient.util.FeatureDisabler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;

import java.util.function.Supplier;

@Mixin(FeatureDisabler.class)
public class FeatureDisablerMixin {
	@Inject(method = "setServers", at = @At("HEAD"), cancellable = true, remap = false)
	private static void aj$dontSetServers(BooleanOption option, Supplier<Boolean> condition, String[] servers, CallbackInfo ci) {
		ci.cancel();
	}
}
