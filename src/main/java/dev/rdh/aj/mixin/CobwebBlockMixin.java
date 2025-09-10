package dev.rdh.aj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rdh.aj.AutoJump;

import net.minecraft.block.state.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(CobwebBlock.class)
public class CobwebBlockMixin {
	@Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
	private void aj$disableEntityCollision(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
		if(AutoJump.NOWEB.enabled()) {
			ci.cancel();
		}
	}
}
