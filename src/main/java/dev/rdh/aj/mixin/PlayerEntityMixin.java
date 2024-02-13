package dev.rdh.aj.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.rdh.aj.AutoJump;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(World arg) { super(arg); }

	@Override
	protected float getJumpVelocity() {
		float f = super.getJumpVelocity();
		if(AutoJump.isHjEnabled()) {
			return f * 1.75F;
		} else {
			return f;
		}
	}
}
