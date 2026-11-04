package org.qualet.irlredactor.mixin.client;

import com.mojang.blaze3d.platform.SDLEventHandler;
import org.lwjgl.sdl.SDL_Event;
import org.qualet.irlredactor.editor.LightEditorScreen;
import org.qualet.irlredactor.imgui.ImGuiRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forwards Minecraft's already-polled SDL3 input events to Dear ImGui.
 * Minecraft 26.3 no longer uses GLFW callbacks, so the old ImGui GLFW backend
 * cannot be used here.
 */
@Mixin(SDLEventHandler.class)
public abstract class SDLEventImGuiMixin
{
    private static void irl$forward(SDL_Event event)
    {
        if (LightEditorScreen.isOverlayActive())
        {
            ImGuiRuntime.processSdlEvent(event.address());
        }
    }

    @Inject(method = "handleKeyEvent(Lorg/lwjgl/sdl/SDL_Event;)V", at = @At("HEAD"))
    private void irl$forwardKey(SDL_Event event, CallbackInfo ci)
    {
        irl$forward(event);
    }

    @Inject(method = "handleTextInputEvent(Lorg/lwjgl/sdl/SDL_Event;)V", at = @At("HEAD"))
    private void irl$forwardText(SDL_Event event, CallbackInfo ci)
    {
        irl$forward(event);
    }

    @Inject(method = "handleMouseMotionEvent(Lorg/lwjgl/sdl/SDL_Event;)V", at = @At("HEAD"))
    private void irl$forwardMouseMotion(SDL_Event event, CallbackInfo ci)
    {
        irl$forward(event);
    }

    @Inject(method = "handleMouseButtonEvent(Lorg/lwjgl/sdl/SDL_Event;)V", at = @At("HEAD"))
    private void irl$forwardMouseButton(SDL_Event event, CallbackInfo ci)
    {
        irl$forward(event);
    }

    @Inject(method = "handleMouseWheelEvent(Lorg/lwjgl/sdl/SDL_Event;)V", at = @At("HEAD"))
    private void irl$forwardMouseWheel(SDL_Event event, CallbackInfo ci)
    {
        irl$forward(event);
    }
}
