package org.qualet.flashbacklighting.keyframe;

import com.moulberry.flashback.keyframe.change.KeyframeChange;
import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import org.qualet.flashbacklighting.light.LightBridge;

import java.util.UUID;

public record FlashbackLightChange(UUID lightId, double x, double y, double z,
        float intensity, float radius, float dirX, float dirY, float dirZ,
        float red, float green, float blue) implements KeyframeChange {
    @Override
    public void apply(KeyframeHandler handler) {
        LightBridge.apply(lightId, x, y, z, intensity, radius,
            dirX, dirY, dirZ, red, green, blue);
    }

    @Override
    public KeyframeChange interpolate(KeyframeChange to, double amount) {
        FlashbackLightChange other = (FlashbackLightChange) to;
        return new FlashbackLightChange(
            lightId, lerp(x, other.x, amount), lerp(y, other.y, amount), lerp(z, other.z, amount),
            f(intensity, other.intensity, amount), f(radius, other.radius, amount),
            f(dirX, other.dirX, amount), f(dirY, other.dirY, amount), f(dirZ, other.dirZ, amount),
            f(red, other.red, amount), f(green, other.green, amount), f(blue, other.blue, amount)
        );
    }

    private static float f(float a, float b, double t) { return (float) lerp(a, b, t); }
    private static double lerp(double from, double to, double amount) {
        return from + (to - from) * amount;
    }
}
