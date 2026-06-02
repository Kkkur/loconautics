/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.tuple.MutablePair
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionHandler;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.MutablePair;

public class ElevatorControlsHandler {
    private static ContraptionControlsBlockEntity.ControlsSlot slot = new ElevatorControlsSlot();

    @OnlyIn(value=Dist.CLIENT)
    public static boolean onScroll(double delta) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return false;
        }
        if (player.isSpectator()) {
            return false;
        }
        if (mc.level == null) {
            return false;
        }
        Couple<Vec3> rayInputs = ContraptionHandlerClient.getRayInputs(player);
        Vec3 origin = (Vec3)rayInputs.getFirst();
        Vec3 target = (Vec3)rayInputs.getSecond();
        AABB aabb = new AABB(origin, target).inflate(16.0);
        Collection contraptions = ((Map)ContraptionHandler.loadedContraptions.get((LevelAccessor)mc.level)).values();
        for (WeakReference ref : contraptions) {
            BlockHitResult rayTraceResult;
            Contraption contraption;
            AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity)((Object)ref.get());
            if (contraptionEntity == null || !((contraption = contraptionEntity.getContraption()) instanceof ElevatorContraption)) continue;
            ElevatorContraption ec = (ElevatorContraption)contraption;
            if (!contraptionEntity.getBoundingBox().intersects(aabb) || (rayTraceResult = ContraptionHandlerClient.rayTraceContraption(origin, target, contraptionEntity)) == null) continue;
            BlockPos pos = rayTraceResult.getBlockPos();
            StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(pos);
            if (info == null || !AllBlocks.CONTRAPTION_CONTROLS.has(info.state()) || !slot.testHit((LevelAccessor)mc.level, pos, info.state(), rayTraceResult.getLocation().subtract(Vec3.atLowerCornerOf((Vec3i)pos)))) continue;
            MovementContext ctx = null;
            for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair : contraption.getActors()) {
                if (!info.equals(pair.left)) continue;
                ctx = (MovementContext)pair.right;
                break;
            }
            if (!(ctx.temporaryData instanceof ContraptionControlsMovement.ElevatorFloorSelection)) {
                ctx.temporaryData = new ContraptionControlsMovement.ElevatorFloorSelection();
            }
            ContraptionControlsMovement.ElevatorFloorSelection efs = (ContraptionControlsMovement.ElevatorFloorSelection)ctx.temporaryData;
            int prev = efs.currentIndex;
            efs.currentIndex = efs.currentIndex + (int)(delta > 0.0 ? Math.ceil(delta) : Math.floor(delta));
            ContraptionControlsMovement.tickFloorSelection(efs, ec);
            if (prev != efs.currentIndex && !ec.namesList.isEmpty()) {
                float pitch = (float)efs.currentIndex / (float)ec.namesList.size();
                pitch = Mth.lerp((float)pitch, (float)1.0f, (float)1.5f);
                AllSoundEvents.SCROLL_VALUE.play(mc.player.level(), (Player)mc.player, (Vec3i)BlockPos.containing((Position)contraptionEntity.toGlobalVector(rayTraceResult.getLocation(), 1.0f)), 1.0f, pitch);
            }
            return true;
        }
        return false;
    }

    private static class ElevatorControlsSlot
    extends ContraptionControlsBlockEntity.ControlsSlot {
        private ElevatorControlsSlot() {
        }

        @Override
        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)this.scale * 0.85;
        }
    }
}
