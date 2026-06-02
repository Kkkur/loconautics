/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.util.TriState
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.redstone.displayLink;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public abstract class ClickToLinkBlockItem
extends BlockItem {
    private static BlockPos lastShownPos = null;
    private static AABB lastShownAABB = null;

    public ClickToLinkBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    @SubscribeEvent
    public static void linkableItemAlwaysPlacesWhenUsed(PlayerInteractEvent.RightClickBlock event) {
        ItemStack usedItem = event.getItemStack();
        Item item = usedItem.getItem();
        if (!(item instanceof ClickToLinkBlockItem)) {
            return;
        }
        ClickToLinkBlockItem blockItem = (ClickToLinkBlockItem)item;
        if (event.getLevel().getBlockState(event.getPos()).is(blockItem.getBlock())) {
            return;
        }
        event.setUseBlock(TriState.FALSE);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        String msgKey = this.getMessageTranslationKey();
        int maxDistance = this.getMaxDistanceFromSelection();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown() && stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.displayClientMessage((Component)CreateLang.translateDirect(msgKey + ".clear", new Object[0]), true);
            stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            return InteractionResult.SUCCESS;
        }
        ResourceLocation placedDim = level.dimension().location();
        if (!stack.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
            if (!this.isValidTarget((LevelAccessor)level, pos)) {
                if (this.placeWhenInvalid()) {
                    InteractionResult useOn = super.useOn(pContext);
                    if (level.isClientSide || useOn == InteractionResult.FAIL) {
                        return useOn;
                    }
                    ItemStack itemInHand = player.getItemInHand(pContext.getHand());
                    if (!itemInHand.isEmpty()) {
                        stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
                        stack.remove(DataComponents.BLOCK_ENTITY_DATA);
                    }
                    return useOn;
                }
                if (level.isClientSide) {
                    AllSoundEvents.DENY.playFrom((Entity)player);
                }
                player.displayClientMessage((Component)CreateLang.translateDirect(msgKey + ".invalid", new Object[0]), true);
                return InteractionResult.FAIL;
            }
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.displayClientMessage((Component)CreateLang.translateDirect(msgKey + ".set", new Object[0]), true);
            stack.set(AllDataComponents.CLICK_TO_LINK_DATA, (Object)new ClickToLinkData(pos, placedDim));
            return InteractionResult.SUCCESS;
        }
        ClickToLinkData data = (ClickToLinkData)stack.get(AllDataComponents.CLICK_TO_LINK_DATA);
        BlockPos selectedPos = data.selectedPos();
        ResourceLocation selectedDim = data.selectedDim();
        BlockPos placedPos = pos.relative(pContext.getClickedFace(), state.canBeReplaced() ? 0 : 1);
        if (!(maxDistance == -1 || selectedPos.closerThan((Vec3i)placedPos, (double)maxDistance) && selectedDim.equals((Object)placedDim))) {
            player.displayClientMessage((Component)CreateLang.translateDirect(msgKey + ".too_far", new Object[0]).withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        CompoundTag beTag = new CompoundTag();
        beTag.put("TargetOffset", NbtUtils.writeBlockPos((BlockPos)selectedPos.subtract((Vec3i)placedPos)));
        NBTHelper.writeResourceLocation((CompoundTag)beTag, (String)"TargetDimension", (ResourceLocation)selectedDim);
        BlockEntity.addEntityType((CompoundTag)beTag, ((IBE)this.getBlock()).getBlockEntityType());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.of((CompoundTag)beTag));
        InteractionResult useOn = super.useOn(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL) {
            return useOn;
        }
        ItemStack itemInHand = player.getItemInHand(pContext.getHand());
        if (!itemInHand.isEmpty()) {
            stack.remove(AllDataComponents.CLICK_TO_LINK_DATA);
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        }
        player.displayClientMessage((Component)CreateLang.translateDirect(msgKey + ".success", new Object[0]).withStyle(ChatFormatting.GREEN), true);
        return useOn;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void clientTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack heldItemMainhand = player.getMainHandItem();
        Item item = heldItemMainhand.getItem();
        if (!(item instanceof ClickToLinkBlockItem)) {
            return;
        }
        ClickToLinkBlockItem blockItem = (ClickToLinkBlockItem)item;
        if (!heldItemMainhand.has(AllDataComponents.CLICK_TO_LINK_DATA)) {
            return;
        }
        BlockPos selectedPos = ((ClickToLinkData)heldItemMainhand.get(AllDataComponents.CLICK_TO_LINK_DATA)).selectedPos();
        if (!selectedPos.equals((Object)lastShownPos)) {
            lastShownAABB = blockItem.getSelectionBounds(selectedPos);
            lastShownPos = selectedPos;
        }
        Outliner.getInstance().showAABB((Object)"target", lastShownAABB).colored(16763764).lineWidth(0.0625f);
    }

    public abstract int getMaxDistanceFromSelection();

    public abstract String getMessageTranslationKey();

    public boolean placeWhenInvalid() {
        return false;
    }

    public boolean isValidTarget(LevelAccessor level, BlockPos pos) {
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    public AABB getSelectionBounds(BlockPos pos) {
        ClientLevel world = Minecraft.getInstance().level;
        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getShape((BlockGetter)world, pos);
        return shape.isEmpty() ? new AABB(BlockPos.ZERO) : shape.bounds().move(pos);
    }

    public record ClickToLinkData(BlockPos selectedPos, ResourceLocation selectedDim) {
        public static final Codec<ClickToLinkData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.CODEC.fieldOf("selected_pos").forGetter(ClickToLinkData::selectedPos), (App)ResourceLocation.CODEC.fieldOf("selected_dim").forGetter(ClickToLinkData::selectedDim)).apply((Applicative)instance, ClickToLinkData::new));
        public static final StreamCodec<ByteBuf, ClickToLinkData> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, ClickToLinkData::selectedPos, (StreamCodec)ResourceLocation.STREAM_CODEC, ClickToLinkData::selectedDim, ClickToLinkData::new);
    }
}
