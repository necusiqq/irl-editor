package org.qualet.flashbacklighting.keyframe;

import com.moulberry.flashback.keyframe.KeyframeType;
import com.moulberry.flashback.keyframe.change.KeyframeChange;
import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import com.moulberry.flashback.keyframe.handler.MinecraftKeyframeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class FlashbackLightKeyframeType implements KeyframeType<FlashbackLightKeyframe> {
    public static final FlashbackLightKeyframeType INSTANCE = new FlashbackLightKeyframeType();

    private FlashbackLightKeyframeType() {}

    @Override public String name() { return "Light"; }
    @Override public String id() { return "IRL_LIGHT"; }
    @Override public String icon() { return "\ue04d"; }
    @Override public Class<? extends KeyframeChange> keyframeChangeType() { return FlashbackLightChange.class; }
    @Override public boolean supportsHandler(KeyframeHandler handler) { return handler instanceof MinecraftKeyframeHandler; }
    @Override public boolean allowApplyingDuplicateKeyframeChanges() { return true; }

    @Override
    public @Nullable FlashbackLightKeyframe createDirect() {
        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera == null) camera = Minecraft.getInstance().player;
        if (camera == null) return null;
        FlashbackLightKeyframe keyframe = new FlashbackLightKeyframe(
            camera.getX(), camera.getY() + camera.getEyeHeight(), camera.getZ(), 1.0f, 8.0f);
        var look = camera.getLookAngle();
        keyframe.dirX = (float) look.x;
        keyframe.dirY = (float) look.y;
        keyframe.dirZ = (float) look.z;
        return keyframe;
    }

    @Override public KeyframeCreatePopup<FlashbackLightKeyframe> createPopup() { return null; }
}
