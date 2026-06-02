/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity$ConnectionStats
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage
 *  net.minecraft.core.BlockPos
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.chain_conveyor;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChainConveyorBlockEntity.class})
public abstract class ChainConveyorBlockEntityMixin {
    @Shadow
    public Map<BlockPos, ChainConveyorBlockEntity.ConnectionStats> connectionStats;
    @Shadow
    Map<BlockPos, List<ChainConveyorPackage>> travellingPackages;

    @Shadow
    protected abstract void drop(ChainConveyorPackage var1);

    @Inject(method={"removeInvalidConnections"}, at={@At(value="INVOKE", target="Ljava/util/Iterator;remove()V")})
    public void dropInvalidPackages(CallbackInfo ci, @Local(name={"next"}) BlockPos next) {
        this.connectionStats.remove(next);
        List<ChainConveyorPackage> packages = this.travellingPackages.remove(next);
        if (packages != null && !packages.isEmpty()) {
            for (ChainConveyorPackage box : packages) {
                this.drop(box);
            }
        }
    }
}
