package dev.akatriggered.mixin;

import dev.akatriggered.Main;
import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import dev.akatriggered.util.PerformanceGuard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = (MinecraftClient)(Object) this;
        if (mc.player == null || mc.world == null) return;

        PerformanceGuard guard = Main.getPerformanceGuard();
        if (guard != null) {
            int ping = 0;
            if (mc.getNetworkHandler() != null) {
                PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                if (entry != null) ping = entry.getLatency();
            }
            guard.observePingMillis(ping);
            guard.observeUseKeyState(mc.options.useKey.isPressed());
        }

        if (Main.getOptOutCache() != null && Main.getOptOutCache().isOptedOut()) return;

        boolean holdingCrystal = mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
            || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL);
        if (!holdingCrystal) return;

        if (OptimizerCommand.tweakMode) {
            if (mc.options.useKey.isPressed() && guard != null
                && ((MinecraftClientAccessor) this).getItemUseCooldown() > 0
                && guard.allowPlaceBoost()) {
                ((MinecraftClientAccessor) this).setItemUseCooldown(0);
            }
            return;
        }

        if (OptimizerCommand.defaultMode) {
            CrystalOptimizer.tick();
            if (mc.options.useKey.isPressed()
                && ((MinecraftClientAccessor) this).getItemUseCooldown() > 0) {
                ((MinecraftClientAccessor) this).setItemUseCooldown(0);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "doItemUse")
    private void onDoItemUseTail(CallbackInfo ci) {
        if (!OptimizerCommand.tweakMode) return;
        MinecraftClient mc = (MinecraftClient)(Object) this;
        if (mc.player == null) return;
        boolean holdingCrystal = mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
            || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL);
        if (!holdingCrystal) return;
        if (Main.getOptOutCache() != null && Main.getOptOutCache().isOptedOut()) return;
        PerformanceGuard guard = Main.getPerformanceGuard();
        if (guard != null && guard.allowPlaceBoost()) {
            ((MinecraftClientAccessor) this).setItemUseCooldown(0);
        }
    }

    @Inject(at = @At("HEAD"), method = "doItemUse", cancellable = true)
    private void onDoItemUseHead(CallbackInfo ci) {
        if (!OptimizerCommand.defaultMode) return;
        MinecraftClient mc = (MinecraftClient)(Object) this;
        if (mc.player == null) return;
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) return;
        if (!mc.options.useKey.isPressed()) return;
        ci.cancel();
    }
}
