package dev.akatriggered.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {

    @Accessor("rightClickDelay")
    int getItemUseCooldown();

    @Accessor("rightClickDelay")
    void setItemUseCooldown(int cooldown);

    @Accessor("missTime")
    int getAttackCooldown();

    @Accessor("missTime")
    void setAttackCooldown(int cooldown);
}
