package org.qualet.irlredactor.imgui;

import imgui.ImGui;
import org.qualet.irlredactor.editor.LightEditorScreen;

/**
 * Decides when the ImGui editor should swallow input so it doesn't leak to the
 * game / a foreign screen (e.g. Replay Mod's timeline). Safe to call before ImGui
 * is initialized — {@link #active()} guards {@code ImGui.getIO()} from asserting.
 */
public final class ImGuiInput
{
    private ImGuiInput()
    {
    }

    public static boolean wantsMouse()
    {
        return active() && ImGui.getIO().getWantCaptureMouse();
    }

    public static boolean wantsKeyboard()
    {
        // WantCaptureKeyboard may remain true for the whole overlay (for example,
        // because of ImGui navigation focus), swallowing Minecraft keybinds such
        // as the editor movement-mode toggle. Suppress game keys only while an
        // actual ImGui item is active (notably InputText and interactive controls).
        return active() && ImGui.isAnyItemActive();
    }

    /** Overlay drawing this frame AND ImGui ready — guards getIO() from asserting. */
    private static boolean active()
    {
        return LightEditorScreen.isOverlayActive() && ImGuiRuntime.get().isInitialized();
    }
}
