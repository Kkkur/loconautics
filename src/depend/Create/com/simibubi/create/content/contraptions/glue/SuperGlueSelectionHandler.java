/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.glue;

import com.google.common.base.Objects;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.glue.SuperGlueRemovalPacket;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHelper;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionPacket;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.RaycastHelper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SuperGlueSelectionHandler {
    private static final int PASSIVE = 5083490;
    private static final int HIGHLIGHT = 6866310;
    private static final int FAIL = 12957000;
    private Object clusterOutlineSlot = new Object();
    private Object bbOutlineSlot = new Object();
    private int clusterCooldown;
    private BlockPos firstPos;
    private BlockPos hoveredPos;
    private Set<BlockPos> currentCluster;
    private int glueRequired;
    private SuperGlueEntity selected;
    private BlockPos soundSourceForRemoval;

    public void tick() {
        HitResult hitResult;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        BlockPos hovered = null;
        ItemStack stack = player.getMainHandItem();
        if (!this.isGlue(stack)) {
            if (this.firstPos != null) {
                this.discard();
            }
            return;
        }
        if (this.clusterCooldown > 0) {
            if (this.clusterCooldown == 25) {
                player.displayClientMessage(CommonComponents.EMPTY, true);
            }
            Outliner.getInstance().keep(this.clusterOutlineSlot);
            --this.clusterCooldown;
        }
        AABB scanArea = player.getBoundingBox().inflate(32.0, 16.0, 32.0);
        List glueNearby = mc.level.getEntitiesOfClass(SuperGlueEntity.class, scanArea);
        this.selected = null;
        if (this.firstPos == null) {
            double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
            Vec3 traceOrigin = player.getEyePosition();
            Vec3 traceTarget = RaycastHelper.getTraceTarget((Player)player, range, traceOrigin);
            double bestDistance = Double.MAX_VALUE;
            for (SuperGlueEntity glueEntity : glueNearby) {
                Vec3 vec3;
                double distanceToSqr;
                Optional clip = glueEntity.getBoundingBox().clip(traceOrigin, traceTarget);
                if (clip.isEmpty() || (distanceToSqr = (vec3 = (Vec3)clip.get()).distanceToSqr(traceOrigin)) > bestDistance) continue;
                this.selected = glueEntity;
                this.soundSourceForRemoval = BlockPos.containing((Position)vec3);
                bestDistance = distanceToSqr;
            }
            for (SuperGlueEntity glueEntity : glueNearby) {
                boolean h = this.clusterCooldown == 0 && glueEntity == this.selected;
                AllSpecialTextures faceTex = h ? AllSpecialTextures.GLUE : null;
                Outliner.getInstance().showAABB((Object)glueEntity, glueEntity.getBoundingBox()).colored(h ? 6866310 : 5083490).withFaceTextures((BindableTexture)faceTex, (BindableTexture)faceTex).disableLineNormals().lineWidth(h ? 0.0625f : 0.015625f);
            }
        }
        if ((hitResult = mc.hitResult) != null && hitResult.getType() == HitResult.Type.BLOCK) {
            hovered = ((BlockHitResult)hitResult).getBlockPos();
        }
        if (hovered == null) {
            this.hoveredPos = null;
            return;
        }
        if (this.firstPos != null && !this.firstPos.closerThan((Vec3i)hovered, 24.0)) {
            CreateLang.translate("super_glue.too_far", new Object[0]).color(12957000).sendStatus((Player)player);
            return;
        }
        boolean cancel = player.isShiftKeyDown();
        if (cancel && this.firstPos == null) {
            return;
        }
        AABB currentSelectionBox = this.getCurrentSelectionBox();
        boolean unchanged = Objects.equal((Object)hovered, (Object)this.hoveredPos);
        if (unchanged) {
            if (this.currentCluster != null) {
                boolean canReach = this.currentCluster.contains(hovered);
                boolean canAfford = SuperGlueSelectionHelper.collectGlueFromInventory((Player)player, this.glueRequired, true);
                int color = 6866310;
                String key = "super_glue.click_to_confirm";
                if (!canReach) {
                    color = 12957000;
                    key = "super_glue.cannot_reach";
                } else if (!canAfford) {
                    color = 12957000;
                    key = "super_glue.not_enough";
                } else if (cancel) {
                    color = 12957000;
                    key = "super_glue.click_to_discard";
                }
                CreateLang.translate(key, new Object[0]).color(color).sendStatus((Player)player);
                if (currentSelectionBox != null) {
                    Outliner.getInstance().showAABB(this.bbOutlineSlot, currentSelectionBox).colored(canReach && canAfford && !cancel ? 6866310 : 12957000).withFaceTextures((BindableTexture)AllSpecialTextures.GLUE, (BindableTexture)AllSpecialTextures.GLUE).disableLineNormals().lineWidth(0.0625f);
                }
                Outliner.getInstance().showCluster(this.clusterOutlineSlot, this.currentCluster).colored(5083490).disableLineNormals().lineWidth(0.015625f);
            }
            return;
        }
        this.hoveredPos = hovered;
        Set<BlockPos> cluster = SuperGlueSelectionHelper.searchGlueGroup((Level)mc.level, this.firstPos, this.hoveredPos, true);
        this.currentCluster = cluster;
        this.glueRequired = 1;
    }

    private boolean isGlue(ItemStack stack) {
        return stack.getItem() instanceof SuperGlueItem;
    }

    private AABB getCurrentSelectionBox() {
        return this.firstPos == null || this.hoveredPos == null ? null : new AABB(Vec3.atLowerCornerOf((Vec3i)this.firstPos), Vec3.atLowerCornerOf((Vec3i)this.hoveredPos)).expandTowards(1.0, 1.0, 1.0);
    }

    public boolean onMouseInput(boolean attack) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (!this.isGlue(player.getMainHandItem())) {
            return false;
        }
        if (!player.mayBuild()) {
            return false;
        }
        if (attack) {
            if (this.selected == null) {
                return false;
            }
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new SuperGlueRemovalPacket(this.selected.getId(), this.soundSourceForRemoval));
            this.selected = null;
            this.clusterCooldown = 0;
            return true;
        }
        if (player.isShiftKeyDown()) {
            if (this.firstPos != null) {
                this.discard();
                return true;
            }
            return false;
        }
        if (this.hoveredPos == null) {
            return false;
        }
        Direction face = null;
        HitResult hitResult = mc.hitResult;
        if (hitResult instanceof BlockHitResult) {
            AbstractChassisBlock cb;
            BlockHitResult bhr = (BlockHitResult)hitResult;
            face = bhr.getDirection();
            BlockState blockState = level.getBlockState(this.hoveredPos);
            Block block = blockState.getBlock();
            if (block instanceof AbstractChassisBlock && (cb = (AbstractChassisBlock)block).getGlueableSide(blockState, bhr.getDirection()) != null) {
                return false;
            }
        }
        if (this.firstPos != null && this.currentCluster != null) {
            boolean canReach = this.currentCluster.contains(this.hoveredPos);
            boolean canAfford = SuperGlueSelectionHelper.collectGlueFromInventory((Player)player, this.glueRequired, true);
            if (!canReach || !canAfford) {
                return true;
            }
            this.confirm();
            return true;
        }
        this.firstPos = this.hoveredPos;
        if (face != null) {
            SuperGlueItem.spawnParticles((Level)level, this.firstPos, face, true);
        }
        CreateLang.translate("super_glue.first_pos", new Object[0]).sendStatus((Player)player);
        AllSoundEvents.SLIME_ADDED.playAt((Level)level, (Vec3i)this.firstPos, 0.5f, 0.85f, false);
        level.playSound((Player)player, this.firstPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
        return true;
    }

    public void discard() {
        LocalPlayer player = Minecraft.getInstance().player;
        this.currentCluster = null;
        this.firstPos = null;
        CreateLang.translate("super_glue.abort", new Object[0]).sendStatus((Player)player);
        this.clusterCooldown = 0;
    }

    public void confirm() {
        LocalPlayer player = Minecraft.getInstance().player;
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new SuperGlueSelectionPacket(this.firstPos, this.hoveredPos));
        AllSoundEvents.SLIME_ADDED.playAt(player.level(), (Vec3i)this.hoveredPos, 0.5f, 0.95f, false);
        player.level().playSound((Player)player, this.hoveredPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
        if (this.currentCluster != null) {
            Outliner.getInstance().showCluster(this.clusterOutlineSlot, this.currentCluster).colored(11924166).withFaceTextures((BindableTexture)AllSpecialTextures.GLUE, (BindableTexture)AllSpecialTextures.HIGHLIGHT_CHECKERED).disableLineNormals().lineWidth(0.041666668f);
        }
        this.discard();
        CreateLang.translate("super_glue.success", new Object[0]).sendStatus((Player)player);
        this.clusterCooldown = 40;
    }
}
