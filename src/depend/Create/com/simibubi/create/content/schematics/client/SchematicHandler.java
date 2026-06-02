/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.createmod.catnip.outliner.AABBOutline
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.SchematicInstances;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.client.SchematicHotbarSlotOverlay;
import com.simibubi.create.content.schematics.client.SchematicRenderer;
import com.simibubi.create.content.schematics.client.SchematicTransformation;
import com.simibubi.create.content.schematics.client.ToolSelectionScreen;
import com.simibubi.create.content.schematics.client.tools.ToolType;
import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import com.simibubi.create.content.schematics.packet.SchematicSyncPacket;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.outliner.AABBOutline;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SchematicHandler
implements LayeredDraw.Layer {
    private String displayedSchematic;
    private SchematicTransformation transformation;
    private AABB bounds;
    private boolean deployed;
    private boolean active;
    private ToolType currentTool;
    private static final int SYNC_DELAY = 10;
    private int syncCooldown;
    private int activeHotbarSlot;
    private ItemStack activeSchematicItem;
    private AABBOutline outline;
    private final SchematicRenderer[] renderers = new SchematicRenderer[3];
    private final SchematicHotbarSlotOverlay overlay = new SchematicHotbarSlotOverlay();
    private ToolSelectionScreen selectionScreen;

    public SchematicHandler() {
        this.currentTool = ToolType.DEPLOY;
        this.selectionScreen = new ToolSelectionScreen((List<ToolType>)ImmutableList.of((Object)((Object)ToolType.DEPLOY)), this::equip);
        this.transformation = new SchematicTransformation();
    }

    public void tick() {
        LocalPlayer player;
        ItemStack stack;
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            if (this.active) {
                this.active = false;
                this.syncCooldown = 0;
                this.activeHotbarSlot = 0;
                this.activeSchematicItem = null;
            }
            return;
        }
        if (this.activeSchematicItem != null && this.transformation != null) {
            this.transformation.tick();
        }
        if ((stack = this.findBlueprintInHand((Player)(player = mc.player))) == null) {
            this.active = false;
            this.syncCooldown = 0;
            if (this.activeSchematicItem != null && this.itemLost((Player)player)) {
                this.activeHotbarSlot = 0;
                this.activeSchematicItem = null;
            }
            return;
        }
        if (!this.active || !((String)stack.get(AllDataComponents.SCHEMATIC_FILE)).equals(this.displayedSchematic)) {
            this.init(player, stack);
        }
        if (!this.active) {
            return;
        }
        if (this.syncCooldown > 0) {
            --this.syncCooldown;
        }
        if (this.syncCooldown == 1) {
            this.sync();
        }
        this.selectionScreen.update();
        this.currentTool.getTool().updateSelection();
    }

    private void init(LocalPlayer player, ItemStack stack) {
        this.loadSettings(stack);
        this.displayedSchematic = (String)stack.get(AllDataComponents.SCHEMATIC_FILE);
        this.active = true;
        if (this.deployed) {
            this.setupRenderer();
            ToolType toolBefore = this.currentTool;
            this.selectionScreen = new ToolSelectionScreen(ToolType.getTools(player.isCreative()), this::equip);
            if (toolBefore != null) {
                this.selectionScreen.setSelectedElement(toolBefore);
                this.equip(toolBefore);
            }
        } else {
            this.selectionScreen = new ToolSelectionScreen((List<ToolType>)ImmutableList.of((Object)((Object)ToolType.DEPLOY)), this::equip);
        }
    }

    private void setupRenderer() {
        ClientLevel clientWorld = Minecraft.getInstance().level;
        StructureTemplate schematic = SchematicItem.loadSchematic((Level)clientWorld, this.activeSchematicItem);
        Vec3i size = schematic.getSize();
        if (size.equals((Object)Vec3i.ZERO)) {
            return;
        }
        SchematicLevel w = new SchematicLevel((Level)clientWorld);
        SchematicLevel wMirroredFB = new SchematicLevel((Level)clientWorld);
        SchematicLevel wMirroredLR = new SchematicLevel((Level)clientWorld);
        StructurePlaceSettings placementSettings = new StructurePlaceSettings();
        BlockPos pos = BlockPos.ZERO;
        try {
            schematic.placeInWorld((ServerLevelAccessor)w, pos, pos, placementSettings, w.getRandom(), 2);
            for (BlockEntity blockEntity : w.getBlockEntities()) {
                blockEntity.setLevel((Level)w);
            }
            this.fixControllerBlockEntities(w);
        }
        catch (Exception e) {
            Minecraft.getInstance().player.displayClientMessage((Component)CreateLang.translate("schematic.error", new Object[0]).component(), false);
            Create.LOGGER.error("Failed to load Schematic for Previewing", (Throwable)e);
            return;
        }
        placementSettings.setMirror(Mirror.FRONT_BACK);
        pos = BlockPos.ZERO.east(size.getX() - 1);
        schematic.placeInWorld((ServerLevelAccessor)wMirroredFB, pos, pos, placementSettings, wMirroredFB.getRandom(), 2);
        StructureTransform transform = new StructureTransform(placementSettings.getRotationPivot(), Direction.Axis.Y, Rotation.NONE, placementSettings.getMirror());
        for (BlockEntity be : wMirroredFB.getRenderedBlockEntities()) {
            transform.apply(be);
        }
        this.fixControllerBlockEntities(wMirroredFB);
        placementSettings.setMirror(Mirror.LEFT_RIGHT);
        pos = BlockPos.ZERO.south(size.getZ() - 1);
        schematic.placeInWorld((ServerLevelAccessor)wMirroredLR, pos, pos, placementSettings, wMirroredFB.getRandom(), 2);
        transform = new StructureTransform(placementSettings.getRotationPivot(), Direction.Axis.Y, Rotation.NONE, placementSettings.getMirror());
        for (BlockEntity be : wMirroredLR.getRenderedBlockEntities()) {
            transform.apply(be);
        }
        this.fixControllerBlockEntities(wMirroredLR);
        this.renderers[0] = new SchematicRenderer(w);
        this.renderers[1] = new SchematicRenderer(wMirroredFB);
        this.renderers[2] = new SchematicRenderer(wMirroredLR);
    }

    private void fixControllerBlockEntities(SchematicLevel level) {
        for (BlockEntity blockEntity : level.getBlockEntities()) {
            if (!(blockEntity instanceof IMultiBlockEntityContainer)) continue;
            IMultiBlockEntityContainer multiBlockEntity = (IMultiBlockEntityContainer)blockEntity;
            BlockPos lastKnown = multiBlockEntity.getLastKnownPos();
            BlockPos current = blockEntity.getBlockPos();
            if (lastKnown == null || current == null || multiBlockEntity.isController() || lastKnown.equals((Object)current)) continue;
            BlockPos newControllerPos = multiBlockEntity.getController().offset((Vec3i)current.subtract((Vec3i)lastKnown));
            if (multiBlockEntity instanceof SmartBlockEntity) {
                SmartBlockEntity sbe = (SmartBlockEntity)((Object)multiBlockEntity);
                sbe.markVirtual();
            }
            multiBlockEntity.setController(newControllerPos);
        }
    }

    public void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        boolean present;
        if (!this.active) {
            return;
        }
        boolean bl = present = this.activeSchematicItem != null;
        if (!present) {
            return;
        }
        ms.pushPose();
        this.currentTool.getTool().renderTool(ms, buffer, camera);
        ms.popPose();
        ms.pushPose();
        this.transformation.applyTransformations(ms, camera);
        if (this.deployed) {
            boolean fb;
            float pt = AnimationTickHolder.getPartialTicks();
            boolean lr = this.transformation.getScaleLR().getValue(pt) < 0.0f;
            boolean bl2 = fb = this.transformation.getScaleFB().getValue(pt) < 0.0f;
            if (lr && !fb && this.renderers[2] != null) {
                this.renderers[2].render(ms, buffer);
            } else if (fb && !lr && this.renderers[1] != null) {
                this.renderers[1].render(ms, buffer);
            } else if (this.renderers[0] != null) {
                this.renderers[0].render(ms, buffer);
            }
        }
        this.currentTool.getTool().renderOnSchematic(ms, buffer);
        ms.popPose();
    }

    public void updateRenderers() {
        for (SchematicRenderer renderer : this.renderers) {
            if (renderer == null) continue;
            renderer.update();
        }
    }

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || !this.active) {
            return;
        }
        if (this.activeSchematicItem != null) {
            this.overlay.renderOn(guiGraphics, this.activeHotbarSlot);
        }
        this.currentTool.getTool().renderOverlay(mc.gui, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        this.selectionScreen.renderPassive(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    public boolean onMouseInput(int button, boolean pressed) {
        if (!this.active) {
            return false;
        }
        if (!pressed || button != 1) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.isShiftKeyDown()) {
            return false;
        }
        HitResult hitResult = mc.hitResult;
        if (hitResult instanceof BlockHitResult) {
            BlockHitResult blockRayTraceResult = (BlockHitResult)hitResult;
            BlockState clickedBlock = mc.level.getBlockState(blockRayTraceResult.getBlockPos());
            if (AllBlocks.SCHEMATICANNON.has(clickedBlock)) {
                return false;
            }
            if (AllBlocks.DEPLOYER.has(clickedBlock)) {
                return false;
            }
        }
        return this.currentTool.getTool().handleRightClick();
    }

    public void onKeyInput(int key, boolean pressed) {
        if (!this.active) {
            return;
        }
        if (!AllKeys.TOOL_MENU.doesModifierAndCodeMatch(key)) {
            return;
        }
        if (pressed && !this.selectionScreen.focused) {
            this.selectionScreen.focused = true;
        }
        if (!pressed && this.selectionScreen.focused) {
            this.selectionScreen.focused = false;
            this.selectionScreen.onClose();
        }
    }

    public boolean mouseScrolled(double delta) {
        if (!this.active) {
            return false;
        }
        if (this.selectionScreen.focused) {
            this.selectionScreen.cycle((int)Math.signum(delta));
            return true;
        }
        if (AllKeys.ctrlDown()) {
            return this.currentTool.getTool().handleMouseWheel(delta);
        }
        return false;
    }

    private ItemStack findBlueprintInHand(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!AllItems.SCHEMATIC.isIn(stack)) {
            return null;
        }
        if (!stack.has(AllDataComponents.SCHEMATIC_FILE)) {
            return null;
        }
        this.activeSchematicItem = stack;
        this.activeHotbarSlot = player.getInventory().selected;
        return stack;
    }

    private boolean itemLost(Player player) {
        for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
            if (player.getInventory().getItem(i).is(this.activeSchematicItem.getItem()) || !ItemStack.matches((ItemStack)player.getInventory().getItem(i), (ItemStack)this.activeSchematicItem)) continue;
            return false;
        }
        return true;
    }

    public void markDirty() {
        this.syncCooldown = 10;
    }

    public void sync() {
        if (this.activeSchematicItem == null) {
            return;
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new SchematicSyncPacket(this.activeHotbarSlot, this.transformation.toSettings(), this.transformation.getAnchor(), this.deployed));
    }

    public void equip(ToolType tool) {
        this.currentTool = tool;
        this.currentTool.getTool().init();
    }

    public void loadSettings(ItemStack blueprint) {
        StructurePlaceSettings settings = SchematicItem.getSettings(blueprint);
        this.transformation = new SchematicTransformation();
        this.deployed = (Boolean)blueprint.getOrDefault(AllDataComponents.SCHEMATIC_DEPLOYED, (Object)false);
        BlockPos anchor = (BlockPos)blueprint.getOrDefault(AllDataComponents.SCHEMATIC_ANCHOR, (Object)BlockPos.ZERO);
        Vec3i size = (Vec3i)blueprint.get(AllDataComponents.SCHEMATIC_BOUNDS);
        if (size == null) {
            return;
        }
        this.bounds = new AABB(0.0, 0.0, 0.0, (double)size.getX(), (double)size.getY(), (double)size.getZ());
        this.outline = new AABBOutline(this.bounds);
        this.outline.getParams().colored(6850245).lineWidth(0.0625f);
        this.transformation.init(anchor, settings, this.bounds);
    }

    public void deploy() {
        if (!this.deployed) {
            List<ToolType> tools = ToolType.getTools(Minecraft.getInstance().player.isCreative());
            this.selectionScreen = new ToolSelectionScreen(tools, this::equip);
        }
        this.deployed = true;
        this.setupRenderer();
    }

    public String getCurrentSchematicName() {
        return this.displayedSchematic != null ? this.displayedSchematic : "-";
    }

    public void printInstantly() {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new SchematicPlacePacket(this.activeSchematicItem.copy()));
        this.activeSchematicItem.set(AllDataComponents.SCHEMATIC_DEPLOYED, (Object)false);
        SchematicInstances.clearHash(this.activeSchematicItem);
        this.active = false;
        this.markDirty();
    }

    public boolean isActive() {
        return this.active;
    }

    public AABB getBounds() {
        return this.bounds;
    }

    public SchematicTransformation getTransformation() {
        return this.transformation;
    }

    public boolean isDeployed() {
        return this.deployed;
    }

    public ItemStack getActiveSchematicItem() {
        return this.activeSchematicItem;
    }

    public AABBOutline getOutline() {
        return this.outline;
    }
}
