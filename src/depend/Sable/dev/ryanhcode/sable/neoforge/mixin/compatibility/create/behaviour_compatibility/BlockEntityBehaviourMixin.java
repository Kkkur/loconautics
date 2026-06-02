/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour
 *  com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour
 *  com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.behaviour_compatibility;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockEntityBehaviour.class})
public abstract class BlockEntityBehaviourMixin {
    @Shadow
    public static <T extends BlockEntityBehaviour> T get(BlockEntity be, BehaviourType<T> type) {
        return null;
    }

    @Inject(method={"get(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/blockEntity/behaviour/BlockEntityBehaviour;"}, at={@At(value="HEAD")}, remap=false, cancellable=true)
    private static <T extends BlockEntityBehaviour> void sable$accountForSubLevels(BlockGetter reader, BlockPos pos, BehaviourType<T> type, CallbackInfoReturnable<T> cir) {
        if (reader instanceof Level) {
            ActiveSableCompanion helper;
            BlockEntity caughtBE;
            Level level = (Level)reader;
            if (BlockEntityBehaviourMixin.sable$checkType(type) && (caughtBE = (helper = Sable.HELPER).runIncludingSubLevels(level, pos.getCenter(), true, helper.getContaining(level, (Vec3i)pos), (subLevel, internalPos) -> level.getBlockEntity(internalPos))) != null) {
                cir.setReturnValue(BlockEntityBehaviourMixin.get(caughtBE, type));
            }
        }
    }

    @Unique
    private static boolean sable$checkType(BehaviourType<?> type) {
        return type == BeltProcessingBehaviour.TYPE || type == DirectBeltInputBehaviour.TYPE || type == TransportedItemStackHandlerBehaviour.TYPE || type == InvManipulationBehaviour.TYPE || type == EdgeInteractionBehaviour.TYPE;
    }
}
