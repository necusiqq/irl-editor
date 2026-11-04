package org.qualet.irlredactor.mixin.client;

import net.minecraft.client.Minecraft;
import org.qualet.irlredactor.editor.LightEditorScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the ImGui light editor after Minecraft has blitted the completed frame
 * to the window surface, but immediately before that surface is presented.
 *
 * <p>Minecraft 26.3 moved frame rendering out of the old runTick path and
 * replaced the old framebuffer blit with the GpuSurface presentation flow.
 * Hooking GpuSurface.present keeps the overlay on top of the already-rendered
 * world and deferred GUI, while the window's back buffer is still available.</p>
 */
@Mixin(Minecraft.class)
public class MinecraftClientImGuiMixin
{
    @Inject(
        method = "renderFrame(Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/GpuSurface;present()V",
            shift = At.Shift.BEFORE
        )
    )
    private void irl$renderImGuiOverlay(boolean advanceGameTime, CallbackInfo ci)
    {
        LightEditorScreen.renderActiveOverlay();
    }
}
