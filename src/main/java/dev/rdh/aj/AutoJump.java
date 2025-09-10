package dev.rdh.aj;

import org.lwjgl.input.Keyboard;

import dev.rdh.aj.Setting.ChatMessageSetting;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

@Mod(modid = "aj", useMetadata = true)
public class AutoJump  {
	private static final Minecraft MC = Minecraft.getInstance();

	public static final KeyBinding ATTACK_2 = new KeyBinding(
			"key.aj.attack2",
			Keyboard.KEY_LBRACKET,
			"key.categories.gameplay"
	);

	static {
		ClientRegistry.registerKeyBinding(ATTACK_2);
	}

	private static List<KeyBinding> keys = null;

	public static final Setting AUTOJUMP = new ChatMessageSetting("AutoJump", Keyboard.KEY_BACKSLASH, "movement", aj -> {
		if(MC.player.onGround && !MC.player.isSneaking()
				&& !MC.options.sneakKey.isPressed() && !MC.options.jumpKey.isPressed()
				&& MC.world.getCollisions(MC.player,
				MC.player.getShape().move(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)).isEmpty()) {
			MC.player.jump();
		}
	});

	public static final Setting INVWALK = new ChatMessageSetting("InvWalk", Keyboard.KEY_MINUS, "movement", iw -> {
		Screen screen = MC.screen;
		if(screen instanceof ChatScreen || screen instanceof GameMenuScreen) {
			return;
		}

		if(keys == null) {
			keys = Arrays.asList(MC.options.forwardKey, MC.options.backKey, MC.options.leftKey, MC.options.rightKey,
					MC.options.jumpKey, MC.options.sneakKey, MC.options.sprintKey);
		}

		for(KeyBinding key : keys) {
			KeyBinding.set(key.getKeyCode(), Keyboard.isKeyDown(key.getKeyCode()));
		}
	});

	public static final Setting AIRHOP = new ChatMessageSetting("AirHop", Keyboard.KEY_O, "movement");

	public static final Setting HIGHJUMP = new Setting("HighJump", Keyboard.KEY_EQUALS, "movement", hj -> {
		if(!MC.player.onGround && !AIRHOP.enabled())
			return;
		MC.player.jump();
		hj.disable();
	});


	public static final Setting SHOW_BARRIERS = new ChatMessageSetting("ShowBarriers", Keyboard.KEY_BACK, "misc", (sb, world) -> {
		int radius = 16;
		BlockPos playerPos = MC.player.getSourceBlockPos();
		for(int x = -radius; x <= radius; x++) {
			for(int y = -radius; y <= radius; y++) {
				for(int z = -radius; z <= radius; z++) {
					BlockPos pos = playerPos.add(x, y, z);
					if(!world.isChunkLoaded(pos)) continue;
					if(world.getBlockState(pos).getBlock() == Blocks.BARRIER) {
						world.addParticle(ParticleType.BARRIER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
					}
				}
			}
		}
	});

	public static final Setting NOWEB = new ChatMessageSetting("NoWeb", Keyboard.KEY_SEMICOLON, "movement");

	public static final Setting ANTIBLINDNESS = new ChatMessageSetting("AntiBlindness", Keyboard.KEY_L, "misc");

	public AutoJump() {
		System.out.println("AutoJump initialized");
	}
}
