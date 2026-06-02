/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  foundry.veil.api.network.VeilPacketManager
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.handle;

import com.simibubi.create.AllItems;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.handle.HandleBlock;
import dev.simulated_team.simulated.content.blocks.handle.HandleBlockEntity;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.network.packets.UpdatePlayerUsingHandlePacket;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ClientHandleHandler
extends BlockHoldInteraction {
    private float desiredRange = -1.0f;
    public int actuallyUsedBlockCountdown = 0;
    public boolean movingSubLevel = false;

    @Override
    public void startHold(Level level, Player player, BlockPos blockPos) {
        InteractionHand hand = this.getHandOrNull(player);
        if (hand == null) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof HandleBlockEntity)) {
            return;
        }
        HandleBlockEntity handleBE = (HandleBlockEntity)blockEntity;
        Vector3d grabCenter = handleBE.getGrabCenter();
        Vector3d projected = Sable.HELPER.projectOutOfSubLevel(player.level(), grabCenter);
        Vec3 eyePosition = player.getEyePosition();
        this.desiredRange = (float)Math.min(projected.distance(eyePosition.x, eyePosition.y, eyePosition.z), Math.min(5.0, player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue()));
        this.movingSubLevel = player.isShiftKeyDown();
        player.swing(hand);
        super.startHold(level, player, blockPos);
        this.sendUpdate(false);
    }

    @Override
    public boolean activeTick(Level level, LocalPlayer player) {
        boolean crouchingOrFlying;
        BlockPos interactionPos = this.getInteractionPos();
        ChunkPos chunk = new ChunkPos(interactionPos);
        SubLevelContainer container = SubLevelContainer.getContainer((Level)level);
        assert (container != null);
        if (container.inBounds(chunk) && Sable.HELPER.getContaining(level, chunk) == null) {
            return true;
        }
        InteractionHand hand = this.getHandOrNull((Player)player);
        if (hand == null || player.isDeadOrDying() || player.isSpectator()) {
            return true;
        }
        BlockEntity blockEntity = level.getBlockEntity(interactionPos);
        if (!(blockEntity instanceof HandleBlockEntity)) {
            return true;
        }
        HandleBlockEntity handleBE = (HandleBlockEntity)blockEntity;
        Vector3d globalTarget = Sable.HELPER.projectOutOfSubLevel(level, handleBE.getGrabCenter());
        if (!ClientHandleHandler.inInteractionRange((Player)player, (Vector3dc)globalTarget, 4.0)) {
            return true;
        }
        if (!HandleBlock.canInteractWithHandle((Player)player)) {
            return true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.gameRenderer.getMainCamera().isDetached()) {
            player.swingTime = 0;
            player.swinging = true;
            player.swingingArm = InteractionHand.MAIN_HAND;
        }
        if (player.isFallFlying()) {
            player.stopFallFlying();
        }
        boolean bl = crouchingOrFlying = this.movingSubLevel || player.getAbilities().flying;
        if (!crouchingOrFlying) {
            if (player.input.up) {
                this.deltaRange((Player)player, -0.5f);
            }
            if (player.input.down) {
                this.deltaRange((Player)player, 0.5f);
            }
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new UpdatePlayerUsingHandlePacket(-1.0f, false, interactionPos)});
            Vec3 eyePos = player.getEyePosition();
            Vec3 goalEyePos = JOMLConversion.toMojang((Vector3dc)globalTarget).add(player.getLookAngle().scale((double)(-(this.desiredRange * 0.4f)) - Math.max(-player.getLookAngle().y, 0.0)));
            Vec3 difference = goalEyePos.subtract(eyePos);
            double differenceLength = difference.length();
            double maxLength = 2.0;
            if (differenceLength > 2.0) {
                difference = difference.scale(2.0 / differenceLength);
            }
            player.setDeltaMovement(player.getDeltaMovement().scale(0.25).add(difference.scale(0.3)));
            player.resetFallDistance();
        } else {
            this.sendUpdate(false);
        }
        return false;
    }

    @Override
    public void clientTick(Level level, LocalPlayer player) {
        if (this.actuallyUsedBlockCountdown > 0) {
            --this.actuallyUsedBlockCountdown;
        }
        if (this.isActive()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.keyUse.isDown() || minecraft.options.keyShift.isDown()) {
            return;
        }
        if (player.isUsingItem()) {
            return;
        }
        if (player.getMainHandItem().is((Holder)AllItems.WRENCH) || player.getOffhandItem().is((Holder)AllItems.WRENCH)) {
            return;
        }
        if (!HandleBlock.canInteractWithHandle((Player)player)) {
            return;
        }
        if (this.actuallyUsedBlockCountdown > 0) {
            return;
        }
        double length = player.getDeltaMovement().length();
        Vec3 moveNorm = player.getDeltaMovement().normalize();
        for (double i = -0.2; i < length; i += 0.2) {
            Vec3 castDir;
            Vec3 castOrigin = player.getEyePosition().add(moveNorm.scale(i));
            BlockHitResult clip = level.clip(new ClipContext(castOrigin, castOrigin.add(castDir = player.getLookAngle().scale(BlockHoldInteraction.getInteractionRange((Player)player))), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player));
            BlockState state = level.getBlockState(clip.getBlockPos());
            if (!state.is(SimTags.Blocks.HANDLES)) continue;
            this.startHold(level, (Player)player, clip.getBlockPos());
            return;
        }
    }

    @Override
    public InteractCallback.Result onScroll(double deltaX, double deltaY) {
        Player player = SimDistUtil.getClientPlayer();
        if (this.isActive() && player != null) {
            this.deltaRange(player, (float)deltaY);
            return new InteractCallback.Result(true);
        }
        return InteractCallback.Result.empty();
    }

    public void deltaRange(Player player, float delta) {
        this.desiredRange = (float)Math.clamp((double)(this.desiredRange + delta), 1.0, Math.min(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue(), 5.0));
    }

    @Override
    public void stop() {
        this.sendUpdate(true);
        this.desiredRange = -1.0f;
        super.stop();
    }

    @Nullable
    public InteractionHand getHandOrNull(Player player) {
        ItemStack mainItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        return this.isEmptyOrExtendoGrip(mainItem) ? InteractionHand.MAIN_HAND : (this.isEmptyOrExtendoGrip(offHandItem) ? InteractionHand.OFF_HAND : null);
    }

    public boolean isEmptyOrExtendoGrip(ItemStack stack) {
        return stack.isEmpty() || AllItems.EXTENDO_GRIP.is(stack.getItem());
    }

    private void sendUpdate(boolean remove) {
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new UpdatePlayerUsingHandlePacket(remove ? -1.0f : this.desiredRange, remove, this.getInteractionPos())});
    }
}
