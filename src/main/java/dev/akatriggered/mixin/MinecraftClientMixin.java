package dev.akatriggered.mixin;

import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    
    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        CrystalOptimizer.tick();
    }

    @Inject(at = @At("HEAD"), method = "doItemUse", cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        if (!OptimizerCommand.fastCrystal) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        
        ItemStack mainHand = mc.player.getMainHandStack();
        if (mainHand.isOf(Items.END_CRYSTAL)) {
            // Only cancel if we've reached the packet limit and key is still pressed
            if (mc.options.useKey.isPressed() && CrystalOptimizer.hitCount >= CrystalOptimizer.getPacketLimit()) {
                ci.cancel();
            }
        }
    }
}
