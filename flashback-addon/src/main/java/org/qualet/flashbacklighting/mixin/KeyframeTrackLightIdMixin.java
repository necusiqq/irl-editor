package org.qualet.flashbacklighting.mixin;

import com.moulberry.flashback.keyframe.change.KeyframeChange;
import com.moulberry.flashback.state.KeyframeTrack;
import com.moulberry.flashback.state.RealTimeMapping;
import org.qualet.flashbacklighting.keyframe.FlashbackLightChange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

@Mixin(KeyframeTrack.class)
public abstract class KeyframeTrackLightIdMixin {
    @Unique
    private static final Map<KeyframeTrack, UUID> flashbackLighting$trackIds = new WeakHashMap<>();

    @Unique
    public static UUID flashbackLighting$getTrackId(KeyframeTrack track) {
        synchronized (flashbackLighting$trackIds) {
            return flashbackLighting$trackIds.computeIfAbsent(track, ignored -> UUID.randomUUID());
        }
    }

    @Inject(method = "createKeyframeChange", at = @At("RETURN"), cancellable = true)
    private void flashbackLighting$useTrackId(float tick, RealTimeMapping mapping,
            CallbackInfoReturnable<KeyframeChange> cir) {
        KeyframeChange change = cir.getReturnValue();
        if (!(change instanceof FlashbackLightChange lightChange)) return;

        KeyframeTrack track = (KeyframeTrack) (Object) this;
        UUID id = flashbackLighting$getTrackId(track);

        cir.setReturnValue(new FlashbackLightChange(id,
            lightChange.x(), lightChange.y(), lightChange.z(),
            lightChange.intensity(), lightChange.radius(),
            lightChange.dirX(), lightChange.dirY(), lightChange.dirZ(),
            lightChange.red(), lightChange.green(), lightChange.blue(),
            lightChange.spot(), lightChange.range(),
            lightChange.outerAngle(), lightChange.innerAngle()));
    }
}
