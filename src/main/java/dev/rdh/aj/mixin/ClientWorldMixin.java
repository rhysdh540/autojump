package dev.rdh.aj.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rdh.aj.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;

import java.util.function.Consumer;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
	@Inject(method = "addEntity(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"))
	private void a(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof FireballEntity) {
			Minecraft.getInstance().player.sendMessage(new LiteralText("Fireball!").setStyle(new Style().setBold(true).setColor(Formatting.DARK_RED)));
			Minecraft.getInstance().player.playSound("mob.cow.hurt", Float.MAX_VALUE, 1.0F);
		} else if (entity instanceof ProjectileEntity) {
			Minecraft.getInstance().player.sendMessage(new LiteralText("Arrow!").setStyle(new Style().setBold(true).setColor(Formatting.DARK_AQUA)));
			Minecraft.getInstance().player.playSound("mob.zombie.death", Float.MAX_VALUE, 1.0F);
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;tick()V"))
	private void onTick(CallbackInfo ci) {
		for(Consumer<ClientWorld> ticker : Setting.WORLD_TICKERS) {
			ticker.accept((ClientWorld)(Object)this);
		}
	}
}
