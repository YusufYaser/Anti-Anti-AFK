package xyz.yusufyaser.aaafk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AntiAntiAFKClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MinecraftClient client = MinecraftClient.getInstance();

        KeyBinding openMenuBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.anti-anti-afk.menu",
                InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_M,
                "category.anti-anti-afk"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(_client -> {
            while (openMenuBinding.wasPressed()) {
				if (client.getCurrentServerEntry() != null) client.setScreen(new SettingsScreen());
            }
        });

        AFKTimer.start();
	}
}