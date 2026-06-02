/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.impl.visualization.VisualManagerImpl
 *  dev.engine_room.flywheel.impl.visualization.storage.Storage
 *  dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.flywheel.SubLevelEmbedding
 *  dev.ryanhcode.sable.neoforge.mixinterface.compatibility.flywheel.BlockEntityStorageExtension
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.simulated_team.simulated.neoforge.mixin.diagram;

import dev.engine_room.flywheel.impl.visualization.VisualManagerImpl;
import dev.engine_room.flywheel.impl.visualization.storage.Storage;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.flywheel.SubLevelEmbedding;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.flywheel.BlockEntityStorageExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.mixin_interface.diagram.VisualManagerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={VisualManagerImpl.class})
public abstract class VisualManagerImplMixin<T, S extends Storage<T>>
implements VisualManagerExtension {
    @Shadow
    public abstract S getStorage();

    @Override
    public SubLevelEmbedding sable$getBEEmbeddingInfo(ClientSubLevel subLevel) {
        return ((BlockEntityStorageExtension)this.getStorage()).sable$getEmbeddingInfo((SubLevel)subLevel);
    }
}
