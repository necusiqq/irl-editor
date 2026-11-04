package org.qualet.flashbacklighting.light;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Runtime bridge to IRL Redactor's client-side light scene. */
public final class LightBridge {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Flashback Lighting");
    private static final Map<UUID, Object> LIGHTS = new HashMap<>();

    private static Constructor<?> lightConstructor;
    private static Method addMethod, removeMethod;
    private static Field xField, yField, zField, intensityField, radiusField, nameField;
    private static Field dirXField, dirYField, dirZField, redField, greenField, blueField;

    private LightBridge() {}

    public static void apply(UUID id, double x, double y, double z, float intensity, float radius,
            float dirX, float dirY, float dirZ, float red, float green, float blue) {
        try {
            ensureBridge();
            Object light = LIGHTS.get(id);
            if (light == null) {
                light = lightConstructor.newInstance();
                nameField.set(light, "Flashback Light");
                LIGHTS.put(id, light);
                addMethod.invoke(null, light);
            }
            xField.setDouble(light, x);
            yField.setDouble(light, y);
            zField.setDouble(light, z);
            intensityField.setFloat(light, intensity);
            radiusField.setFloat(light, radius);
            dirXField.setFloat(light, dirX);
            dirYField.setFloat(light, dirY);
            dirZField.setFloat(light, dirZ);
            redField.setFloat(light, clamp01(red));
            greenField.setFloat(light, clamp01(green));
            blueField.setFloat(light, clamp01(blue));
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.error("IRL Redactor light bridge failed", e);
        }
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    /** Remove all addon-owned lights, e.g. when the replay world changes. */
    public static void remove() {
        if (LIGHTS.isEmpty()) return;
        try {
            ensureBridge();
            for (Object light : LIGHTS.values()) removeMethod.invoke(null, light);
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.error("Could not remove addon-owned IRL Redactor lights", e);
        } finally {
            LIGHTS.clear();
        }
    }

    private static void ensureBridge() throws ReflectiveOperationException {
        if (lightConstructor != null) return;
        ClassLoader loader = LightBridge.class.getClassLoader();
        Class<?> scene = Class.forName("org.qualet.irlredactor.light.LightScene", true, loader);
        Class<?> placed = Class.forName("org.qualet.irlredactor.light.PlacedLight", true, loader);
        lightConstructor = placed.getConstructor();
        addMethod = scene.getMethod("add", placed);
        removeMethod = scene.getMethod("remove", placed);
        xField = placed.getField("x");
        yField = placed.getField("y");
        zField = placed.getField("z");
        intensityField = placed.getField("intensity");
        radiusField = placed.getField("radius");
        nameField = placed.getField("name");
        dirXField = placed.getField("dirX");
        dirYField = placed.getField("dirY");
        dirZField = placed.getField("dirZ");
        redField = placed.getField("r");
        greenField = placed.getField("g");
        blueField = placed.getField("b");
    }
}
