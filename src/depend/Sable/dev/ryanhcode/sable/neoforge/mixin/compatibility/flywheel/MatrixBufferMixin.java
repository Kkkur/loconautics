/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.backend.engine.indirect.MatrixBuffer
 *  org.spongepowered.asm.mixin.Debug
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.flywheel;

import dev.engine_room.flywheel.backend.engine.indirect.MatrixBuffer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Debug(export=true)
@Mixin(value={MatrixBuffer.class})
public class MatrixBufferMixin {
    @ModifyArg(method={"<init>"}, at=@At(value="INVOKE", target="Ldev/engine_room/flywheel/backend/engine/indirect/ResizableStorageArray;<init>(J)V"), index=0)
    private long sable$overrideMatrixSize(long stride) {
        return 192L;
    }
}
