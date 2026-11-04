package org.qualet.flashbacklighting.keyframe;

import com.moulberry.flashback.editor.ui.ImGuiHelper;
import com.moulberry.flashback.keyframe.Keyframe;
import com.moulberry.flashback.keyframe.KeyframeType;
import com.moulberry.flashback.keyframe.change.KeyframeChange;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public final class FlashbackLightKeyframe extends Keyframe {
    public double x, y, z;
    public float intensity = 1.0f;
    public float radius = 8.0f;

    public FlashbackLightKeyframe() {}

    public FlashbackLightKeyframe(double x, double y, double z, float intensity, float radius) {
        this.x = x; this.y = y; this.z = z;
        this.intensity = intensity; this.radius = radius;
    }

    @Override public KeyframeType<?> keyframeType() { return FlashbackLightKeyframeType.INSTANCE; }
    @Override public Keyframe copy() {
        FlashbackLightKeyframe copy = new FlashbackLightKeyframe(x, y, z, intensity, radius);
        copy.interpolationType(interpolationType());
        return copy;
    }
    @Override public @Nullable KeyframeChange createChange() {
        return new FlashbackLightChange(x, y, z, intensity, radius);
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
    }
}
