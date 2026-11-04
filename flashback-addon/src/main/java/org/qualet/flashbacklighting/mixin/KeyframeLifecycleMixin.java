package org.qualet.flashbacklighting.mixin;

import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import com.moulberry.flashback.state.EditorState;
import org.qualet.flashbacklighting.light.LightBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditorState.class)
public abstract class KeyframeLifecycleMixin {
    @Inject(method = "applyKeyframes(Lcom/moulberry/flashback/keyframe/handler/KeyframeHandler;FJ)V", at = @At("HEAD"))
    private void flashbackLighting$beginFrame(KeyframeHandler handler, float tick, long stamp, CallbackInfo ci) {
        LightBridge.beginFrame(handler);
    }

    @Inject(method = "applyKeyframes(Lcom/moulberry/flashback/keyframe/handler/KeyframeHandler;FJ)V", at = @At("RETURN"))
    private void flashbackLighting$endFrame(KeyframeHandler handler, float tick, long stamp, CallbackInfo ci) {
        LightBridge.endFrame(handler);
    }
}
