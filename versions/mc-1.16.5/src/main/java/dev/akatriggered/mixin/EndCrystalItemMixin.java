package dev.akatriggered.mixin;

import dev.akatriggered.command.OptimizerCommand;
import dev.akatriggered.util.ActionResultResolver;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (OptimizerCommand.defaultMode) {
            cir.setReturnValue(ActionResultResolver.pass());
        }
    }
}
