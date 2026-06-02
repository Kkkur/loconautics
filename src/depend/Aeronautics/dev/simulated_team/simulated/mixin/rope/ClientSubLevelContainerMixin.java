/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer
 *  dev.ryanhcode.sable.network.client.ClientSableInterpolationState
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.rope;

import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.network.client.ClientSableInterpolationState;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientLevelRopeManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientSubLevelContainer.class})
public abstract class ClientSubLevelContainerMixin {
    @Shadow
    @Final
    private ClientSableInterpolationState interpolation;

    @Shadow
    public abstract ClientLevel getLevel();

    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void sable$tickRopeInterpolation(CallbackInfo ci) {
        ClientLevel level = this.getLevel();
        ClientLevelRopeManager ropeManager = ClientLevelRopeManager.getOrCreate((Level)level);
        ropeManager.tickInterpolation(this.interpolation.getTickPointer());
    }
}
