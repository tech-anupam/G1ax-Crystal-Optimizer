package dev.akatriggered.mixin;

import dev.akatriggered.Main;
import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"))
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (!OptimizerCommand.defaultMode) return;
        if (!(packet instanceof PlayerInteractEntityC2SPacket)) return;
        if (Main.getOptOutCache() != null && Main.getOptOutCache().isOptedOut()) return;

        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        PlayerInteractEntityC2SPacket interactPacket = (PlayerInteractEntityC2SPacket) packet;
        if (!isAttackPacket(interactPacket)) return;

        Entity entity = null;
        try {
            entity = interactPacket.getEntity(mc.world);
        } catch (Throwable ignored) {}

        if (!(entity instanceof EndCrystalEntity)) {
            HitResult crosshair = mc.crosshairTarget;
            if (crosshair instanceof EntityHitResult) {
                Entity crosshairEntity = ((EntityHitResult) crosshair).getEntity();
                if (crosshairEntity instanceof EndCrystalEntity) {
                    entity = crosshairEntity;
                }
            }
        }

        if (entity instanceof EndCrystalEntity) {
            CrystalOptimizer.onCrystalAttackPacket(entity);
        }
    }

    private static boolean isAttackPacket(PlayerInteractEntityC2SPacket packet) {
        try {
            return packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK;
        } catch (Throwable t) {
            try {
                for (java.lang.reflect.Method m : packet.getClass().getDeclaredMethods()) {
                    if (m.getName().equals("getType") || m.getName().equals("type")) {
                        m.setAccessible(true);
                        Object type = m.invoke(packet);
                        return type != null && type.toString().equals("ATTACK");
                    }
                }
            } catch (Exception ignored) {}
            return false;
        }
    }
}
