package org.qualet.flashbacklighting.light;

import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import com.moulberry.flashback.keyframe.handler.MinecraftKeyframeHandler;
import java.lang.reflect.*;
import java.util.*;

public final class LightBridge {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Flashback Lighting");
    private static final Map<UUID,Object> LIGHTS = new HashMap<>();
    private static final Set<UUID> ACTIVE_THIS_FRAME = new HashSet<>();
    private static Constructor<?> ctor;
    private static Method add, remove, all;
    private static Class<?> placedClass;

    private LightBridge() {}

    public static void beginFrame(KeyframeHandler handler) {
        if (handler instanceof MinecraftKeyframeHandler) ACTIVE_THIS_FRAME.clear();
    }

    public static void endFrame(KeyframeHandler handler) {
        if (!(handler instanceof MinecraftKeyframeHandler)) return;
        try {
            ensure();
            Iterator<Map.Entry<UUID,Object>> iterator = LIGHTS.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID,Object> entry = iterator.next();
                if (!ACTIVE_THIS_FRAME.contains(entry.getKey())) {
                    remove.invoke(null, entry.getValue());
                    iterator.remove();
                }
            }
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.error("Could not reconcile addon-owned lights", e);
        } finally {
            ACTIVE_THIS_FRAME.clear();
        }
    }

    public static void apply(UUID id,double x,double y,double z,float intensity,float radius,
            float dx,float dy,float dz,float red,float green,float blue,
            boolean spot,float range,float outer,float inner) {
        try {
            ensure();
            Object l=LIGHTS.get(id);
            boolean created = l == null;
            if (created) l=ctor.newInstance();
            field("name").set(l,"Flashback Light");
            field("x").setDouble(l,x); field("y").setDouble(l,y); field("z").setDouble(l,z);
            field("intensity").setFloat(l,intensity); field("radius").setFloat(l,radius);
            field("dirX").setFloat(l,dx); field("dirY").setFloat(l,dy); field("dirZ").setFloat(l,dz);
            field("r").setFloat(l,clamp(red)); field("g").setFloat(l,clamp(green)); field("b").setFloat(l,clamp(blue));
            field("range").setFloat(l,Math.max(0.1f,range));
            field("outerAngleDeg").setFloat(l,clampAngle(outer));
            field("innerAngleDeg").setFloat(l,Math.min(clampAngle(outer),clampAngle(inner)));
            @SuppressWarnings({"rawtypes","unchecked"}) Object type=Enum.valueOf((Class)field("type").getType(),spot?"SPOT":"POINT");
            field("type").set(l,type);
            if (created) LIGHTS.put(id,l);
            // IRL Redactor can clear its scene independently (for example when
            // its editor resets the placed-light list). Reattach our stable
            // instance instead of silently losing the animated light.
            if (!((List<?>) all.invoke(null)).contains(l)) add.invoke(null,l);
            ACTIVE_THIS_FRAME.add(id);
        } catch(ReflectiveOperationException|LinkageError e) {
            LOGGER.error("IRL Redactor light bridge failed",e);
        }
    }

    private static float clamp(float v){return Math.max(0f,Math.min(1f,v));}
    private static float clampAngle(float v){return Math.max(1f,Math.min(179f,v));}

    public static void remove(){
        if(LIGHTS.isEmpty()) { ACTIVE_THIS_FRAME.clear(); return; }
        try {
            ensure();
            for(Object l:LIGHTS.values()) remove.invoke(null,l);
        } catch(ReflectiveOperationException|LinkageError e) {
            LOGGER.error("Could not remove addon-owned lights",e);
        } finally {
            LIGHTS.clear();
            ACTIVE_THIS_FRAME.clear();
        }
    }

    public static Object getLight(UUID id) {
        return LIGHTS.get(id);
    }

    public static void setEditListener(UUID id, java.util.function.Consumer<Object> listener) {
        try {
            ensure();
            Object light = LIGHTS.get(id);
            if (light != null) field("editListener").set(light, listener);
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.error("Could not bind the IRL editor to a Flashback light", e);
        }
    }

    private static Field field(String name) throws NoSuchFieldException { return placedClass.getField(name); }

    private static void ensure() throws ReflectiveOperationException {
        if(ctor!=null)return;
        ClassLoader cl=LightBridge.class.getClassLoader();
        Class<?> scene=Class.forName("org.qualet.irlredactor.light.LightScene",true,cl);
        placedClass=Class.forName("org.qualet.irlredactor.light.PlacedLight",true,cl);
        ctor=placedClass.getConstructor();
        add=scene.getMethod("add",placedClass);
        remove=scene.getMethod("remove",placedClass);
        all=scene.getMethod("all");
    }
}
