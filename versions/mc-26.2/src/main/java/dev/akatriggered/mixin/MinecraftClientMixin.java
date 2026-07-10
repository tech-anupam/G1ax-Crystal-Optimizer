package dev.akatriggered.mixin;

import dev.akatriggered.Main;
import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import dev.akatriggered.util.PerformanceGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        Minecraft mc = (Minecraft)(Object) this;
        if (mc.player == null || mc.level == null) return;

        PerformanceGuard guard = Main.getPerformanceGuard();
        if (guard != null) {
            int ping = 0;
            if (mc.getConnection() != null) {
                PlayerInfo entry = mc.getConnection().getPlayerInfo(mc.player.getUUID());
                if (entry != null) ping = entry.getLatency();
            }
            guard.observePingMillis(ping);
            guard.observeUseKeyState(mc.options.keyUse.isDown());
        }

        if (Main.getOptOutCache() != null && Main.getOptOutCache().isOptedOut()) return;

        boolean holdingCrystal = mc.player.getMainHandItem().is(Items.END_CRYSTAL)
            || mc.player.getOffhandItem().is(Items.END_CRYSTAL);
        if (!holdingCrystal) return;

        if (OptimizerCommand.tweakMode) {
            if (mc.options.keyUse.isDown() && guard != null
                && ((MinecraftClientAccessor) this).getItemUseCooldown() > 0
                && guard.allowPlaceBoost()) {
                ((MinecraftClientAccessor) this).setItemUseCooldown(0);
            }
            return;
        }

        if (OptimizerCommand.defaultMode) {
            CrystalOptimizer.tick();
            if (mc.options.keyUse.isDown()
                && ((MinecraftClientAccessor) this).getItemUseCooldown() > 0) {
                ((MinecraftClientAccessor) this).setItemUseCooldown(0);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "startUseItem")
    private void onDoItemUseTail(CallbackInfo ci) {
        if (!OptimizerCommand.tweakMode) return;
        Minecraft mc = (Minecraft)(Object) this;
        if (mc.player == null) return;
        boolean holdingCrystal = mc.player.getMainHandItem().is(Items.END_CRYSTAL)
            || mc.player.getOffhandItem().is(Items.END_CRYSTAL);
        if (!holdingCrystal) return;
        if (Main.getOptOutCache() != null && Main.getOptOutCache().isOptedOut()) return;
        PerformanceGuard guard = Main.getPerformanceGuard();
        if (guard != null && guard.allowPlaceBoost()) {
            ((MinecraftClientAccessor) this).setItemUseCooldown(0);
        }
    }

    @Inject(at = @At("HEAD"), method = "startUseItem", cancellable = true)
    private void onDoItemUseHead(CallbackInfo ci) {
        if (!OptimizerCommand.defaultMode) return;
        Minecraft mc = (Minecraft)(Object) this;
        if (mc.player == null) return;
        if (!mc.player.getMainHandItem().is(Items.END_CRYSTAL)) return;
        if (!mc.options.keyUse.isDown()) return;
        ci.cancel();
    }
}
