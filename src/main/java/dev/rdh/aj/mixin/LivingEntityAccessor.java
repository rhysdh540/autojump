package dev.rdh.aj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
	@Accessor("jumpingCooldown")
	int getJumpingCooldown();

	@Accessor("jumpingCooldown")
	void setJumpingCooldown(int jumpingCooldown);
}
