package org.qualet.irlredactor.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.GameRenderer;
import org.qualet.irl.light.FramePipeline;
import org.qualet.irl.light.iris.IrisShadersState;
import org.qualet.irlredactor.client.diag.VlProfiler;
import org.qualet.irlredactor.light.LightDriver;

/**
 * Per-frame entry point for the light + shadow pipeline.
 * Minecraft 26.3's renderLevel method has no parameters, so both callbacks
 * intentionally accept only CallbackInfo.
 */
@Mixin(GameRenderer.class)
public class GameRendererLightMixin
{
    @Inject(method = "renderLevel()V", at = @At("HEAD"))
    private void irlite$collectLights(CallbackInfo ci)
    {
        // 26.3 renderLevel() has no tick counter parameter. Use the current
        // frame's partial tick supplied by the client timer.
        float tickDelta = net.minecraft.client.Minecraft.getInstance()
            .getDeltaTracker().getGameTimeDeltaPartialTick(true);

        // Dev GPU profiler (editor "perf" section): the shadow bake below runs
        // strictly before the Iris pass sequence, so its GL_TIME_ELAPSED bracket
        // never nests with the per-pass brackets. collect inside frame() issues
        // no GL, so the bracket measures bake GPU work only. The core-side
        // ShadowBakeProbe (installed in IRLRedactorClient) switches this bracket
        // to bake-* siblings at the bakeInner seams; the endPass below closes
        // whichever segment is open. Everything no-ops while collection is off.
        VlProfiler.frameTick();
        VlProfiler.beginPass(VlProfiler.PASS_BAKE);
        // Depth textures are freed on the dormant transition; re-ramp auto-shadow
        // first-bakes when shaders return instead of baking every cube in one frame.
        FramePipeline.frame(tickDelta, IrisShadersState::shadersDisabled, LightDriver::collect,
            LightDriver::resetAutoShadowRamp);
        VlProfiler.endPass();
    }

    /**
     * Deferred SSBO upload — the light buffer is made relative to the current-frame eye
     * that the shaderpack reconstructs fragments against, not a stale camera.
     *
     * <p>The upload is deferred to a strictly-later bytecode point than the HEAD collect —
     * right after extractCamera(F), so collect+bake at HEAD always runs first and the upload
     * lands before world rendering / Iris activation.</p>
     */
    @Inject(method = "renderLevel()V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/GameRenderer;extractCamera(F)V",
                     shift = At.Shift.AFTER,
                     ordinal = 0),
            require = 1)
    private void irlite$uploadLights(CallbackInfo ci)
    {
        FramePipeline.uploadIfPending();
    }
}
