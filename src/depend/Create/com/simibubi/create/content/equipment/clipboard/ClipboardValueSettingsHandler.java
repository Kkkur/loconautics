/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.ICancellableEvent
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.RenderHighlightEvent$Block
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickItem
 */
package com.simibubi.create.content.equipment.clipboard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ClipboardValueSettingsHandler {
    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void drawCustomBlockSelection(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();
        BlockState blockstate = mc.level.getBlockState(pos);
        if (mc.player == null || mc.player.isSpectator()) {
            return;
        }
        if (!mc.level.getWorldBorder().isWithinBounds(pos)) {
            return;
        }
        if (!AllBlocks.CLIPBOARD.isIn(mc.player.getMainHandItem())) {
            return;
        }
        BlockEntity blockEntity = mc.level.getBlockEntity(pos);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity smartBE = (SmartBlockEntity)blockEntity;
        if (!(smartBE instanceof ClipboardBlockEntity) && smartBE.getAllBehaviours().stream().noneMatch(b -> {
            ClipboardCloneable cc;
            return b instanceof ClipboardCloneable && (cc = (ClipboardCloneable)((Object)b)).writeToClipboard((HolderLookup.Provider)mc.level.registryAccess(), new CompoundTag(), target.getDirection());
        }) && !(smartBE instanceof ClipboardCloneable)) {
            return;
        }
        VoxelShape shape = blockstate.getShape((BlockGetter)mc.level, pos);
        if (shape.isEmpty()) {
            return;
        }
        VertexConsumer vb = event.getMultiBufferSource().getBuffer(RenderType.lines());
        Vec3 camPos = event.getCamera().getPosition();
        PoseStack ms = event.getPoseStack();
        ms.pushPose();
        ms.translate((double)pos.getX() - camPos.x, (double)pos.getY() - camPos.y, (double)pos.getZ() - camPos.z);
        TrackBlockOutline.renderShape(shape, ms, vb, true);
        event.setCanceled(true);
        ms.popPose();
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void clientTick() {
        ClipboardCloneable ccbe;
        boolean canPaste;
        ClipboardCloneable ccbe2;
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult target = (BlockHitResult)hitResult;
        if (!AllBlocks.CLIPBOARD.isIn(mc.player.getMainHandItem())) {
            return;
        }
        BlockPos pos = target.getBlockPos();
        BlockEntity blockEntity = mc.level.getBlockEntity(pos);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity smartBE = (SmartBlockEntity)blockEntity;
        if (smartBE instanceof ClipboardBlockEntity) {
            ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
            tip.add(CreateLang.translateDirect("clipboard.actions", new Object[0]));
            tip.add(CreateLang.translateDirect("clipboard.copy_other_clipboard", Component.keybind((String)"key.use")));
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
            return;
        }
        ClipboardContent content = (ClipboardContent)mc.player.getMainHandItem().get(AllDataComponents.CLIPBOARD_CONTENT);
        if (content == null) {
            return;
        }
        CompoundTag tagElement = content.copiedValues().orElse(null);
        boolean canCopy = smartBE.getAllBehaviours().stream().anyMatch(b -> {
            ClipboardCloneable cc;
            return b instanceof ClipboardCloneable && (cc = (ClipboardCloneable)((Object)b)).writeToClipboard((HolderLookup.Provider)mc.level.registryAccess(), new CompoundTag(), target.getDirection());
        }) || smartBE instanceof ClipboardCloneable && (ccbe2 = (ClipboardCloneable)((Object)smartBE)).writeToClipboard((HolderLookup.Provider)mc.level.registryAccess(), new CompoundTag(), target.getDirection());
        boolean bl = canPaste = tagElement != null && (smartBE.getAllBehaviours().stream().anyMatch(b -> {
            ClipboardCloneable cc;
            return b instanceof ClipboardCloneable && (cc = (ClipboardCloneable)((Object)b)).readFromClipboard((HolderLookup.Provider)mc.level.registryAccess(), tagElement.getCompound(cc.getClipboardKey()), (Player)mc.player, target.getDirection(), true);
        }) || smartBE instanceof ClipboardCloneable && (ccbe = (ClipboardCloneable)((Object)smartBE)).readFromClipboard((HolderLookup.Provider)mc.level.registryAccess(), tagElement.getCompound(ccbe.getClipboardKey()), (Player)mc.player, target.getDirection(), true));
        if (!canCopy && !canPaste) {
            return;
        }
        ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
        tip.add(CreateLang.translateDirect("clipboard.actions", new Object[0]));
        if (canCopy) {
            tip.add(CreateLang.translateDirect("clipboard.to_copy", Component.keybind((String)"key.use")));
        }
        if (canPaste) {
            tip.add(CreateLang.translateDirect("clipboard.to_paste", Component.keybind((String)"key.attack")));
        }
        CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
    }

    @SubscribeEvent
    public static void rightClickToCopy(PlayerInteractEvent.RightClickBlock event) {
        ClipboardValueSettingsHandler.interact((PlayerInteractEvent)event, false);
    }

    @SubscribeEvent
    public static void leftClickToPaste(PlayerInteractEvent.LeftClickBlock event) {
        ClipboardValueSettingsHandler.interact((PlayerInteractEvent)event, true);
    }

    private static void interact(PlayerInteractEvent event, boolean paste) {
        ItemStack itemStack = event.getItemStack();
        if (!AllBlocks.CLIPBOARD.isIn(itemStack)) {
            return;
        }
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
        Player player = event.getEntity();
        if (player != null && player.isSpectator()) {
            return;
        }
        if (player.isShiftKeyDown()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity smartBE = (SmartBlockEntity)blockEntity;
        ClipboardContent clipboardContent = (ClipboardContent)itemStack.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, (Object)ClipboardContent.EMPTY);
        if (smartBE instanceof ClipboardBlockEntity) {
            ClipboardBlockEntity cbe = (ClipboardBlockEntity)smartBE;
            if (event instanceof ICancellableEvent) {
                ICancellableEvent cancellableEvent = (ICancellableEvent)event;
                cancellableEvent.setCanceled(true);
                PlayerInteractEvent playerInteractEvent = event;
                Objects.requireNonNull(playerInteractEvent);
                PlayerInteractEvent playerInteractEvent2 = playerInteractEvent;
                int n = 0;
                switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PlayerInteractEvent.EntityInteractSpecific.class, PlayerInteractEvent.EntityInteract.class, PlayerInteractEvent.RightClickBlock.class, PlayerInteractEvent.RightClickItem.class}, (Object)playerInteractEvent2, n)) {
                    case 0: {
                        PlayerInteractEvent.EntityInteractSpecific e = (PlayerInteractEvent.EntityInteractSpecific)playerInteractEvent2;
                        e.setCancellationResult(InteractionResult.SUCCESS);
                        break;
                    }
                    case 1: {
                        PlayerInteractEvent.EntityInteract entityInteract = (PlayerInteractEvent.EntityInteract)playerInteractEvent2;
                        entityInteract.setCancellationResult(InteractionResult.SUCCESS);
                        break;
                    }
                    case 2: {
                        PlayerInteractEvent.RightClickBlock e = (PlayerInteractEvent.RightClickBlock)playerInteractEvent2;
                        e.setCancellationResult(InteractionResult.SUCCESS);
                        break;
                    }
                    case 3: {
                        PlayerInteractEvent.RightClickItem e = (PlayerInteractEvent.RightClickItem)playerInteractEvent2;
                        e.setCancellationResult(InteractionResult.SUCCESS);
                        break;
                    }
                }
            }
            if (!world.isClientSide()) {
                List<List<ClipboardEntry>> listTo = ClipboardEntry.readAll(clipboardContent);
                List<List<ClipboardEntry>> listFrom = ClipboardEntry.readAll(cbe.components());
                ArrayList<ClipboardEntry> toAdd = new ArrayList<ClipboardEntry>();
                for (List list : listFrom) {
                    block7: for (Object entry : list) {
                        String string = ((ClipboardEntry)entry).text.getString();
                        for (List<ClipboardEntry> pageTo : listTo) {
                            for (ClipboardEntry existing : pageTo) {
                                if (!string.equals(existing.text.getString())) continue;
                                continue block7;
                            }
                        }
                        toAdd.add(new ClipboardEntry(((ClipboardEntry)entry).checked, ((ClipboardEntry)entry).text));
                    }
                }
                for (ClipboardEntry clipboardEntry : toAdd) {
                    List<ClipboardEntry> page = null;
                    for (List list : listTo) {
                        if (list.size() > 11) continue;
                        page = list;
                        break;
                    }
                    if (page == null) {
                        page = new ArrayList<ClipboardEntry>();
                        listTo.add(page);
                    }
                    page.add(clipboardEntry);
                    clipboardContent = clipboardContent.setType(ClipboardOverrides.ClipboardType.WRITTEN);
                    itemStack.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)clipboardContent);
                }
                clipboardContent = clipboardContent.setPages(listTo);
                itemStack.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)clipboardContent);
            }
            player.displayClientMessage((Component)CreateLang.translate("clipboard.copied_from_clipboard", world.getBlockState(pos).getBlock().getName().withStyle(ChatFormatting.WHITE)).style(ChatFormatting.GREEN).component(), true);
            return;
        }
        CompoundTag tag = clipboardContent.copiedValues().orElse(null);
        if (paste && tag == null) {
            return;
        }
        if (!paste) {
            tag = new CompoundTag();
        }
        boolean anySuccess = false;
        boolean anyValid = false;
        for (BlockEntityBehaviour behaviour : smartBE.getAllBehaviours()) {
            if (!(behaviour instanceof ClipboardCloneable)) continue;
            ClipboardCloneable clipboardCloneable = (ClipboardCloneable)((Object)behaviour);
            anyValid = true;
            String clipboardKey = clipboardCloneable.getClipboardKey();
            if (paste) {
                anySuccess |= clipboardCloneable.readFromClipboard((HolderLookup.Provider)world.registryAccess(), tag.getCompound(clipboardKey), player, event.getFace(), world.isClientSide());
                continue;
            }
            CompoundTag compoundTag = new CompoundTag();
            boolean bl = clipboardCloneable.writeToClipboard((HolderLookup.Provider)world.registryAccess(), compoundTag, event.getFace());
            anySuccess |= bl;
            if (!bl) continue;
            tag.put(clipboardKey, (Tag)compoundTag);
        }
        if (smartBE instanceof ClipboardCloneable) {
            ClipboardCloneable ccbe = (ClipboardCloneable)((Object)smartBE);
            anyValid = true;
            String clipboardKey = ccbe.getClipboardKey();
            if (paste) {
                anySuccess |= ccbe.readFromClipboard((HolderLookup.Provider)world.registryAccess(), tag.getCompound(clipboardKey), player, event.getFace(), world.isClientSide());
            } else {
                CompoundTag compoundTag = new CompoundTag();
                boolean success = ccbe.writeToClipboard((HolderLookup.Provider)world.registryAccess(), compoundTag, event.getFace());
                anySuccess |= success;
                if (success) {
                    tag.put(clipboardKey, (Tag)compoundTag);
                }
            }
        }
        if (!anyValid) {
            return;
        }
        ((ICancellableEvent)event).setCanceled(true);
        if (event instanceof PlayerInteractEvent.RightClickBlock) {
            PlayerInteractEvent.RightClickBlock rightClickBlock = (PlayerInteractEvent.RightClickBlock)event;
            rightClickBlock.setCancellationResult(InteractionResult.SUCCESS);
        }
        if (world.isClientSide()) {
            return;
        }
        if (!anySuccess) {
            return;
        }
        player.displayClientMessage((Component)CreateLang.translate(paste ? "clipboard.pasted_to" : "clipboard.copied_from", world.getBlockState(pos).getBlock().getName().withStyle(ChatFormatting.WHITE)).style(ChatFormatting.GREEN).component(), true);
        if (!paste) {
            clipboardContent = clipboardContent.setType(ClipboardOverrides.ClipboardType.WRITTEN);
            clipboardContent = clipboardContent.setCopiedValues(tag);
            itemStack.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)clipboardContent);
        }
    }
}
