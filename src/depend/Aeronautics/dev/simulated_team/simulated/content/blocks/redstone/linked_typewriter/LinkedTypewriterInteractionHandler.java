/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllDataComponents
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency
 *  com.simibubi.create.foundation.utility.ControlsUtil
 *  foundry.veil.api.network.VeilPacketManager
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.utility.ControlsUtil;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItemBindHandler;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterRenderer;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.mixin.accessor.KeyMappingsAccessor;
import dev.simulated_team.simulated.network.packets.linked_typewriter.TypewriterDisconnectUser;
import dev.simulated_team.simulated.network.packets.linked_typewriter.TypewriterKeyInteractionPacket;
import dev.simulated_team.simulated.network.packets.linked_typewriter.TypewriterKeySavePacket;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Vector;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LinkedTypewriterInteractionHandler {
    private static final Vector<Integer> renderPressed = new Vector();
    private static final Int2IntMap presetKeys = new Int2IntOpenHashMap();
    private static WeakReference<LinkedTypewriterBlockEntity> TYPEWRITER = new WeakReference<Object>(null);
    private static Mode MODE = Mode.IDLE;

    public static void associateTypewriter(LinkedTypewriterBlockEntity be) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (be == null) {
            MODE = Mode.IDLE;
            LinkedTypewriterInteractionHandler.stopInteraction();
            if (TYPEWRITER.get() != null) {
                player.displayClientMessage((Component)SimLang.translate("linked_typewriter.stop_controlling", new Object[0]).component(), true);
            }
        } else {
            MODE = Mode.ACTIVE;
            player.displayClientMessage((Component)SimLang.translate("linked_typewriter.start_controlling", new Object[0]).component(), true);
        }
        TYPEWRITER = new WeakReference<LinkedTypewriterBlockEntity>(be);
    }

    public static void tick() {
        LinkedTypewriterBlockEntity be;
        LinkedTypewriterRenderer.tick();
        if (LinkedTypewriterInteractionHandler.getMode() == Mode.BINDING_FROM_ITEM) {
            LinkedTypewriterItemBindHandler.tick();
        }
        if ((be = (LinkedTypewriterBlockEntity)((Object)TYPEWRITER.get())) == null) {
            return;
        }
        if (LinkedTypewriterInteractionHandler.getMode() == Mode.ACTIVE && !LinkedTypewriterBlockEntity.playerInRange((Player)Minecraft.getInstance().player, be.getLevel(), be.getBlockPos())) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterDisconnectUser(be.getBlockPos())});
            LinkedTypewriterInteractionHandler.associateTypewriter(null);
        }
        if (LinkedTypewriterInteractionHandler.getMode() != Mode.SCREEN_BINDING && Minecraft.getInstance().screen != null && !be.isRemoved()) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterDisconnectUser(be.getBlockPos())});
            LinkedTypewriterInteractionHandler.associateTypewriter(null);
        }
    }

    private static void stopInteraction() {
        LinkedTypewriterRenderer.resetKeys();
        renderPressed.clear();
    }

    public static void onKeyPress(int key, int scanCode, int action, int modifiers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        LinkedTypewriterBlockEntity be = (LinkedTypewriterBlockEntity)((Object)TYPEWRITER.get());
        if (LinkedTypewriterInteractionHandler.getMode() == Mode.BINDING_FROM_ITEM) {
            LinkedTypewriterItemBindHandler.keyPress(key, scanCode, action, modifiers);
            return;
        }
        if (LinkedTypewriterInteractionHandler.getMode() != Mode.SCREEN_BINDING) {
            if (be != null && !be.isRemoved()) {
                LinkedTypewriterEntries.KeyboardEntry frequency = be.getTypewriterEntries().getEntry(key);
                if (key == 256) {
                    be.disconnectUser();
                    VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterDisconnectUser(be.getBlockPos())});
                    minecraft.setScreen(null);
                }
                if (frequency != null) {
                    LinkedTypewriterInteractionHandler.preventPress(key, scanCode);
                    if (action != 2) {
                        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterKeyInteractionPacket(be.getBlockPos(), key, scanCode, action)});
                    }
                    LocalPlayer player = minecraft.player;
                    if (action == 1) {
                        SimSoundEvents.LINKED_TYPEWRITER_TAP.playAt(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f, true);
                        LinkedTypewriterInteractionHandler.checkKeyCodeAndSetPressed(key, true);
                    } else if (action == 0) {
                        SimSoundEvents.LINKED_TYPEWRITER_UNTAP.playAt(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f, true);
                        LinkedTypewriterInteractionHandler.checkKeyCodeAndSetPressed(key, false);
                    }
                }
                for (KeyMapping control : ControlsUtil.getControls()) {
                    if (!control.matches(key, scanCode)) continue;
                    control.consumeClick();
                    control.setDown(false);
                    break;
                }
            } else {
                MODE = Mode.IDLE;
                LinkedTypewriterInteractionHandler.stopInteraction();
            }
        }
    }

    public static void preventPress(int key, int scanCode) {
        for (KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
            if (!mapping.matches(key, scanCode)) continue;
            mapping.consumeClick();
            mapping.setDown(false);
            break;
        }
    }

    private static void checkKeyCodeAndSetPressed(int keycode, boolean pressed) {
        int indexPressed;
        if (presetKeys.containsKey(keycode)) {
            indexPressed = presetKeys.get(keycode);
        } else {
            RandomSource random = RandomSource.create((long)keycode);
            indexPressed = random.nextInt(13);
        }
        if (pressed) {
            renderPressed.addElement(indexPressed);
        } else {
            renderPressed.removeElement(indexPressed);
        }
    }

    public static Mode getMode() {
        return MODE;
    }

    public static void setMode(Mode newMode) {
        MODE = newMode;
    }

    public static Vector<Integer> getPressedKeys() {
        return renderPressed;
    }

    public static void sendLinkedControllerData(Level level, BlockPos blockPos, ItemStack item) {
        ObjectArrayList linkedControllerItems;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof LinkedTypewriterBlockEntity)) {
            return;
        }
        ItemContainerContents linkedControllerData = (ItemContainerContents)item.get(AllDataComponents.LINKED_CONTROLLER_ITEMS);
        if (linkedControllerData == null) {
            int size = 12;
            ObjectArrayList emptyData = new ObjectArrayList(12);
            for (int i = 0; i < 12; ++i) {
                emptyData.add((Object)ItemStack.EMPTY);
            }
            linkedControllerItems = emptyData;
        } else {
            linkedControllerItems = new ObjectArrayList(linkedControllerData.stream().toList());
            while (linkedControllerItems.size() < 12) {
                linkedControllerItems.add(ItemStack.EMPTY);
            }
        }
        Int2ObjectOpenHashMap newKeyBindings = new Int2ObjectOpenHashMap();
        int controlIndex = 0;
        for (KeyMapping mapping : ControlsUtil.getControls()) {
            int control = ((KeyMappingsAccessor)mapping).getKey().getValue();
            ItemStack first = (ItemStack)linkedControllerItems.get(2 * controlIndex);
            ItemStack second = (ItemStack)linkedControllerItems.get(2 * controlIndex + 1);
            newKeyBindings.put(control, (Object)new LinkedTypewriterEntries.KeyboardEntry(RedstoneLinkNetworkHandler.Frequency.of((ItemStack)first), RedstoneLinkNetworkHandler.Frequency.of((ItemStack)second), control, blockPos));
            ++controlIndex;
        }
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterKeySavePacket((Map<Integer, LinkedTypewriterEntries.KeyboardEntry>)newKeyBindings, blockPos, false)});
    }

    static {
        presetKeys.put(81, 0);
        presetKeys.put(87, 1);
        presetKeys.put(69, 2);
        presetKeys.put(65, 6);
        presetKeys.put(83, 7);
        presetKeys.put(68, 8);
        presetKeys.put(265, 4);
        presetKeys.put(263, 10);
        presetKeys.put(264, 11);
        presetKeys.put(262, 12);
        presetKeys.put(32, 13);
        presetKeys.put(48, 12);
        presetKeys.put(320, 12);
        for (int i = 0; i < 9; ++i) {
            presetKeys.put(49 + i, i);
            presetKeys.put(321 + i, i);
        }
    }

    public static enum Mode {
        IDLE,
        ACTIVE,
        BIND,
        SCREEN_BINDING,
        BINDING_FROM_ITEM;

    }
}
