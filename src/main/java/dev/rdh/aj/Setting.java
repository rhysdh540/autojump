package dev.rdh.aj;

import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.LiteralText;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Setting {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	public final String name;
	public final KeyBinding keyBinding;
	private boolean enabled;

	private final BiConsumer<Setting, ClientWorld> action;

	private static final Pattern UPPERCASE_ONLY = Pattern.compile("[^A-Z]");

	public Setting(String name, int keyCode, String category, BiConsumer<Setting, ClientWorld> action) {
		this.name = name;
		this.enabled = false;
		this.action = action;

		String key = UPPERCASE_ONLY.matcher(name).replaceAll("").toLowerCase();
		key = "key.aj.toggle_" + key;

		this.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(key, keyCode, "key.categories." + category));

		ClientTickEvents.START_WORLD_TICK.register(this::tick);
	}

	public Setting(String name, int keyCode, String category, Consumer<Setting> action) {
		this(name, keyCode, category, (setting, world) -> action.accept(setting));
	}

	public Setting(String name,int keyCode, String category) {
		this(name, keyCode, category, (setting, world) -> {});
	}

	public boolean pressed() {
		return this.keyBinding.wasPressed();
	}

	public boolean held() {
		return this.keyBinding.isPressed();
	}

	public boolean enabled() {
		return this.enabled;
	}

	public void enable() {
		this.enabled = true;
	}

	public void disable() {
		this.enabled = false;
	}

	public void toggle() {
		this.enabled = !this.enabled;
	}

	public void tick(ClientWorld world) {
		if(this.pressed()) {
			this.toggle();
		}
		if(this.enabled) {
			this.action.accept(this, world);
		}
	}

	public static class ChatMessageSetting extends Setting {

		public ChatMessageSetting(String name, int keyCode, String category, BiConsumer<Setting, ClientWorld> action) {
			super(name, keyCode, category, action);
		}

		public ChatMessageSetting(String name, int keyCode, String category) {
			super(name, keyCode, category);
		}

		public ChatMessageSetting(String name, int keyCode, String category, Consumer<Setting> action) {
			super(name, keyCode, category, action);
		}

		private void sendMessage() {
			if(MC.player == null) return;
			MC.player.sendMessage(new LiteralText(this.name + ": " + (this.enabled() ? "Enabled" : "Disabled")));
		}

		@Override
		public void toggle() {
			super.toggle();
			this.sendMessage();
		}

		@Override
		public void enable() {
			super.enable();
			this.sendMessage();
		}

		@Override
		public void disable() {
			super.disable();
			this.sendMessage();
		}
	}
}
