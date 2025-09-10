package dev.rdh.aj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
	@Inject(method = "spawnEntity", at = @At("HEAD"))
	private void a(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof FireballEntity) {
			MinecraftClient.getInstance().player.sendMessage(new LiteralText("Fireball!").setStyle(new Style().setBold(true).setFormatting(Formatting.DARK_RED)));
			MinecraftClient.getInstance().player.playSound("mob.cow.hurt", Float.MAX_VALUE, 1.0F);
		} else if (entity instanceof AbstractArrowEntity) {
			MinecraftClient.getInstance().player.sendMessage(new LiteralText("Arrow!").setStyle(new Style().setBold(true).setFormatting(Formatting.DARK_AQUA)));
			MinecraftClient.getInstance().player.playSound("mob.zombie.death", Float.MAX_VALUE, 1.0F);
		}
	}
}
