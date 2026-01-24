package dev.akatriggered.handler;

import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.optimizer.CrystalOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.event.GameEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.atomic.DoubleAdder;

public class InteractHandler implements PlayerInteractEntityC2SPacket.Handler {

    private final MinecraftClient client;
    private final DoubleAdder damageAdder = new DoubleAdder();
    private static volatile long lastInteractTime = 0;
    private static final long INTERACT_COOLDOWN_MS = 50;

    public InteractHandler(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void interact(Hand hand) {
    }

    @Override
    public void interactAt(Hand hand, Vec3d pos) {
    }

    @Override
    public void attack() {
        if (!OptimizerCommand.crystalOptimizer) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastInteractTime < INTERACT_COOLDOWN_MS) {
            return;
        }
        
        HitResult hitResult = client.crosshairTarget;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return;
        }

        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof EndCrystalEntity crystal)) {
            return;
        }

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        if (canDestroyCrystal(player)) {
            destroyCrystal(crystal);
            lastInteractTime = currentTime;
        }
    }

    private boolean canDestroyCrystal(ClientPlayerEntity player) {
        StatusEffectInstance weakness = player.getStatusEffect(StatusEffects.WEAKNESS);

        if (weakness == null) return true;

        double baseDamage = player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        double weaknessPenalty = 4.0D * (weakness.getAmplifier() + 1);

        if (baseDamage > weaknessPenalty + 5.0D) {
            return true;
        }

        return calculateTotalDamage(player) > 0.0D;
    }

    private double calculateTotalDamage(ClientPlayerEntity player) {
        double baseDamage = player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
        double weaponDamage = getWeaponDamage(player.getMainHandStack());

        StatusEffectInstance strength = player.getStatusEffect(StatusEffects.STRENGTH);
        double strengthBonus = strength != null ? 3.0D * (strength.getAmplifier() + 1) : 0.0D;

        StatusEffectInstance weakness = player.getStatusEffect(StatusEffects.WEAKNESS);
        double weaknessPenalty = weakness != null ? 4.0D * (weakness.getAmplifier() + 1) : 0.0D;

        return Math.max(0.0D, baseDamage + weaponDamage + strengthBonus - weaknessPenalty);
    }

    private double getWeaponDamage(ItemStack item) {
        if (item.isEmpty()) return 0.0D;
        return 1.0D; // Basic weapon damage
    }

    private void destroyCrystal(Entity crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.emitGameEvent(GameEvent.ENTITY_DIE);
    }
}
