// src/main/java/com/example/blockhighlight/BlockHighlightConfig.java
package com.example.blockhighlight;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlockHighlightConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("blockhighlight.json");
    
    private static ConfigData config = new ConfigData();
    
    public static class ConfigData {
        public boolean enabled = false;
        public float highlightOpacity = 1.0f;
        public float hiddenOpacity = 0.1f;
        public boolean showBreakingProgress = true;
        public boolean enableInCreative = false;
        
        // RGB values for highlight color (0.0-1.0)
        public float highlightRed = 1.0f;
        public float highlightGreen = 1.0f;
        public float highlightBlue = 1.0f;
    }
    
    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                    config = GSON.fromJson(reader, ConfigData.class);
                }
            }
        } catch (Exception e) {
            BlockHighlightMod.LOGGER.error("Failed to load config", e);
        }
        save(); // Save to ensure file exists with defaults
    }
    
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(config, writer);
            }
        } catch (Exception e) {
            BlockHighlightMod.LOGGER.error("Failed to save config", e);
        }
    }
    
    public static boolean isEnabled() {
        return config.enabled;
    }
    
    public static void setEnabled(boolean enabled) {
        config.enabled = enabled;
        save();
    }
    
    public static void toggleEnabled() {
        setEnabled(!config.enabled);
    }
    
    public static float getHighlightOpacity() {
        return config.highlightOpacity;
    }
    
    public static void setHighlightOpacity(float opacity) {
        config.highlightOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        save();
    }
    
    public static float getHiddenOpacity() {
        return config.hiddenOpacity;
    }
    
    public static void setHiddenOpacity(float opacity) {
        config.hiddenOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        save();
    }
    
    public static boolean showBreakingProgress() {
        return config.showBreakingProgress;
    }
    
    public static void setShowBreakingProgress(boolean show) {
        config.showBreakingProgress = show;
        save();
    }
    
    public static boolean isEnabledInCreative() {
        return config.enableInCreative;
    }
    
    public static void setEnabledInCreative(boolean enabled) {
        config.enableInCreative = enabled;
        save();
    }
    
    public static float[] getHighlightColor() {
        return new float[]{config.highlightRed, config.highlightGreen, config.highlightBlue};
    }
    
    public static void setHighlightColor(float red, float green, float blue) {
        config.highlightRed = Math.max(0.0f, Math.min(1.0f, red));
        config.highlightGreen = Math.max(0.0f, Math.min(1.0f, green));
        config.highlightBlue = Math.max(0.0f, Math.min(1.0f, blue));
        save();
    }
}
