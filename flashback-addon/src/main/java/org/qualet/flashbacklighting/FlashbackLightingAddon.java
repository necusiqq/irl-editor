package org.qualet.flashbacklighting;

import com.moulberry.flashback.keyframe.KeyframeRegistry;
import com.moulberry.flashback.keyframe.types.CameraKeyframeType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.qualet.flashbacklighting.keyframe.FlashbackLightKeyframeType;
import org.qualet.flashbacklighting.light.LightBridge;

public final class FlashbackLightingAddon implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyframeRegistry.register(FlashbackLightKeyframeType.INSTANCE);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null) {
                LightBridge.remove();
            }
        });
    }
}
