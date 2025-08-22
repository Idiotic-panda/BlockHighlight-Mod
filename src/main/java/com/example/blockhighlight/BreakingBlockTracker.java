// src/main/java/com/example/blockhighlight/BreakingBlockTracker.java
package com.example.blockhighlight;

import net.minecraft.util.math.BlockPos;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class BreakingBlockTracker {
    private static final Map<BlockPos, BreakingInfo> breakingBlocks = new ConcurrentHashMap<>();
    private static final long CLEANUP_INTERVAL = 1000; // 1 second
    private static long lastCleanup = 0;
    
    public static class BreakingInfo {
        public final UUID playerId;
        public final int breakStage;
        public final long timestamp;
        
        public BreakingInfo(UUID playerId, int breakStage) {
            this.playerId = playerId;
            this.breakStage = breakStage;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static void updateBreakingBlock(BlockPos pos, UUID playerId, int breakStage) {
        if (breakStage >= 0 && breakStage < 10) {
            // Block is being broken
            breakingBlocks.put(pos.toImmutable(), new BreakingInfo(playerId, breakStage));
        } else {
            // Block breaking finished or cancelled
            breakingBlocks.remove(pos);
        }
        
        // Periodic cleanup of old entries
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCleanup > CLEANUP_INTERVAL) {
            cleanup();
            lastCleanup = currentTime;
        }
    }
    
    public static boolean isBlockBeingBroken(BlockPos pos) {
        BreakingInfo info = breakingBlocks.get(pos);
        if (info == null) return false;
        
        // Remove old entries (blocks that haven't been updated in 3 seconds)
        long currentTime = System.currentTimeMillis();
        if (currentTime - info.timestamp > 3000) {
            breakingBlocks.remove(pos);
            return false;
        }
        
        return true;
    }
    
    public static BreakingInfo getBreakingInfo(BlockPos pos) {
        return breakingBlocks.get(pos);
    }
    
    public static void removeBreakingBlock(BlockPos pos) {
        breakingBlocks.remove(pos);
    }
    
    public static void clear() {
        breakingBlocks.clear();
    }
    
    private static void cleanup() {
        long currentTime = System.currentTimeMillis();
        breakingBlocks.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().timestamp > 3000);
    }
    
    public static int getActiveBreakingBlocksCount() {
        return breakingBlocks.size();
    }
    
    public static Map<BlockPos, BreakingInfo> getAllBreakingBlocks() {
        return new ConcurrentHashMap<>(breakingBlocks);
    }
}

// src/main/java/com/example/blockhighlight/mixin/ClientWorldMixin.java
package com.example.blockhighlight.mixin;

import com.example.blockhighlight.BreakingBlockTracker;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    
    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"))
    private void onSetBlockBreakingInfo(int breakerEntityId, BlockPos pos, int progress, CallbackInfo ci) {
        ClientWorld world = (ClientWorld) (Object) this;
        Entity entity = world.getEntityById(breakerEntityId);
        
        if (entity != null) {
            UUID playerId = entity.getUuid();
            BreakingBlockTracker.updateBreakingBlock(pos, playerId, progress);
        }
    }
}
