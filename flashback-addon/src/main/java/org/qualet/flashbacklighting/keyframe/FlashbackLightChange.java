package org.qualet.flashbacklighting.keyframe;

import com.moulberry.flashback.keyframe.change.KeyframeChange;
import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import org.qualet.flashbacklighting.light.LightBridge;

public record FlashbackLightChange(double x, double y, double z, float intensity, float radius) implements KeyframeChange {
    @Override
    public void apply(KeyframeHandler handler) {
        LightBridge.apply(x, y, z, intensity, radius);
    }

    @Override
    public KeyframeChange interpolate(KeyframeChange to, double amount) {
        FlashbackLightChange other = (FlashbackLightChange) to;
        return new FlashbackLightChange(
            lerp(x, other.x, amount), lerp(y, other.y, amount), lerp(z, other.z, amount),
            (float) lerp(intensity, other.intensity, amount),
            (float) lerp(radius, other.radius, amount)
        );
    }

    private static double lerp(double from, double to, double amount) {
        return from + (to - from) * amount;
    }
}
