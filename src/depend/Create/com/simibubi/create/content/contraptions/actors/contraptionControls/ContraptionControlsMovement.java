/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.actors.contraptionControls;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsRenderer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ContraptionControlsMovement
implements MovementBehaviour {
    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public void startMoving(MovementContext context) {
        if (context.contraption instanceof ElevatorContraption && context.blockEntityData != null) {
            context.blockEntityData.remove("Filter");
        }
    }

    @Override
    public void stopMoving(MovementContext context) {
        ItemStack filter = ContraptionControlsMovement.getFilter(context);
        if (filter != null) {
            context.blockEntityData.putBoolean("Disabled", context.contraption.isActorTypeDisabled(filter) || context.contraption.isActorTypeDisabled(ItemStack.EMPTY));
        }
    }

    public static boolean isSameFilter(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() && stack2.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents((ItemStack)stack1, (ItemStack)stack2);
    }

    public static ItemStack getFilter(MovementContext ctx) {
        CompoundTag blockEntityData = ctx.blockEntityData;
        if (blockEntityData == null) {
            return null;
        }
        return ItemStack.parseOptional((HolderLookup.Provider)ctx.world.registryAccess(), (CompoundTag)blockEntityData.getCompound("Filter"));
    }

    public static boolean isDisabledInitially(MovementContext ctx) {
        return ctx.blockEntityData != null && ctx.blockEntityData.getBoolean("Disabled");
    }

    @Override
    public void tick(MovementContext ctx) {
        boolean below;
        if (!ctx.world.isClientSide()) {
            return;
        }
        Contraption contraption = ctx.contraption;
        BlockEntity blockEntity = contraption.getBlockEntityClientSide(ctx.localPos);
        if (!(contraption instanceof ElevatorContraption)) {
            if (!(blockEntity instanceof ContraptionControlsBlockEntity)) {
                return;
            }
            ContraptionControlsBlockEntity cbe = (ContraptionControlsBlockEntity)blockEntity;
            ItemStack filter = ContraptionControlsMovement.getFilter(ctx);
            int value = contraption.isActorTypeDisabled(filter) || contraption.isActorTypeDisabled(ItemStack.EMPTY) ? 180 : 0;
            cbe.indicator.setValue((double)value);
            cbe.indicator.updateChaseTarget((float)value);
            cbe.tickAnimations();
            return;
        }
        ElevatorContraption ec = (ElevatorContraption)contraption;
        if (!(ctx.temporaryData instanceof ElevatorFloorSelection)) {
            ctx.temporaryData = new ElevatorFloorSelection();
        }
        ElevatorFloorSelection efs = (ElevatorFloorSelection)ctx.temporaryData;
        ContraptionControlsMovement.tickFloorSelection(efs, ec);
        if (!(blockEntity instanceof ContraptionControlsBlockEntity)) {
            return;
        }
        ContraptionControlsBlockEntity cbe = (ContraptionControlsBlockEntity)blockEntity;
        cbe.tickAnimations();
        int currentY = (int)Math.round(contraption.entity.getY() + (double)ec.getContactYOffset());
        boolean atTargetY = ec.clientYTarget == currentY;
        LerpedFloat indicator = cbe.indicator;
        float currentIndicator = indicator.getChaseTarget();
        boolean bl = atTargetY ? currentIndicator > 0.0f : (below = ec.clientYTarget <= currentY);
        if (currentIndicator == 0.0f && !atTargetY) {
            int startingPoint = below ? 181 : -181;
            indicator.setValue((double)startingPoint);
            indicator.updateChaseTarget((float)startingPoint);
            cbe.tickAnimations();
            return;
        }
        int currentStage = Mth.floor((float)((currentIndicator % 360.0f + 360.0f) % 360.0f));
        if (!atTargetY || currentStage / 45 != 0) {
            float increment = currentStage / 45 == (below ? 4 : 3) ? 2.25f : 33.75f;
            indicator.chase((double)(currentIndicator + (below ? increment : -increment)), 45.0, LerpedFloat.Chaser.LINEAR);
            return;
        }
        indicator.setValue(0.0);
        indicator.updateChaseTarget(0.0f);
    }

    public static void tickFloorSelection(ElevatorFloorSelection efs, ElevatorContraption ec) {
        if (ec.namesList.isEmpty()) {
            efs.currentShortName = "X";
            efs.currentLongName = "No Floors";
            efs.currentIndex = 0;
            efs.targetYEqualsSelection = true;
            return;
        }
        efs.currentIndex = Mth.clamp((int)efs.currentIndex, (int)0, (int)(ec.namesList.size() - 1));
        IntAttached<Couple<String>> entry = ec.namesList.get(efs.currentIndex);
        efs.currentTargetY = (Integer)entry.getFirst();
        efs.currentShortName = (String)((Couple)entry.getSecond()).getFirst();
        efs.currentLongName = (String)((Couple)entry.getSecond()).getSecond();
        boolean bl = efs.targetYEqualsSelection = efs.currentTargetY == ec.clientYTarget;
        if (ec.isTargetUnreachable(efs.currentTargetY)) {
            efs.currentLongName = CreateLang.translate("contraption.controls.floor_unreachable", new Object[0]).string();
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void renderInContraption(MovementContext ctx, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        ContraptionControlsRenderer.renderInContraption(ctx, renderWorld, matrices, buffer);
    }

    public static class ElevatorFloorSelection {
        public int currentIndex = 0;
        public int currentTargetY = 0;
        public boolean targetYEqualsSelection = true;
        public String currentShortName = "";
        public String currentLongName = "";
    }
}
