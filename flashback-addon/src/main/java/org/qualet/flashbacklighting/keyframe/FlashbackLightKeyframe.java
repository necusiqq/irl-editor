package org.qualet.flashbacklighting.keyframe;

import com.moulberry.flashback.editor.ui.ImGuiHelper;
import com.moulberry.flashback.keyframe.Keyframe;
import com.moulberry.flashback.keyframe.KeyframeType;
import com.moulberry.flashback.keyframe.change.KeyframeChange;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class FlashbackLightKeyframe extends Keyframe {
    public UUID lightId = UUID.randomUUID();
    public double x, y, z;
    public float intensity = 1.0f;
    public float radius = 8.0f;
    public float dirX = 0.0f, dirY = -1.0f, dirZ = 0.0f;
    public float red = 1.0f, green = 1.0f, blue = 1.0f;
    public boolean spot = false;
    public float range = 12.0f, outerAngle = 35.0f, innerAngle = 25.0f;

    public FlashbackLightKeyframe() {}

    public FlashbackLightKeyframe(double x, double y, double z, float intensity, float radius) {
        this.x = x; this.y = y; this.z = z;
        this.intensity = intensity; this.radius = radius;
    }

    @Override public KeyframeType<?> keyframeType() { return FlashbackLightKeyframeType.INSTANCE; }
    @Override public Keyframe copy() {
        FlashbackLightKeyframe copy = new FlashbackLightKeyframe(x, y, z, intensity, radius);
        copy.lightId = this.lightId;
        copy.dirX = dirX; copy.dirY = dirY; copy.dirZ = dirZ;
        copy.red = red; copy.green = green; copy.blue = blue;
        copy.spot = spot; copy.range = range; copy.outerAngle = outerAngle; copy.innerAngle = innerAngle;
        copy.interpolationType(interpolationType());
        return copy;
    }
    @Override public @Nullable KeyframeChange createChange() {
        return new FlashbackLightChange(lightId, x, y, z, intensity, radius,
            dirX, dirY, dirZ, red, green, blue, spot, range, outerAngle, innerAngle);
    }

    @Override
    public @Nullable KeyframeChange createSmoothInterpolatedChange(Keyframe p1, Keyframe p2, Keyframe p3,
            float t0, float t1, float t2, float t3, float amount) {
        FlashbackLightKeyframe from = (FlashbackLightKeyframe) p1;
        FlashbackLightKeyframe to = (FlashbackLightKeyframe) p2;
        return from.createChange().interpolate(to.createChange(), amount);
    }

    @Override
    public @Nullable KeyframeChange createHermiteInterpolatedChange(Map<Float, Keyframe> keyframes, float tick) {
        Map.Entry<Float, Keyframe> before = null;
        Map.Entry<Float, Keyframe> after = null;
        for (Map.Entry<Float, Keyframe> entry : keyframes.entrySet()) {
            if (entry.getKey() <= tick) before = entry;
            if (entry.getKey() >= tick) { after = entry; break; }
        }
        if (before == null) return after == null ? createChange() : after.getValue().createChange();
        if (after == null || before.getKey().equals(after.getKey())) return before.getValue().createChange();
        double amount = (tick - before.getKey()) / (after.getKey() - before.getKey());
        return before.getValue().createChange().interpolate(after.getValue().createChange(), amount);
    }

    @Override
    public void renderEditKeyframe(Consumer<Consumer<Keyframe>> update) {
        float[] v = new float[1];
        v[0] = (float) x;
        if (ImGuiHelper.inputFloat("X", v)) update.accept(k -> ((FlashbackLightKeyframe) k).x = v[0]);
        v[0] = (float) y;
        if (ImGuiHelper.inputFloat("Y", v)) update.accept(k -> ((FlashbackLightKeyframe) k).y = v[0]);
        v[0] = (float) z;
        if (ImGuiHelper.inputFloat("Z", v)) update.accept(k -> ((FlashbackLightKeyframe) k).z = v[0]);
        v[0] = intensity;
        if (ImGuiHelper.inputFloat("Intensity", v)) update.accept(k -> ((FlashbackLightKeyframe) k).intensity = v[0]);
        v[0] = radius;
        if (ImGuiHelper.inputFloat("Radius", v)) update.accept(k -> ((FlashbackLightKeyframe) k).radius = v[0]);
        v[0] = dirX;
        if (ImGuiHelper.inputFloat("Direction X", v)) update.accept(k -> ((FlashbackLightKeyframe) k).dirX = v[0]);
        v[0] = dirY;
        if (ImGuiHelper.inputFloat("Direction Y", v)) update.accept(k -> ((FlashbackLightKeyframe) k).dirY = v[0]);
        v[0] = dirZ;
        if (ImGuiHelper.inputFloat("Direction Z", v)) update.accept(k -> ((FlashbackLightKeyframe) k).dirZ = v[0]);
        v[0] = red;
        if (ImGuiHelper.inputFloat("Red (0-1)", v)) update.accept(k -> ((FlashbackLightKeyframe) k).red = v[0]);
        v[0] = green;
        if (ImGuiHelper.inputFloat("Green (0-1)", v)) update.accept(k -> ((FlashbackLightKeyframe) k).green = v[0]);
        v[0] = blue;
        if (ImGuiHelper.inputFloat("Blue (0-1)", v)) update.accept(k -> ((FlashbackLightKeyframe) k).blue = v[0]);
        int[] lightType = { spot ? 1 : 0 };
        if (ImGuiHelper.combo("Light type", lightType, new String[] { "Point", "Spot" })) {
            update.accept(k -> ((FlashbackLightKeyframe) k).spot = lightType[0] == 1);
        }
        v[0] = range;
        if (ImGuiHelper.inputFloat("Spot range", v)) update.accept(k -> ((FlashbackLightKeyframe) k).range = Math.max(0.1f, v[0]));
        v[0] = outerAngle;
        if (ImGuiHelper.inputFloat("Spot outer angle", v)) update.accept(k -> ((FlashbackLightKeyframe) k).outerAngle = Math.max(1.0f, Math.min(179.0f, v[0])));
        v[0] = innerAngle;
        if (ImGuiHelper.inputFloat("Spot inner angle", v)) update.accept(k -> ((FlashbackLightKeyframe) k).innerAngle = Math.max(1.0f, Math.min(((FlashbackLightKeyframe) k).outerAngle, v[0])));
    }
}
