package dev.akatriggered.optimizer;

import dev.akatriggered.Main;
import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.util.PerformanceGuard;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class CrystalOptimizer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void tick() {
        PerformanceGuard guard = Main.getPerformanceGuard();
        OptOutCache cache = Main.getOptOutCache();
        if (guard == null || cache == null || cache.isOptedOut()) return;
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) return;
        if (!mc.options.useKey.isPressed()) return;
        if (!guard.allowPlaceBoost()) return;

        BlockHitResult lookResult = raycastBlocks(4.5);
        if (lookResult == null || lookResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos targetPos = lookResult.getBlockPos();
        if (!isValidBase(targetPos)) return;
        if (!isSpaceFree(targetPos.up())) return;

        ActionResult result = mc.interactionManager.interactBlock(
            mc.player,
            Hand.MAIN_HAND,
            new BlockHitResult(
                Vec3d.ofCenter(targetPos).add(0, 0.5, 0),
                Direction.UP,
                targetPos,
                false
            )
        );

        if (dev.akatriggered.util.ActionResultResolver.isAccepted(result)) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    public static void onCrystalAttackPacket(Entity entity) {
        if (!(entity instanceof EndCrystalEntity crystal)) return;
        PerformanceGuard guard = Main.getPerformanceGuard();
        OptOutCache cache = Main.getOptOutCache();
        if (guard == null || cache == null || cache.isOptedOut()) return;
        if (!guard.allowBreakPrediction(crystal.getId())) return;
        try {
            entity.setRemoved(Entity.RemovalReason.KILLED);
        } catch (Exception ignored) {}
    }

    private static boolean isValidBase(BlockPos pos) {
        if (mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        return state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK);
    }

    private static boolean isSpaceFree(BlockPos above) {
        if (mc.world == null) return false;
        if (!mc.world.isAir(above) || !mc.world.isAir(above.up())) return false;
        double x = above.getX(), y = above.getY(), z = above.getZ();
        List<Entity> blocking = mc.world.getOtherEntities(mc.player,
            new Box(x, y, z, x + 1.0, y + 2.0, z + 1.0));
        return blocking.isEmpty();
    }

    private static BlockHitResult raycastBlocks(double reach) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d look = mc.player.getRotationVec(1.0f);
        Vec3d end = eye.add(look.multiply(reach));
        HitResult hit = mc.world.raycast(new RaycastContext(
            eye, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            mc.player
        ));
        return hit instanceof BlockHitResult bhr ? bhr : null;
    }
}
