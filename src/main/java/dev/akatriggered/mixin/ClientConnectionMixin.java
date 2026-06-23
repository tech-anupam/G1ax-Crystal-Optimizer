package dev.akatriggered.mixin;

import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    /**
     * Intercepts outgoing PlayerInteractEntityC2SPacket.
     * When the player sends an ATTACK packet targeting an EndCrystalEntity,
     * we remove it client-side immediately — the server will process the explosion,
     * but the client no longer needs to wait for the removal packet to stop rendering it.
     *
     * This is the core optimization: the crystal visually disappears the instant
     * you click, matching what top crystal PvP players expect.
     */
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (!OptimizerCommand.defaultMode) return;

        if (!(packet instanceof PlayerInteractEntityC2SPacket interactPacket)) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        HitResult crosshair = mc.crosshairTarget;
        if (!(crosshair instanceof EntityHitResult entityHit)) return;

        Entity entity = entityHit.getEntity();
        if (!(entity instanceof EndCrystalEntity)) return;

        interactPacket.handle(new AttackHandler(entity));
    }

    private record AttackHandler(Entity target) implements PlayerInteractEntityC2SPacket.Handler {
        @Override
        public void interact(net.minecraft.util.Hand hand) {}

        @Override
        public void interactAt(net.minecraft.util.Hand hand, net.minecraft.util.math.Vec3d pos) {}

        @Override
        public void attack() {
            CrystalOptimizer.onCrystalAttackPacket(target);
        }
    }
}
