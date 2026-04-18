package dev.akatriggered.optimizer;

import dev.akatriggered.command.OptimizerCommand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CrystalOptimizer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public static volatile int hitCount = 0;
    public static volatile int breakingBlockTick = 0;
    private static volatile long lastPacketTime = 0;
    private static final long PACKET_DELAY_MS = 25;
    
    public static void tick() {
        if (!OptimizerCommand.fastCrystal) return;
        
        CompletableFuture.runAsync(() -> {
            try {
                processOptimizedTicks();
            } catch (Exception e) {
            }
        });
    }
    
    private static void processOptimizedTicks() {
        try {
            if (mc == null || mc.player == null || mc.world == null) return;
            
            ItemStack mainHandStack = mc.player.getMainHandStack();
            if (mainHandStack == null) return;

            if (mc.options.attackKey.isPressed()) {
                breakingBlockTick++;
            } else {
                breakingBlockTick = 0;
            }

            if (breakingBlockTick > 2) return;

            if (!mc.options.useKey.isPressed()) {
                hitCount = 0;
                return;
            }
            
            if (hitCount >= getPacketLimit()) return;

            if (isLookingAtTargetEntity()) {
                if (mc.options.attackKey.isPressed()) {
                    Entity target = getTargetEntity();
                    if (target != null && hitCount >= 1) {
                        target.setRemoved(Entity.RemovalReason.KILLED);
                    }
                    hitCount++;
                }
            }

            if (!mainHandStack.isOf(Items.END_CRYSTAL)) return;

            if (mc.options.useKey.isPressed()) {
                BlockHitResult lookPos = getLookPosition();
                if (lookPos != null && isValidCrystalPlacement(lookPos.getBlockPos())) {
                    sendInteractBlockPacket(lookPos.getBlockPos(), lookPos.getSide());
                    if (canPlaceCrystal(lookPos.getBlockPos())) {
                        mc.player.swingHand(mc.player.getActiveHand());
                        hitCount++;
                    }
                }
            }
        } catch (Exception e) {
            // Prevent crashes from mod conflicts
        }
    }

    private static boolean isValidCrystalPlacement(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK);
    }

    private static BlockHitResult getLookPosition() {
        Vec3d camPos = mc.player.getEyePos();
        Vec3d lookVec = getLookVector();
        return mc.world.raycast(new RaycastContext(
            camPos, 
            camPos.add(lookVec.multiply(4.5)), 
            RaycastContext.ShapeType.OUTLINE, 
            RaycastContext.FluidHandling.NONE, 
            mc.player
        ));
    }

    private static Entity getTargetEntity() {
        if (mc.crosshairTarget instanceof EntityHitResult hit) {
            Entity entity = hit.getEntity();
            if (entity instanceof EndCrystalEntity || 
                entity instanceof SlimeEntity || 
                entity instanceof MagmaCubeEntity) {
                return entity;
            }
        }
        return null;
    }

    private static boolean isLookingAtTargetEntity() {
        return mc.crosshairTarget instanceof EntityHitResult hit && 
               (hit.getEntity() instanceof EndCrystalEntity ||
                hit.getEntity() instanceof MagmaCubeEntity ||
                hit.getEntity() instanceof SlimeEntity);
    }

    private static Vec3d getLookVector() {
        return mc.player.getRotationVec(1.0F);
    }

    private static ActionResult sendInteractBlockPacket(BlockPos pos, Direction dir) {
        // Add timing check to prevent packet flooding on multiplayer
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPacketTime < PACKET_DELAY_MS) {
            return ActionResult.PASS;
        }
        lastPacketTime = currentTime;
        
        Vec3d vec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        Vec3i vec3i = new Vec3i((int) vec.x, (int) vec.y, (int) vec.z);
        BlockPos blockPos = new BlockPos(vec3i);
        BlockHitResult result = new BlockHitResult(vec, dir, blockPos, false);
        return mc.interactionManager.interactBlock(mc.player, mc.player.getActiveHand(), result);
    }

    public static int getPacketLimit() {
        int ping = getPing();
        return ping > 50 ? 2 : 1;
    }

    private static int getPing() {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }

    private static boolean canPlaceCrystal(BlockPos block) {
        try {
            if (mc == null || mc.world == null || block == null) return false;
            
            BlockState blockState = mc.world.getBlockState(block);
            if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK)) {
                return false;
            }
            
            BlockPos above = block.up();
            if (!mc.world.isAir(above)) return false;
            
            double x = above.getX();
            double y = above.getY();
            double z = above.getZ();
            
            List<Entity> entities = mc.world.getOtherEntities(null, 
                new Box(x, y, z, x + 1.0D, y + 2.0D, z + 1.0D));
            return entities.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
