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

    private static Class<?> lightSceneClass;
    private static Class<?> placedLightClass;
    private static Constructor<?> lightConstructor;
    private static Method addMethod, removeMethod;
    private static Field xField, yField, zField, intensityField, radiusField, nameField;

    private LightBridge() {}

    public static void apply(UUID id, double x, double y, double z, float intensity, float radius) {
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
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.error("IRL Redactor light bridge failed", e);
        }
    }

    /** Remove all addon-owned lights, e.g. when the replay world changes. */
    public static void remove() {
        if (LIGHTS.isEmpty()) return;
        try {
            ensureBridge();
            for (Object light : LIGHTS.values()) {
                removeMethod.invoke(null, light);
            }
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.error("Could not remove addon-owned IRL Redactor lights", e);
        } finally {
            LIGHTS.clear();
        }
    }

    private static void ensureBridge() throws ReflectiveOperationException {
        if (lightConstructor != null) return;
        ClassLoader loader = LightBridge.class.getClassLoader();
        lightSceneClass = Class.forName("org.qualet.irlredactor.light.LightScene", true, loader);
        placedLightClass = Class.forName("org.qualet.irlredactor.light.PlacedLight", true, loader);
        lightConstructor = placedLightClass.getConstructor();
        addMethod = lightSceneClass.getMethod("add", placedLightClass);
        removeMethod = lightSceneClass.getMethod("remove", placedLightClass);
        xField = placedLightClass.getField("x");
        yField = placedLightClass.getField("y");
        zField = placedLightClass.getField("z");
        intensityField = placedLightClass.getField("intensity");
        radiusField = placedLightClass.getField("radius");
        nameField = placedLightClass.getField("name");
    }
}
