package com.example.blockhighlight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockHighlightMod implements ModInitializer {
    public static final String MOD_ID = "blockhighlight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static KeyBinding toggleKeybind;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing BlockHighlight Mod");
        
        // Register keybinding
        toggleKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.blockhighlight.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.blockhighlight.general"
        ));
        
        // Register tick event for keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKeybind.wasPressed()) {
                BlockHighlightConfig.toggleEnabled();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Block Highlight: " + 
                        (BlockHighlightConfig.isEnabled() ? "ON" : "OFF")), true);
                }
            }
        });
        
        // Initialize config
        BlockHighlightConfig.load();
    }
    
    public static KeyBinding getToggleKeybind() {
        return toggleKeybind;
    }
}
