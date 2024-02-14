package dev.rdh.aj;

import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.input.Keyboard;

import dev.rdh.aj.Setting.ChatMessageSetting;
import dev.rdh.aj.mixin.LivingEntityAccessor;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

public class AutoJump implements ClientModInitializer {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private static List<KeyBinding> keys = null;

	public static final Setting AUTOJUMP = new ChatMessageSetting("AutoJump", Keyboard.KEY_BACKSLASH, "movement", aj -> {
		if(MC.player.onGround && !MC.player.isSneaking()
				&& !MC.options.sneakKey.isPressed() && !MC.options.jumpKey.isPressed()
				&& MC.world.doesBoxCollide(MC.player,
				MC.player.getBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)).isEmpty()) {
			MC.player.jump();
		}
	});

	public static final Setting INVWALK = new ChatMessageSetting("InvWalk", Keyboard.KEY_MINUS, "movement", iw -> {
		Screen screen = MC.currentScreen;
		if(screen instanceof ChatScreen || screen instanceof GameMenuScreen) {
			return;
		}

		if(keys == null) {
			keys = Arrays.asList(MC.options.forwardKey, MC.options.backKey, MC.options.leftKey, MC.options.rightKey, MC.options.jumpKey, MC.options.sneakKey, MC.options.sprintKey);
		}

		for(KeyBinding key : keys) {
			KeyBinding.setKeyPressed(key.getCode(), GameOptions.isPressed(key));
		}
	});

	public static final Setting HIGHJUMP = new Setting("HighJump", Keyboard.KEY_EQUALS, "movement",
			hj -> {
				if(((LivingEntityAccessor) MC.player).getJumpingCooldown() != 0 || !MC.player.onGround)
					return;
				MC.player.jump();
				((LivingEntityAccessor) MC.player).setJumpingCooldown(10);
				hj.disable();
			});

	public static final Setting SHOW_BARRIERS = new ChatMessageSetting("ShowBarriers", Keyboard.KEY_BACK, "misc", (sb, world) -> {
		int radius = 5;
		BlockPos playerPos = MC.player.getBlockPos();
		for(int x = -radius; x <= radius; x++) {
			for(int y = -radius; y <= radius; y++) {
				for(int z = -radius; z <= radius; z++) {
					BlockPos pos = playerPos.add(x, y, z);
					if(world.getBlockState(pos).getBlock() == Blocks.BARRIER) {
						world.addParticle(ParticleType.BARRIER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
					}
				}
			}
		}
	});

	public static final Setting NOWEB = new ChatMessageSetting("NoWeb", Keyboard.KEY_SEMICOLON, "movement");

	@Override
	public void onInitializeClient() {

	}
}
