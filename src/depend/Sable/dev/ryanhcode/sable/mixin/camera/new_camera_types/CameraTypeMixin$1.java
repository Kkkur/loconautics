/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CameraType
 */
package dev.ryanhcode.sable.mixin.camera.new_camera_types;

import net.minecraft.client.CameraType;

static class CameraTypeMixin.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$client$CameraType;

    static {
        $SwitchMap$net$minecraft$client$CameraType = new int[CameraType.values().length];
        try {
            CameraTypeMixin.1.$SwitchMap$net$minecraft$client$CameraType[CameraType.FIRST_PERSON.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CameraTypeMixin.1.$SwitchMap$net$minecraft$client$CameraType[CameraType.THIRD_PERSON_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CameraTypeMixin.1.$SwitchMap$net$minecraft$client$CameraType[CameraType.THIRD_PERSON_FRONT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
