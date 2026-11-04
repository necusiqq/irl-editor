package org.qualet.irlredactor.mixin.client;

import net.minecraft.client.Minecraft;
import org.qualet.irlredactor.editor.LightEditorScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the ImGui light editor immediately before the acquired window surface
 * is presented, after Minecraft has blitted the completed frame to it.
 *
 * <p>In Minecraft 26.3, GpuSurface is
 * {@code com.mojang.renderpearl.api.device.GpuSurface}. Keep this injection
 * aligned with the actual owner used by Minecraft's renderFrame bytecode.</p>
 */
@Mixin(Minecraft.class)
public class MinecraftClientImGuiMixin
{
    @Inject(
        method = "renderFrame(Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/renderpearl/api/device/GpuSurface;present()V",
            shift = At.Shift.BEFORE
        )
    )
    private void irl$renderImGuiOverlay(boolean advanceGameTime, CallbackInfo ci)
    {
        LightEditorScreen.renderActiveOverlay();
    }
}
