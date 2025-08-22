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
