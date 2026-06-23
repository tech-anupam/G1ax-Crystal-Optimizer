package dev.akatriggered.optimizer;

import dev.akatriggered.command.OptimizerCommand;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

/**
 * Handles DEFAULT mode only.
 * Logic: send interactBlock packets directly at a rate limited by ping,
 * and remove crystals client-side immediately on attack (reduces visual lag).
 *
 * TWEAK mode is handled entirely in MinecraftClientMixin via itemUseCooldown reset.
 */
public class CrystalOptimizer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static volatile int hitCount = 0;
    private static volatile long lastResetMs = 0;

    /**
     * Called each tick from MinecraftClientMixin in DEFAULT mode.
     * Sends crystal placement packet directly, bypassing vanilla useOnBlock validation
     * (which we cancelled via EndCrystalItemMixin returning PASS).
     */
    public static void tick() {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) return;
        if (!mc.options.useKey.isPressed()) {
            resetHitCount();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastResetMs >= getResetDelayMs()) {
            hitCount = 0;
            lastResetMs = now;
        }

        if (hitCount >= getPacketLimit()) return;

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

        if (result.isAccepted()) {
            mc.player.swingHand(Hand.MAIN_HAND);
            hitCount++;
        }
    }

    /**
     * Called from ClientConnectionMixin when a PlayerInteractEntityC2SPacket(ATTACK)
     * is detected targeting a crystal. Removes the crystal client-side immediately
     * to eliminate the visual lag of waiting for server acknowledgement.
     */
    public static void onCrystalAttackPacket(Entity entity) {
        if (!OptimizerCommand.defaultMode) return;
        if (!(entity instanceof EndCrystalEntity)) return;

        try {
            entity.setRemoved(Entity.RemovalReason.KILLED);
        } catch (Exception e) {
            OptimizerCommand.error("Crystal removal failed: " + e.getMessage());
        }
    }

    private static boolean isValidBase(BlockPos pos) {
        if (mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        return state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK);
    }

    /**
     * End crystal requires 2 blocks of vertical clearance above the base.
     * Box: (x, y, z) to (x+1, y+2, z+1) where (x,y,z) is the above-base position.
     */
    private static boolean isSpaceFree(BlockPos above) {
        if (mc.world == null) return false;
        if (!mc.world.isAir(above) || !mc.world.isAir(above.up())) return false;

        double x = above.getX();
        double y = above.getY();
        double z = above.getZ();
        List<Entity> blocking = mc.world.getOtherEntities(
            mc.player,
            new Box(x, y, z, x + 1.0, y + 2.0, z + 1.0)
        );
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

    /**
     * Ping-adaptive packet limit.
     * High ping = more packets in flight before waiting (server processes slower).
     * Low ping = fewer needed since server acks quickly.
     */
    public static int getPacketLimit() {
        int ping = getPing();
        if (ping > 150) return 4;
        if (ping > 50) return 3;
        return 2;
    }

    private static long getResetDelayMs() {
        return 50; // 1 server tick = 50ms
    }

    private static int getPing() {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }

    private static void resetHitCount() {
        hitCount = 0;
        lastResetMs = System.currentTimeMillis();
    }
}
