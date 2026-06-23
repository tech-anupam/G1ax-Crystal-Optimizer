package dev.akatriggered.mixin;

import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    /**
     * Runs every game tick (20 times/second).
     *
     * TWEAK MODE (AC-safe):
     *   Only clears itemUseCooldown when holding a crystal.
     *   Minecraft vanilla sets this to 4 after each right-click,
     *   forcing a 200ms wait before the next placement attempt.
     *   Clearing it lets vanilla doItemUse() run every tick instead.
     *   The ENTIRE vanilla code path (EndCrystalItem.useOnBlock,
     *   placement validation, server packet) is untouched and unchanged.
     *   Only the FREQUENCY of attempts increases. Nothing else.
     *
     * DEFAULT MODE:
     *   Adds full optimizer: direct interactBlock packets + ping-adaptive
     *   rate limiting + client-side crystal removal on attack.
     */
    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = (MinecraftClient) (Object) this;
        if (mc.player == null || mc.world == null) return;

        boolean holdingCrystal = mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)
            || mc.player.getOffHandStack().isOf(Items.END_CRYSTAL);
        if (!holdingCrystal) return;

        if (OptimizerCommand.tweakMode) {
            if (((MinecraftClientAccessor) this).getItemUseCooldown() > 0) {
                ((MinecraftClientAccessor) this).setItemUseCooldown(0);
            }
            return;
        }

        if (OptimizerCommand.defaultMode) {
            CrystalOptimizer.tick();
            if (((MinecraftClientAccessor) this).getItemUseCooldown() > 0) {
                ((MinecraftClientAccessor) this).setItemUseCooldown(0);
            }
        }
    }

    /**
     * Only active in DEFAULT mode.
     * Prevents vanilla from placing crystals after our direct interactBlock call
     * has already hit the packet rate limit. Without this, vanilla would still
     * attempt placement, doubling packets.
     * In TWEAK mode: never runs — vanilla doItemUse runs completely normally.
     */
    @Inject(at = @At("HEAD"), method = "doItemUse", cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        if (!OptimizerCommand.defaultMode) return;

        MinecraftClient mc = (MinecraftClient) (Object) this;
        if (mc.player == null) return;
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) return;
        if (!mc.options.useKey.isPressed()) return;

        if (CrystalOptimizer.hitCount >= CrystalOptimizer.getPacketLimit()) {
            ci.cancel();
        }
    }
}
