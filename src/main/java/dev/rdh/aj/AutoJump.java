package dev.rdh.aj;

import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import dev.rdh.aj.mixin.LivingEntityAccessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.LiteralText;

import java.util.Arrays;
import java.util.List;

public class AutoJump implements ClientModInitializer {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private static final KeyBinding AUTOJUMP = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aj.toggle_aj", Keyboard.KEY_BACKSLASH, "key.categories.movement"));
	private static final KeyBinding INVWALK = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aj.toggle_iw", Keyboard.KEY_MINUS, "key.categories.movement"));
	private static final KeyBinding HIGHJUMP = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aj.toggle_hj", Keyboard.KEY_EQUALS, "key.categories.movement"));
	private static final KeyBinding SHOW_BARRIERS = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aj.toggle_sb", Keyboard.KEY_BACK, "key.categories.misc"));

	private static boolean ajEnabled = false;
	private static boolean iwEnabled = false;
	private static boolean hjEnabled = false;
	private static boolean sbEnabled = false;

	private static final Logger LOGGER = LogManager.getLogger("AutoJump");

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_WORLD_TICK.register(client -> {
			if(AUTOJUMP.wasPressed()) {
				ajEnabled = !ajEnabled;
				String msg = "AutoJump: " + (ajEnabled ? "Enabled" : "Disabled");
				LOGGER.info(msg);
				MC.player.sendMessage(new LiteralText(msg));
			}

			if(INVWALK.wasPressed()) {
				iwEnabled = !iwEnabled;
				String msg = "InvWalk: " + (iwEnabled ? "Enabled" : "Disabled");
				LOGGER.info(msg);
				MC.player.sendMessage(new LiteralText(msg));
			}

			hj: if(HIGHJUMP.wasPressed()) {
				if(((LivingEntityAccessor) MC.player).getJumpingCooldown() != 0 || !MC.player.onGround)
					break hj;
				hjEnabled = true;
				MC.player.jump();
				((LivingEntityAccessor) MC.player).setJumpingCooldown(10);
				hjEnabled = false;
			}

			if(SHOW_BARRIERS.wasPressed()) {
				sbEnabled = !sbEnabled;
				String msg = "ShowBarriers: " + (sbEnabled ? "Enabled" : "Disabled");
				LOGGER.info(msg);
				MC.player.sendMessage(new LiteralText(msg));
			}

			if(ajEnabled)
				tickAutoJump();
			if(iwEnabled)
				tickInvWalk();
		});
	}

	private void tickAutoJump() {
		if(MC.player.onGround && !MC.player.isSneaking()
				&& !MC.options.sneakKey.isPressed() && !MC.options.jumpKey.isPressed()
				&& MC.world.doesBoxCollide(MC.player,
				MC.player.getBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)).isEmpty()) {
			MC.player.jump();
		}
	}

	public static boolean isHjEnabled() {
		return hjEnabled;
	}
	public static boolean isSbEnabled() {
		return sbEnabled;
	}

	private static List<KeyBinding> keys = null;

	private void tickInvWalk() {
		if(keys == null) {
			keys = Arrays.asList(MC.options.forwardKey, MC.options.backKey, MC.options.leftKey, MC.options.rightKey, MC.options.jumpKey, MC.options.sneakKey, MC.options.sprintKey);
		}
		Screen screen = MC.currentScreen;
		if(screen instanceof ChatScreen || screen instanceof GameMenuScreen) {
			return;
		}

		for(KeyBinding key : keys) {
			KeyBinding.setKeyPressed(key.getCode(), GameOptions.isPressed(key));
		}
	}
}
