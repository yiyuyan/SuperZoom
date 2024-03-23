package cn.ksmcbrigade.sz.mixin;

import cn.ksmcbrigade.sz.SuperZoom;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Inject(method = "swapPaint",at = @At("HEAD"), cancellable = true)
    public void move(CallbackInfo ci){
        if(SuperZoom.EnabledTick){
            ci.cancel();
        }
    }
}
