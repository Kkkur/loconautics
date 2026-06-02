/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.belt.BeltBlock
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.belt;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.neoforge.physics.callback.BeltBlockCallback;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={BeltBlock.class})
public class BeltBlockMixin
implements BlockWithSubLevelCollisionCallback {
    @Override
    public BlockSubLevelCollisionCallback sable$getCallback() {
        return BeltBlockCallback.INSTANCE;
    }
}
