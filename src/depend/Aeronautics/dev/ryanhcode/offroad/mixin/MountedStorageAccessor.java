/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.storage.item.MountedItemStorage
 *  com.simibubi.create.content.contraptions.MountedStorageManager
 *  net.minecraft.core.BlockPos
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.ryanhcode.offroad.mixin;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import java.util.Map;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={MountedStorageManager.class})
public interface MountedStorageAccessor {
    @Accessor
    public Map<BlockPos, MountedItemStorage> getItemsBuilder();
}
