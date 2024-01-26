package dev.rdh.aj;

import net.fabricmc.api.ModInitializer;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.Arrays;
import java.util.List;

public class AutoJump implements ModInitializer {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private static final KeyBinding AUTOJUMP = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aj.toggle_aj", Keyboard.KEY_BACKSLASH, "key.categories.movement"));
	private static final KeyBinding INVWALK = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aj.toggle_iw", Keyboard.KEY_MINUS, "key.categories.movement"));

	private static boolean ajEnabled = false;
	private static boolean iwEnabled = false;

	private static final Logger LOGGER = LogManager.getLogger("AutoJump");

	@Override
	public void onInitialize() {
		ClientTickEvents.START_WORLD_TICK.register(client -> {
			if(AUTOJUMP.isPressed()) {
				ajEnabled = !ajEnabled;
				LOGGER.info("AutoJump: {}", ajEnabled ? "Enabled" : "Disabled");
			}

			if(INVWALK.isPressed()) {
				iwEnabled = !iwEnabled;
				LOGGER.info("InvWalk: {}", iwEnabled ? "Enabled" : "Disabled");
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

	private void tickInvWalk() {
		Screen screen = MC.currentScreen;
		if(screen instanceof ChatScreen) {
			return;
		}

		List<KeyBinding> keys = Arrays.asList(MC.options.forwardKey, MC.options.backKey, MC.options.leftKey, MC.options.rightKey, MC.options.jumpKey, MC.options.sneakKey);
		for(KeyBinding key : keys) {
			int code = key.getCode();
			KeyBinding.setKeyPressed(code, GameOptions.isPressed(key));
		}
	}
}
