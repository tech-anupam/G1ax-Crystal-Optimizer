package dev.akatriggered.mixin;

import dev.akatriggered.Main;
import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import dev.akatriggered.util.PerformanceGuard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (!OptimizerCommand.defaultMode) return;
        if (!(packet instanceof PlayerInteractEntityC2SPacket interactPacket)) return;
        if (Main.getOptOutCache() != null && Main.getOptOutCache().isOptedOut()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        HitResult crosshair = mc.crosshairTarget;
        if (!(crosshair instanceof EntityHitResult entityHit)) return;

        Entity entity = entityHit.getEntity();
        if (!(entity instanceof EndCrystalEntity)) return;

        PerformanceGuard guard = Main.getPerformanceGuard();
        interactPacket.handle(new AttackHandler(entity, guard));
    }

    private record AttackHandler(Entity target, PerformanceGuard guard)
        implements PlayerInteractEntityC2SPacket.Handler {
        @Override public void interact(Hand hand) {}
        @Override public void interactAt(Hand hand, Vec3d pos) {}
        @Override public void attack() {
            CrystalOptimizer.onCrystalAttackPacket(target);
        }
    }
}
