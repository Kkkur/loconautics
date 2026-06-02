/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency
 *  com.simibubi.create.foundation.item.TooltipHelper
 *  com.simibubi.create.foundation.utility.CreateLang
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.simulated_team.simulated.content.blocks.redstone.AbstractLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItem;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.mixin.accessor.RedstoneLinkBlockEntityAccessor;
import dev.simulated_team.simulated.network.packets.linked_typewriter.TypewriterSaveKeyToItemPacket;
import dev.simulated_team.simulated.util.SimColors;
import foundry.veil.api.network.VeilPacketManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LinkedTypewriterItemBindHandler {
    public static final LayeredDraw.Layer OVERLAY = LinkedTypewriterItemBindHandler::renderOverlay;
    private static BlockPos clickedPos;
    private static final List<AABB> outlines;
    private static boolean firstTick;

    public static void setClickedPos(BlockPos pos) {
        clickedPos = pos;
        if (pos != null) {
            firstTick = true;
        } else {
            LinkedTypewriterItemBindHandler.reset();
        }
    }

    public static void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offhandItem = player.getOffhandItem();
        if (Minecraft.getInstance().screen != null || !(mainHandItem.getItem() instanceof LinkedTypewriterItem) && !(offhandItem.getItem() instanceof LinkedTypewriterItem)) {
            LinkedTypewriterItemBindHandler.reset();
            return;
        }
        if (firstTick || level.getGameTime() % 5L == 0L) {
            firstTick = false;
            outlines.clear();
            Couple<RedstoneLinkNetworkHandler.Frequency> frequencies = LinkedTypewriterItemBindHandler.isPosValid((Level)level);
            if (frequencies == null) {
                LinkedTypewriterItemBindHandler.reset();
            } else {
                BlockState state = level.getBlockState(clickedPos);
                VoxelShape collisionShape = state.getShape((BlockGetter)level, clickedPos);
                if (!collisionShape.isEmpty()) {
                    outlines.addAll(collisionShape.toAabbs());
                }
            }
        }
        if (clickedPos != null) {
            for (AABB outline : outlines) {
                Outliner.getInstance().showAABB((Object)("linked_typewriter_outliner" + String.valueOf(clickedPos) + String.valueOf(outline)), outline.move(clickedPos)).colored(SimColors.GROSS_BINDING_BROWN).lineWidth(0.0625f);
            }
        }
    }

    public static void keyPress(int key, int scanCode, int action, int modifiers) {
        InteractionHand hand;
        ClientLevel level = Minecraft.getInstance().level;
        Couple<RedstoneLinkNetworkHandler.Frequency> frequency = LinkedTypewriterItemBindHandler.isPosValid((Level)level);
        if (frequency == null) {
            LinkedTypewriterItemBindHandler.reset();
            return;
        }
        if (key != 256 && (hand = LinkedTypewriterItemBindHandler.getHand()) != null) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterSaveKeyToItemPacket(hand, new LinkedTypewriterEntries.KeyboardEntry((RedstoneLinkNetworkHandler.Frequency)frequency.getFirst(), (RedstoneLinkNetworkHandler.Frequency)frequency.getSecond(), key, BlockPos.ZERO))});
            LinkedTypewriterInteractionHandler.preventPress(key, scanCode);
            SimLang.builder().translate("linked_typewriter.bind_success", new Object[]{InputConstants.getKey((int)key, (int)scanCode).getDisplayName().getString()}).sendStatus((Player)Minecraft.getInstance().player);
        }
        LinkedTypewriterItemBindHandler.reset();
    }

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (LinkedTypewriterInteractionHandler.getMode() != LinkedTypewriterInteractionHandler.Mode.BINDING_FROM_ITEM) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }
        guiGraphics.pose().pushPose();
        ArrayList<MutableComponent> list = new ArrayList<MutableComponent>();
        list.add(CreateLang.translateDirect((String)"linked_controller.bind_mode", (Object[])new Object[0]).withStyle(ChatFormatting.GOLD));
        MutableComponent component = SimLang.translate("linked_typewriter.bind_item", new Object[0]).component();
        list.addAll(TooltipHelper.cutTextComponent((Component)component, (FontHelper.Palette)FontHelper.Palette.ALL_GRAY));
        int width = 0;
        int n = list.size();
        Objects.requireNonNull(mc.font);
        int height = n * 9;
        for (Component component2 : list) {
            width = Math.max(width, mc.font.width((FormattedText)component2));
        }
        int x = guiGraphics.guiWidth() / 3 - width / 2;
        int n2 = guiGraphics.guiHeight() - height - 24;
        guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, list, x, n2);
        guiGraphics.pose().popPose();
    }

    private static Couple<RedstoneLinkNetworkHandler.Frequency> isPosValid(Level level) {
        Couple frequency = null;
        if (clickedPos != null) {
            BlockEntity be = level.getBlockEntity(clickedPos);
            if (be instanceof AbstractLinkedReceiverBlockEntity) {
                AbstractLinkedReceiverBlockEntity abe = (AbstractLinkedReceiverBlockEntity)be;
                frequency = abe.getFrequency();
            }
            if (be instanceof RedstoneLinkBlockEntity) {
                RedstoneLinkBlockEntity lbe = (RedstoneLinkBlockEntity)be;
                frequency = ((RedstoneLinkBlockEntityAccessor)lbe).getLink().getNetworkKey();
            }
        }
        return frequency;
    }

    private static InteractionHand getHand() {
        LocalPlayer player = Minecraft.getInstance().player;
        Item item = SimBlocks.LINKED_TYPEWRITER.asItem();
        if (player.getMainHandItem().is(item)) {
            return InteractionHand.MAIN_HAND;
        }
        if (player.getOffhandItem().is(item)) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    public static void reset() {
        outlines.clear();
        LinkedTypewriterInteractionHandler.setMode(LinkedTypewriterInteractionHandler.Mode.IDLE);
        clickedPos = null;
    }

    static {
        outlines = new ArrayList<AABB>();
        firstTick = false;
    }
}
