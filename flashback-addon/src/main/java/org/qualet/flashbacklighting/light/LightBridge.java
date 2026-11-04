package org.qualet.flashbacklighting.light;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Runtime bridge to IRL Redactor. Reflection keeps the addon binary independent
 * of IRL Redactor's implementation classes while the Fabric dependency enforces
 * that the mod is present. */
public final class LightBridge {
    private static Object light;
    private static boolean added;
    private static Class<?> lightSceneClass;
    private static Constructor<?> lightConstructor;
    private static Method addMethod, removeMethod;
    private static Field xField, yField, zField, intensityField, radiusField, nameField;

    private LightBridge() {}

    public static void apply(double x, double y, double z, float intensity, float radius) {
        try {
            ensureLight();
            xField.setDouble(light, x);
            yField.setDouble(light, y);
            zField.setDouble(light, z);
            intensityField.setFloat(light, intensity);
            radiusField.setFloat(light, radius);
            if (!added) {
                addMethod.invoke(null, light);
                added = true;
            }
        } catch (ReflectiveOperationException | LinkageError e) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Flashback Lighting: IRL Redactor bridge failed: " + e.getClass().getSimpleName()), true);
            }
        }
    }

    public static void remove() {
        if (!added || light == null) return;
        try {
            removeMethod.invoke(null, light);
        } catch (ReflectiveOperationException ignored) {
        }
        added = false;
        light = null;
    }

    private static void ensureLight() throws ReflectiveOperationException {
        if (light != null) return;
        ClassLoader loader = LightBridge.class.getClassLoader();
        lightSceneClass = Class.forName("org.qualet.irlredactor.light.LightScene", true, loader);
        Class<?> placedLightClass = Class.forName("org.qualet.irlredactor.light.PlacedLight", true, loader);
        lightConstructor = placedLightClass.getConstructor();
        light = lightConstructor.newInstance();
        addMethod = lightSceneClass.getMethod("add", placedLightClass);
        removeMethod = lightSceneClass.getMethod("remove", placedLightClass);
        xField = placedLightClass.getField("x");
        yField = placedLightClass.getField("y");
        zField = placedLightClass.getField("z");
        intensityField = placedLightClass.getField("intensity");
        radiusField = placedLightClass.getField("radius");
        nameField = placedLightClass.getField("name");
        nameField.set(light, "Flashback Light");
    }
}
