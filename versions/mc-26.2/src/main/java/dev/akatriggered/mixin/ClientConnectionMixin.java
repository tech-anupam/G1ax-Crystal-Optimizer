package dev.akatriggered.mixin;

import dev.akatriggered.Main;
import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import dev.akatriggered.util.PerformanceGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (!OptimizerCommand.defaultMode) return;
        if (!(packet instanceof ServerboundInteractPacket interactPacket)) return;
        if (Main.getOptOutCache() != null && Main.getOptOutCache().isOptedOut()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        HitResult crosshair = mc.hitResult;
        if (!(crosshair instanceof EntityHitResult entityHit)) return;

        Entity entity = entityHit.getEntity();
        if (!(entity instanceof EndCrystal)) return;

        if (interactPacket.hand() == null) {
            CrystalOptimizer.onCrystalAttackPacket(entity);
        }
    }
}
