package org.qualet.flashbacklighting;

import com.moulberry.flashback.state.EditorScene;
import com.moulberry.flashback.state.EditorState;
import com.moulberry.flashback.state.KeyframeTrack;
import org.qualet.flashbacklighting.keyframe.FlashbackLightKeyframe;
import org.qualet.flashbacklighting.light.LightBridge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

/** Connects one Flashback keyframe to the existing IRL Redactor light inspector. */
public final class FlashbackLightEditorBridge {
    private FlashbackLightEditorBridge() {}

    public static void openSettings(EditorScene scene, EditorState state, int trackIndex, int tick) {
        try {
            if (scene == null || state == null || trackIndex < 0 || trackIndex >= scene.keyframeTracks.size()) return;
            KeyframeTrack track = scene.keyframeTracks.get(trackIndex);
            if (tick < 0 || !(track.keyframesByTick.get(tick) instanceof FlashbackLightKeyframe keyframe)) return;

            UUID id = trackId(track);
            LightBridge.apply(id, keyframe.x, keyframe.y, keyframe.z, keyframe.intensity, keyframe.radius,
                keyframe.dirX, keyframe.dirY, keyframe.dirZ, keyframe.red, keyframe.green, keyframe.blue,
                keyframe.spot, keyframe.range, keyframe.outerAngle, keyframe.innerAngle);
            Object light = LightBridge.getLight(id);
            if (light == null) return;

            LightBridge.setEditListener(id, edited -> {
                boolean changed = false;
                changed |= set(keyframe, "x", number(edited, "x").doubleValue());
                changed |= set(keyframe, "y", number(edited, "y").doubleValue());
                changed |= set(keyframe, "z", number(edited, "z").doubleValue());
                changed |= set(keyframe, "intensity", number(edited, "intensity").floatValue());
                changed |= set(keyframe, "radius", number(edited, "radius").floatValue());
                changed |= set(keyframe, "dirX", number(edited, "dirX").floatValue());
                changed |= set(keyframe, "dirY", number(edited, "dirY").floatValue());
                changed |= set(keyframe, "dirZ", number(edited, "dirZ").floatValue());
                changed |= set(keyframe, "red", number(edited, "r").floatValue());
                changed |= set(keyframe, "green", number(edited, "g").floatValue());
                changed |= set(keyframe, "blue", number(edited, "b").floatValue());
                changed |= set(keyframe, "spot", "SPOT".equals(String.valueOf(read(edited, "type"))));
                changed |= set(keyframe, "range", number(edited, "range").floatValue());
                changed |= set(keyframe, "outerAngle", number(edited, "outerAngleDeg").floatValue());
                changed |= set(keyframe, "innerAngle", number(edited, "innerAngleDeg").floatValue());
                if (changed) state.markDirty();
            });

            Class<?> screen = Class.forName("org.qualet.irlredactor.editor.LightEditorScreen");
            Method open = screen.getMethod("openForLight", light.getClass());
            open.invoke(null, light);
        } catch (Throwable e) {
            org.slf4j.LoggerFactory.getLogger("Flashback Lighting")
                .error("Could not open IRL Redactor settings for the selected Flashback light", e);
        }
    }

    private static UUID trackId(KeyframeTrack track) throws Exception {
        Method method = KeyframeTrack.class.getMethod("flashbackLighting$getTrackId", KeyframeTrack.class);
        return (UUID) method.invoke(null, track);
    }

    private static Object read(Object target, String name) {
        try { return target.getClass().getField(name).get(target); }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }

    private static Number number(Object target, String name) {
        return (Number) read(target, name);
    }

    private static boolean set(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getField(name);
            Object old = field.get(target);
            if (old instanceof Number a && value instanceof Number b) {
                if (Double.compare(a.doubleValue(), b.doubleValue()) == 0) return false;
            } else if (java.util.Objects.equals(old, value)) {
                return false;
            }
            field.set(target, value);
            return true;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
