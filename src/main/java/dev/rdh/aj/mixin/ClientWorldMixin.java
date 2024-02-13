package dev.rdh.aj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import dev.rdh.aj.AutoJump;

import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
	@ModifyVariable(method = "spawnRandomParticles", at = @At("LOAD"))
	private boolean injectBlockParticle(boolean value) {
		return AutoJump.isSbEnabled() || value;
	}
}
