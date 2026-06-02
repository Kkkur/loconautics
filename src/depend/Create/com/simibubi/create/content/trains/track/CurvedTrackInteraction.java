/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.event.InputEvent$InteractionKeyMappingTriggered
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.CurvedTrackDestroyPacket;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.InputEvent;

public class CurvedTrackInteraction {
    static final int breakerId = new Object().hashCode();
    static int breakTicks;
    static int breakTimeout;
    static float breakProgress;
    static BlockPos breakPos;

    public static void clientTick() {
        TrackBlockOutline.BezierPointSelection result = TrackBlockOutline.result;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (!player.getAbilities().mayBuild) {
            return;
        }
        if (mc.options.keyAttack.isDown() && result != null) {
            breakPos = result.blockEntity().getBlockPos();
            BlockState blockState = level.getBlockState(breakPos);
            if (blockState.isAir()) {
                CurvedTrackInteraction.resetBreakProgress();
                return;
            }
            if ((float)breakTicks % 4.0f == 0.0f) {
                SoundType soundtype = blockState.getSoundType((LevelReader)level, breakPos, (Entity)player);
                mc.getSoundManager().play((SoundInstance)new SimpleSoundInstance(soundtype.getHitSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0f) / 8.0f, soundtype.getPitch() * 0.5f, level.random, BlockPos.containing((Position)result.vec())));
            }
            boolean creative = player.getAbilities().instabuild;
            ++breakTicks;
            breakTimeout = 2;
            float f = creative ? 0.125f : blockState.getDestroyProgress((Player)player, (BlockGetter)level, breakPos) / 8.0f;
            Vec3 vec = VecHelper.offsetRandomly((Vec3)result.vec(), (RandomSource)level.random, (float)0.25f);
            level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockState), vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
            int progress = (int)((breakProgress += f) * 10.0f) - 1;
            level.destroyBlockProgress(player.getId(), breakPos, progress);
            player.swing(InteractionHand.MAIN_HAND);
            if (breakProgress >= 1.0f) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new CurvedTrackDestroyPacket(breakPos, result.loc().curveTarget(), BlockPos.containing((Position)result.vec()), false));
                CurvedTrackInteraction.resetBreakProgress();
            }
            return;
        }
        if (breakTimeout == 0) {
            return;
        }
        if (--breakTimeout > 0) {
            return;
        }
        CurvedTrackInteraction.resetBreakProgress();
    }

    private static void resetBreakProgress() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (breakPos != null && level != null) {
            level.destroyBlockProgress(mc.player.getId(), breakPos, -1);
        }
        breakProgress = 0.0f;
        breakTicks = 0;
        breakPos = null;
    }

    public static boolean onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        TrackBlockOutline.BezierPointSelection result = TrackBlockOutline.result;
        if (result == null) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) {
            return false;
        }
        if (event.isUseItem()) {
            TrackTargetingBlockItem ttbi;
            ItemStack heldItem = player.getMainHandItem();
            Item item = heldItem.getItem();
            if (AllTags.AllBlockTags.TRACKS.matches(heldItem)) {
                player.displayClientMessage((Component)CreateLang.translateDirect("track.turn_start", new Object[0]).withStyle(ChatFormatting.RED), true);
                player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
            if (item instanceof TrackTargetingBlockItem && (ttbi = (TrackTargetingBlockItem)item).useOnCurve(result, heldItem)) {
                player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
            if (AllItems.WRENCH.isIn(heldItem) && player.isShiftKeyDown()) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new CurvedTrackDestroyPacket(result.blockEntity().getBlockPos(), result.loc().curveTarget(), BlockPos.containing((Position)result.vec()), true));
                CurvedTrackInteraction.resetBreakProgress();
                player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
        }
        return event.isAttack();
    }
}
