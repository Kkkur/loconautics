/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.bearing.BearingContraption
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.eriksonn.aeronautics.mixin.propeller_bearing;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.BearingContraptionExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BearingContraption.class})
public class BearingContraptionMixin
implements BearingContraptionExtension {
    @Shadow(remap=false)
    protected int sailBlocks;
    @Shadow
    private boolean isWindmill;
    @Unique
    protected float aeronautics$tempSailStrength;
    @Unique
    private boolean aeronautics$isPropeller = false;

    @Inject(method={"assemble"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/bearing/BearingContraption;expandBoundsAroundAxis(Lnet/minecraft/core/Direction$Axis;)V", shift=At.Shift.AFTER)}, remap=false)
    private void aeronautics$addSailsWithTempSails(Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) throws AssemblyException {
        this.aeronautics$tryCustomFailAssembly();
        this.aeronautics$tryFailAssembly();
        this.sailBlocks += (int)this.aeronautics$tempSailStrength;
    }

    @Override
    public void aeronautics$setPropeller() {
        this.aeronautics$isPropeller = true;
    }

    @Unique
    private void aeronautics$tryFailAssembly() throws AssemblyException {
        if (this.aeronautics$isPropeller && (float)this.sailBlocks + this.aeronautics$tempSailStrength < 2.0f) {
            throw new AssemblyException("not_enough_sails", new Object[]{Float.valueOf((float)this.sailBlocks + this.aeronautics$tempSailStrength), 2});
        }
    }

    @Unique
    private void aeronautics$tryCustomFailAssembly() throws AssemblyException {
        if (this.isWindmill && (float)this.sailBlocks + this.aeronautics$tempSailStrength < (float)((Integer)AllConfigs.server().kinetics.minimumWindmillSails.get()).intValue()) {
            throw new AssemblyException("not_enough_sails", new Object[]{Float.valueOf((float)this.sailBlocks + this.aeronautics$tempSailStrength), AllConfigs.server().kinetics.minimumWindmillSails.get()});
        }
    }
}
