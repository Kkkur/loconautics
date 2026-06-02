/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtAccounter
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.simibubi.create.content.schematics;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicInstances;
import com.simibubi.create.content.schematics.SchematicProcessor;
import com.simibubi.create.content.schematics.client.SchematicEditScreen;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.CreatePaths;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.GZIPInputStream;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class SchematicItem
extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();

    public SchematicItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack create(Level level, String schematic, String owner) {
        ItemStack blueprint = AllItems.SCHEMATIC.asStack();
        blueprint.set(AllDataComponents.SCHEMATIC_DEPLOYED, (Object)false);
        blueprint.set(AllDataComponents.SCHEMATIC_OWNER, (Object)owner);
        blueprint.set(AllDataComponents.SCHEMATIC_FILE, (Object)schematic);
        blueprint.set(AllDataComponents.SCHEMATIC_ANCHOR, (Object)BlockPos.ZERO);
        blueprint.set(AllDataComponents.SCHEMATIC_ROTATION, (Object)Rotation.NONE);
        blueprint.set(AllDataComponents.SCHEMATIC_MIRROR, (Object)Mirror.NONE);
        SchematicItem.writeSize(level, blueprint);
        return blueprint;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.has(AllDataComponents.SCHEMATIC_FILE)) {
            tooltip.add((Component)Component.literal((String)(String.valueOf(ChatFormatting.GOLD) + (String)stack.get(AllDataComponents.SCHEMATIC_FILE))));
        } else {
            tooltip.add((Component)CreateLang.translateDirect("schematic.invalid", new Object[0]).withStyle(ChatFormatting.RED));
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    public static void writeSize(Level level, ItemStack blueprint) {
        StructureTemplate t = SchematicItem.loadSchematic(level, blueprint);
        blueprint.set(AllDataComponents.SCHEMATIC_BOUNDS, (Object)t.getSize());
        SchematicInstances.clearHash(blueprint);
    }

    public static StructurePlaceSettings getSettings(ItemStack blueprint) {
        return SchematicItem.getSettings(blueprint, true);
    }

    public static StructurePlaceSettings getSettings(ItemStack blueprint, boolean processNBT) {
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setRotation((Rotation)blueprint.getOrDefault(AllDataComponents.SCHEMATIC_ROTATION, (Object)Rotation.NONE));
        settings.setMirror((Mirror)blueprint.getOrDefault(AllDataComponents.SCHEMATIC_MIRROR, (Object)Mirror.NONE));
        if (processNBT) {
            settings.addProcessor((StructureProcessor)SchematicProcessor.INSTANCE);
        }
        return settings;
    }

    public static StructureTemplate loadSchematic(Level level, ItemStack blueprint) {
        Path file;
        Path dir;
        StructureTemplate t = new StructureTemplate();
        String owner = (String)blueprint.get(AllDataComponents.SCHEMATIC_OWNER);
        String schematic = (String)blueprint.get(AllDataComponents.SCHEMATIC_FILE);
        if (owner == null || schematic == null || !schematic.endsWith(".nbt")) {
            return t;
        }
        if (!level.isClientSide()) {
            dir = CreatePaths.UPLOADED_SCHEMATICS_DIR;
            file = Paths.get(owner, schematic);
        } else {
            dir = CreatePaths.SCHEMATICS_DIR;
            file = Paths.get(schematic, new String[0]);
        }
        Path path = dir.resolve(file).normalize();
        if (!path.startsWith(dir)) {
            return t;
        }
        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ))));){
            CompoundTag nbt = NbtIo.read((DataInput)stream, (NbtAccounter)NbtAccounter.create((long)0x20000000L));
            t.load((HolderGetter)level.holderLookup(Registries.BLOCK), nbt);
        }
        catch (IOException e) {
            LOGGER.warn("Failed to read schematic", (Throwable)e);
        }
        return t;
    }

    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && !this.onItemUse(context.getPlayer(), context.getHand())) {
            return super.useOn(context);
        }
        return InteractionResult.SUCCESS;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!this.onItemUse(playerIn, handIn)) {
            return super.use(worldIn, playerIn, handIn);
        }
        return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)playerIn.getItemInHand(handIn));
    }

    private boolean onItemUse(Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown() || hand != InteractionHand.MAIN_HAND) {
            return false;
        }
        if (!player.getItemInHand(hand).has(AllDataComponents.SCHEMATIC_FILE)) {
            return false;
        }
        if (!player.level().isClientSide()) {
            return true;
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::displayBlueprintScreen);
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void displayBlueprintScreen() {
        ScreenOpener.open((Screen)new SchematicEditScreen());
    }
}
