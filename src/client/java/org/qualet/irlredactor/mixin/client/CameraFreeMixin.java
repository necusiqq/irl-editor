package org.qualet.irlredactor.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.qualet.irlredactor.client.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Free-fly camera: overrides the camera position after vanilla updates it.
 * Minecraft 26.3 replaced the old setup(Level, Entity, ...) method with
 * update(DeltaTracker), so the injection now targets that method.
 */
@Mixin(Camera.class)
public abstract class CameraFreeMixin
{
    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Inject(method = "update(Lnet/minecraft/client/DeltaTracker;)V", at = @At("TAIL"))
    private void irl$freeCamera(DeltaTracker deltaTracker, CallbackInfo ci)
    {
        if (FreeCamera.isActive())
        {
            FreeCamera.advance();
            this.setPosition(FreeCamera.x(), FreeCamera.y(), FreeCamera.z());
        }
    }
}
