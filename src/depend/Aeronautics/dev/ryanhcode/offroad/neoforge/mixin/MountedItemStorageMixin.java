/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.simibubi.create.api.contraption.storage.item.MountedItemStorage
 *  com.simibubi.create.content.contraptions.Contraption
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.offroad.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import dev.ryanhcode.offroad.content.contraptions.borehead_contraption.BoreheadBearingContraption;
import dev.ryanhcode.offroad.neoforge.mixin_helpers.WrappedWrappedMountedItemStorage;
import java.lang.ref.WeakReference;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={MountedItemStorage.class})
public class MountedItemStorageMixin {
    @WrapMethod(method={"getHandlerForMenu"})
    public IItemHandlerModifiable offroad$wrapHandler(StructureTemplate.StructureBlockInfo info, Contraption contraption, Operation<IItemHandlerModifiable> original) {
        IItemHandlerModifiable originalCall = (IItemHandlerModifiable)original.call(new Object[]{info, contraption});
        if (contraption instanceof BoreheadBearingContraption && originalCall != null) {
            return new WrappedWrappedMountedItemStorage(new WeakReference<Contraption>(contraption), originalCall);
        }
        return originalCall;
    }
}
