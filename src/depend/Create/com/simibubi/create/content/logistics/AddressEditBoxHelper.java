/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics;

import com.google.common.cache.Cache;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.trains.schedule.DestinationSuggestions;
import com.simibubi.create.foundation.utility.TickBasedCache;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AddressEditBoxHelper {
    private static final Cache<BlockPos, WeakReference<ClipboardBlockEntity>> NEARBY_CLIPBOARDS = new TickBasedCache<BlockPos, WeakReference<ClipboardBlockEntity>>(20, false);

    public static void advertiseClipboard(ClipboardBlockEntity blockEntity) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        BlockPos blockPos = blockEntity.getBlockPos();
        if (player.distanceToSqr(Vec3.atCenterOf((Vec3i)blockPos)) > 1024.0) {
            return;
        }
        NEARBY_CLIPBOARDS.put((Object)blockPos, new WeakReference<ClipboardBlockEntity>(blockEntity));
    }

    public static DestinationSuggestions createSuggestions(Screen screen, EditBox pInput, boolean anchorToBottom, String localAddress) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ArrayList<IntAttached<String>> options = new ArrayList<IntAttached<String>>();
        HashSet<String> alreadyAdded = new HashSet<String>();
        DestinationSuggestions destinationSuggestions = new DestinationSuggestions(mc, screen, pInput, mc.font, options, anchorToBottom, -72 + pInput.getY() + (anchorToBottom ? 0 : pInput.getHeight()));
        if (player == null) {
            return destinationSuggestions;
        }
        if (localAddress != null) {
            options.add((IntAttached<String>)IntAttached.with((int)-1, (Object)localAddress));
            alreadyAdded.add(localAddress);
        }
        for (int i = 0; i < 36; ++i) {
            AddressEditBoxHelper.appendAddresses(options, alreadyAdded, player.getInventory().getItem(i));
        }
        for (WeakReference wr : NEARBY_CLIPBOARDS.asMap().values()) {
            ClipboardBlockEntity cbe = (ClipboardBlockEntity)wr.get();
            if (cbe == null) continue;
            AddressEditBoxHelper.appendAddresses(options, alreadyAdded, cbe.components());
        }
        return destinationSuggestions;
    }

    private static void appendAddresses(List<IntAttached<String>> options, Set<String> alreadyAdded, ItemStack item) {
        if (item == null || !AllBlocks.CLIPBOARD.isIn(item)) {
            return;
        }
        AddressEditBoxHelper.appendAddresses(options, alreadyAdded, item.getComponents());
    }

    private static void appendAddresses(List<IntAttached<String>> options, Set<String> alreadyAdded, DataComponentMap components) {
        List<List<ClipboardEntry>> pages = ClipboardEntry.readAll(components);
        pages.forEach(page -> page.forEach(entry -> {
            String string = entry.text.getString();
            if (entry.checked) {
                return;
            }
            if (!string.startsWith("#") || string.length() == 1) {
                return;
            }
            String address = string.substring(1);
            if (address.isBlank()) {
                return;
            }
            String trim = address.trim();
            if (!alreadyAdded.add(trim)) {
                return;
            }
            options.add(IntAttached.withZero((Object)trim));
        }));
    }
}
