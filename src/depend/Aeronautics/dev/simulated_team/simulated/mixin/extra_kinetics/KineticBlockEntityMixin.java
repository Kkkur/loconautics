/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.extra_kinetics;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.simulated_team.simulated.mixin_interface.extra_kinetics.KineticBlockEntityExtension;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={KineticBlockEntity.class})
public abstract class KineticBlockEntityMixin
extends SmartBlockEntity
implements KineticBlockEntityExtension {
    @Shadow
    protected float speed;
    @Shadow
    private int validationCountdown;
    @Unique
    private boolean simulated$extraKineticsConnected = false;

    public KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract boolean hasSource();

    @Shadow
    public abstract void initialize();

    @Override
    public void simulated$setConnectedToExtraKinetics(boolean connectedToExtraKinetics) {
        this.simulated$extraKineticsConnected = connectedToExtraKinetics;
    }

    @Override
    public boolean simulated$getConnectedToExtraKinetics() {
        return this.simulated$extraKineticsConnected;
    }

    @Inject(method={"switchToBlockState"}, at={@At(value="TAIL")})
    private static void switchExtraKinetics(Level world, BlockPos pos, BlockState state, CallbackInfo ci, @Local BlockEntity be) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        if (be instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)be).getExtraKinetics()) != null && extraKinetics.hasNetwork()) {
            extraKinetics.getOrCreateNetwork().remove(extraKinetics);
            extraKinetics.detachKinetics();
            extraKinetics.removeSource();
            if (extraKinetics instanceof GeneratingKineticBlockEntity) {
                GeneratingKineticBlockEntity gbe = (GeneratingKineticBlockEntity)extraKinetics;
                gbe.reActivateSource = true;
            }
        }
    }

    @Redirect(method={"validateKinetics"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    public BlockEntity simulated$useProperSource(Level instance, BlockPos blockPos) {
        BlockEntity be = instance.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics) {
            ExtraKinetics ek = (ExtraKinetics)be;
            if (this.simulated$extraKineticsConnected) {
                be = ek.getExtraKinetics();
            }
        }
        return be;
    }

    @Redirect(method={"setSource"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    public BlockEntity simulated$useProperSource2(Level instance, BlockPos blockPos) {
        BlockEntity be = instance.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics) {
            ExtraKinetics ek = (ExtraKinetics)be;
            if (blockPos instanceof ExtraBlockPos) {
                ExtraBlockPos exp = (ExtraBlockPos)blockPos;
                this.simulated$extraKineticsConnected = true;
                be = ek.getExtraKinetics();
            }
        }
        return be;
    }

    public void setLevel(Level level) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        super.setLevel(level);
        KineticBlockEntityMixin kineticBlockEntityMixin = this;
        if (kineticBlockEntityMixin instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)((Object)kineticBlockEntityMixin)).getExtraKinetics()) != null) {
            extraKinetics.setLevel(level);
        }
    }

    public void setBlockState(BlockState blockState) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        super.setBlockState(blockState);
        KineticBlockEntityMixin kineticBlockEntityMixin = this;
        if (kineticBlockEntityMixin instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)((Object)kineticBlockEntityMixin)).getExtraKinetics()) != null) {
            extraKinetics.setBlockState(blockState);
        }
    }

    public void invalidate() {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        super.invalidate();
        KineticBlockEntityMixin kineticBlockEntityMixin = this;
        if (kineticBlockEntityMixin instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)((Object)kineticBlockEntityMixin)).getExtraKinetics()) != null) {
            extraKinetics.invalidate();
        }
    }

    @Inject(method={"remove"}, at={@At(value="TAIL")}, remap=false)
    public void injectRemove(CallbackInfo ci) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        KineticBlockEntityMixin kineticBlockEntityMixin = this;
        if (kineticBlockEntityMixin instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)((Object)kineticBlockEntityMixin)).getExtraKinetics()) != null) {
            extraKinetics.remove();
        }
    }

    @Inject(method={"removeSource"}, at={@At(value="TAIL")}, remap=false)
    public void simulated$removeConnected(CallbackInfo ci) {
        this.simulated$extraKineticsConnected = false;
    }

    @Inject(method={"write"}, at={@At(value="TAIL")}, remap=false)
    public void simulated$saveConnected(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        KineticBlockEntityMixin kineticBlockEntityMixin = this;
        if (kineticBlockEntityMixin instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)((Object)kineticBlockEntityMixin)).getExtraKinetics()) != null) {
            CompoundTag internalTag = new CompoundTag();
            if (clientPacket) {
                extraKinetics.writeClient(internalTag, registries);
            } else {
                extraKinetics.saveAdditional(internalTag, registries);
            }
            compound.put(ek.getExtraKineticsSaveName(), (Tag)internalTag);
        }
        if (this.hasSource()) {
            compound.putBoolean("ConnectedToExtraKinetics", this.simulated$extraKineticsConnected);
        }
    }

    @Inject(method={"read"}, at={@At(value="TAIL")}, remap=false)
    public void simulated$readConnected(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        KineticBlockEntityMixin kineticBlockEntityMixin = this;
        if (kineticBlockEntityMixin instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)((Object)kineticBlockEntityMixin)).getExtraKinetics()) != null) {
            CompoundTag extraKineticsTag = compound.getCompound(ek.getExtraKineticsSaveName());
            if (clientPacket) {
                extraKinetics.readClient(extraKineticsTag, registries);
            } else {
                extraKinetics.loadCustomOnly(extraKineticsTag, registries);
            }
        }
        if (compound.contains("ConnectedToExtraKinetics")) {
            this.simulated$extraKineticsConnected = compound.getBoolean("ConnectedToExtraKinetics");
        }
    }

    @Override
    public void simulated$setValidationCountdown(int validationCountdown) {
        this.validationCountdown = validationCountdown;
    }
}
