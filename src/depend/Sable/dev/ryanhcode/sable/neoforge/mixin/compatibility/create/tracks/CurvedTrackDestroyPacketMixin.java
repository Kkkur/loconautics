/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.trains.track.CurvedTrackDestroyPacket
 *  com.simibubi.create.content.trains.track.TrackBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.tracks;

import com.simibubi.create.content.trains.track.CurvedTrackDestroyPacket;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={CurvedTrackDestroyPacket.class})
public class CurvedTrackDestroyPacketMixin {
    @Redirect(method={"applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/trains/track/TrackBlockEntity;)V"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/trains/track/TrackBlockEntity;getBlockPos()Lnet/minecraft/core/BlockPos;"))
    protected BlockPos sable$getWorldBlockPos(TrackBlockEntity instance) {
        return BlockPos.containing((Position)Sable.HELPER.projectOutOfSubLevel(instance.getLevel(), instance.getBlockPos().getCenter()));
    }
}
