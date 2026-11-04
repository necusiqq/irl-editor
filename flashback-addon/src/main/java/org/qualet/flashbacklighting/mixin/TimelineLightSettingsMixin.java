package org.qualet.flashbacklighting.mixin;

import com.moulberry.flashback.editor.ui.windows.TimelineWindow;
import com.moulberry.flashback.keyframe.Keyframe;
import com.moulberry.flashback.state.EditorScene;
import com.moulberry.flashback.state.EditorState;
import imgui.moulberry90.ImGui;
import org.qualet.flashbacklighting.FlashbackLightEditorBridge;
import org.qualet.flashbacklighting.keyframe.FlashbackLightKeyframe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TimelineWindow.class)
public abstract class TimelineLightSettingsMixin {
    @Shadow private static EditorScene editorScene;
    @Shadow private static EditorState editorState;
    @Shadow private static int editingKeyframeTrack;
    @Shadow private static int editingKeyframeTick;

    @Inject(method = "renderKeyframeOptionsPopup", at = @At("TAIL"))
    private static void flashbackLighting$addOpenSettingsButton(int totalTicks, CallbackInfo ci) {
        if (editorScene == null || editorState == null || editingKeyframeTrack < 0
                || editingKeyframeTrack >= editorScene.keyframeTracks.size()) return;
        var track = editorScene.keyframeTracks.get(editingKeyframeTrack);
        Keyframe keyframe = track.keyframesByTick.get(editingKeyframeTick);
        if (!(keyframe instanceof FlashbackLightKeyframe)) return;

        if (ImGui.button("Open settings##FlashbackLightSettings")) {
            FlashbackLightEditorBridge.openSettings(editorScene, editorState,
                editingKeyframeTrack, editingKeyframeTick);
            ImGui.closeCurrentPopup();
        }
    }
}
