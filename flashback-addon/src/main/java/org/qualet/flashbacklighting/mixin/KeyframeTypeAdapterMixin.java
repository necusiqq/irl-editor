package org.qualet.flashbacklighting.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.moulberry.flashback.keyframe.Keyframe;
import com.moulberry.flashback.keyframe.interpolation.InterpolationType;
import org.qualet.flashbacklighting.keyframe.FlashbackLightKeyframe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(targets = "com.moulberry.flashback.keyframe.Keyframe$TypeAdapter")
public abstract class KeyframeTypeAdapterMixin {
    @Inject(method = "deserialize", at = @At("HEAD"), cancellable = true)
    private void flashbackLighting$deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context, CallbackInfoReturnable<Keyframe> cir) throws JsonParseException {
        if (!(json instanceof JsonObject object) || !object.has("type")
                || !"irl_light".equals(object.get("type").getAsString())) return;
        FlashbackLightKeyframe keyframe = context.deserialize(json, FlashbackLightKeyframe.class);
        keyframe.interpolationType(context.deserialize(object.get("interpolation_type"), InterpolationType.class));
        cir.setReturnValue(keyframe);
    }

    @Inject(method = "serialize", at = @At("HEAD"), cancellable = true)
    private void flashbackLighting$serialize(Keyframe src, Type typeOfSrc,
            JsonSerializationContext context, CallbackInfoReturnable<JsonElement> cir) {
        if (!(src instanceof FlashbackLightKeyframe keyframe)) return;
        JsonObject object = context.serialize(keyframe, FlashbackLightKeyframe.class).getAsJsonObject();
        object.addProperty("type", "irl_light");
        object.add("interpolation_type", context.serialize(keyframe.interpolationType()));
        cir.setReturnValue(object);
    }
}
