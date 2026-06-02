/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.bell.AbstractBellBlock
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.impact;

import com.simibubi.create.content.equipment.bell.AbstractBellBlock;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.neoforge.physics.callback.AbstractBellBlockCallback;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={AbstractBellBlock.class})
public class AbstractBellBlockMixin
implements BlockWithSubLevelCollisionCallback {
    @Override
    public BlockSubLevelCollisionCallback sable$getCallback() {
        return AbstractBellBlockCallback.INSTANCE;
    }
}
