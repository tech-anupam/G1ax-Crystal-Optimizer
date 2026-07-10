package dev.akatriggered.optimizer;

import dev.akatriggered.Main;
import dev.akatriggered.cache.OptOutCache;
import dev.akatriggered.util.PerformanceGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CrystalOptimizer {

    private static final Minecraft mc = Minecraft.getInstance();

    public static void tick() {
        PerformanceGuard guard = Main.getPerformanceGuard();
        OptOutCache cache = Main.getOptOutCache();
        if (guard == null || cache == null || cache.isOptedOut()) return;
        if (mc.player == null || mc.level == null) return;
        if (!mc.player.getMainHandItem().is(Items.END_CRYSTAL)) return;
        if (!mc.options.keyUse.isDown()) return;
        if (!guard.allowPlaceBoost()) return;

        BlockHitResult lookResult = raycastBlocks(4.5);
        if (lookResult == null || lookResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos targetPos = lookResult.getBlockPos();
        if (!isValidBase(targetPos)) return;
        if (!isSpaceFree(targetPos.above())) return;

        InteractionResult result = mc.gameMode.useItemOn(
            mc.player,
            InteractionHand.MAIN_HAND,
            new BlockHitResult(
                Vec3.atCenterOf(targetPos).add(0, 0.5, 0),
                Direction.UP,
                targetPos,
                false
            )
        );

        if (dev.akatriggered.util.ActionResultResolver.isAccepted(result)) {
            mc.player.swing(InteractionHand.MAIN_HAND);
        }
    }

    public static void onCrystalAttackPacket(Entity entity) {
        if (!(entity instanceof EndCrystal crystal)) return;
        PerformanceGuard guard = Main.getPerformanceGuard();
        OptOutCache cache = Main.getOptOutCache();
        if (guard == null || cache == null || cache.isOptedOut()) return;
        if (!guard.allowBreakPrediction(crystal.getId())) return;
        try {
            entity.setRemoved(Entity.RemovalReason.KILLED);
        } catch (Exception ignored) {}
    }

    private static boolean isValidBase(BlockPos pos) {
        if (mc.level == null) return false;
        BlockState state = mc.level.getBlockState(pos);
        return state.is(Blocks.OBSIDIAN) || state.is(Blocks.BEDROCK);
    }

    private static boolean isSpaceFree(BlockPos above) {
        if (mc.level == null) return false;
        if (!mc.level.isEmptyBlock(above) || !mc.level.isEmptyBlock(above.above())) return false;
        double x = above.getX(), y = above.getY(), z = above.getZ();
        List<Entity> blocking = mc.level.getEntities(mc.player,
            new AABB(x, y, z, x + 1.0, y + 2.0, z + 1.0));
        return blocking.isEmpty();
    }

    private static BlockHitResult raycastBlocks(double reach) {
        Vec3 eye = mc.player.getEyePosition();
        Vec3 look = mc.player.getViewVector(1.0f);
        Vec3 end = eye.add(look.scale(reach));
        HitResult hit = mc.level.clip(new ClipContext(
            eye, end,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            mc.player
        ));
        return hit instanceof BlockHitResult bhr ? bhr : null;
    }
}
