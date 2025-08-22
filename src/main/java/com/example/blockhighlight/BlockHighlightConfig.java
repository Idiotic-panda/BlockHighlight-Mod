// src/main/java/com/example/blockhighlight/BlockHighlightClient.java
package com.example.blockhighlight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

public class BlockHighlightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register config screen with ModMenu if present
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            // Additional client-side initialization if needed
        });
    }
}
