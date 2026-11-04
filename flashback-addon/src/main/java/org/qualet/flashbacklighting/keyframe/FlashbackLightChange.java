package org.qualet.flashbacklighting.keyframe;

import com.moulberry.flashback.keyframe.change.KeyframeChange;
import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import org.qualet.flashbacklighting.light.LightBridge;
import java.util.UUID;

public record FlashbackLightChange(UUID lightId, double x, double y, double z,
        float intensity, float radius, float dirX, float dirY, float dirZ,
        float red, float green, float blue, boolean spot,
        float range, float outerAngle, float innerAngle, float alpha,
        float beamStrength, float anisotropy, float vlDensity, float bulbSize,
        boolean entitiesOnly, boolean blocksOnly, boolean shadows,
        String cookie, float cookieRotation, float cookieScale, boolean cookieInvert) implements KeyframeChange {
    @Override public void apply(KeyframeHandler handler) {
        LightBridge.apply(lightId, x, y, z, intensity, radius, dirX, dirY, dirZ,
            red, green, blue, spot, range, outerAngle, innerAngle, alpha, beamStrength, anisotropy,
            vlDensity, bulbSize, entitiesOnly, blocksOnly, shadows, cookie, cookieRotation,
            cookieScale, cookieInvert);
    }
    @Override public KeyframeChange interpolate(KeyframeChange to, double amount) {
        FlashbackLightChange o = (FlashbackLightChange) to;
        return new FlashbackLightChange(lightId, lerp(x,o.x,amount), lerp(y,o.y,amount), lerp(z,o.z,amount),
            f(intensity,o.intensity,amount), f(radius,o.radius,amount),
            f(dirX,o.dirX,amount), f(dirY,o.dirY,amount), f(dirZ,o.dirZ,amount),
            f(red,o.red,amount), f(green,o.green,amount), f(blue,o.blue,amount),
            amount < 0.5 ? spot : o.spot, f(range,o.range,amount),
            f(outerAngle,o.outerAngle,amount), f(innerAngle,o.innerAngle,amount),
            f(alpha,o.alpha,amount), f(beamStrength,o.beamStrength,amount),
            f(anisotropy,o.anisotropy,amount), f(vlDensity,o.vlDensity,amount),
            f(bulbSize,o.bulbSize,amount), amount < 0.5 ? entitiesOnly : o.entitiesOnly,
            amount < 0.5 ? blocksOnly : o.blocksOnly, amount < 0.5 ? shadows : o.shadows,
            amount < 0.5 ? cookie : o.cookie, f(cookieRotation,o.cookieRotation,amount),
            f(cookieScale,o.cookieScale,amount), amount < 0.5 ? cookieInvert : o.cookieInvert);
    }
    private static float f(float a,float b,double t){return (float)lerp(a,b,t);}
    private static double lerp(double a,double b,double t){return a+(b-a)*t;}
}
