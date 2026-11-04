package org.qualet.flashbacklighting;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.keyframe.KeyframeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import org.qualet.flashbacklighting.keyframe.FlashbackLightKeyframeType;
import org.qualet.flashbacklighting.light.LightBridge;

public final class FlashbackLightingAddon implements ClientModInitializer {
    private static ClientLevel lastLevel;
    private static boolean wasInReplay;

    @Override
    public void onInitializeClient() {
        KeyframeRegistry.register(FlashbackLightKeyframeType.INSTANCE);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != lastLevel) {
                LightBridge.remove();
                lastLevel = client.level;
            }

            boolean inReplay = Flashback.getReplayServer() != null;
            if (wasInReplay && !inReplay) {
                LightBridge.remove();
            }
            wasInReplay = inReplay;
        });
    }
}
