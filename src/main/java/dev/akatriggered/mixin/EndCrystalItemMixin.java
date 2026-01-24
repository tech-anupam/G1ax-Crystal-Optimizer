package dev.akatriggered.mixin;

import dev.akatriggered.command.OptimizerCommand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!OptimizerCommand.fastCrystal) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        ItemStack mainHandStack = mc.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL)) return;

        BlockHitResult lookPos = getLookPosition(mc);
        if (isValidPlacement(mc, lookPos.getBlockPos())) {
            HitResult hitResult = mc.crosshairTarget;
            if (hitResult instanceof BlockHitResult hit) {
                BlockPos block = hit.getBlockPos();
                if (canPlaceCrystal(mc, block)) {
                    // Allow normal placement - no duplication
                    return;
                }
            }
        }
    }

    private boolean isValidPlacement(MinecraftClient mc, BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK);
    }

    private BlockHitResult getLookPosition(MinecraftClient mc) {
        Vec3d camPos = mc.player.getEyePos();
        Vec3d lookVec = getLookVector(mc);
        return mc.world.raycast(new RaycastContext(
            camPos, 
            camPos.add(lookVec.multiply(4.5)), 
            RaycastContext.ShapeType.OUTLINE, 
            RaycastContext.FluidHandling.NONE, 
            mc.player
        ));
    }

    private Vec3d getLookVector(MinecraftClient mc) {
        float f = (float) Math.PI / 180;
        float pi = (float) Math.PI;
        float f1 = MathHelper.cos(-mc.player.getYaw() * f - pi);
        float f2 = MathHelper.sin(-mc.player.getYaw() * f - pi);
        float f3 = -MathHelper.cos(-mc.player.getPitch() * f);
        float f4 = MathHelper.sin(-mc.player.getPitch() * f);
        return new Vec3d(f2 * f3, f4, f1 * f3).normalize();
    }

    private boolean canPlaceCrystal(MinecraftClient mc, BlockPos block) {
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
    }
}
