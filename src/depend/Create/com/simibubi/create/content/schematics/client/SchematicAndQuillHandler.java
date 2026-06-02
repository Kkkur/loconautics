/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.SchematicExport;
import com.simibubi.create.content.schematics.client.ClientSchematicLoader;
import com.simibubi.create.content.schematics.client.SchematicPromptScreen;
import com.simibubi.create.content.schematics.packet.InstantSchematicPacket;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.CreatePaths;
import com.simibubi.create.foundation.utility.RaycastHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SchematicAndQuillHandler {
    private Object outlineSlot = new Object();
    public BlockPos firstPos;
    public BlockPos secondPos;
    private BlockPos selectedPos;
    private Direction selectedFace;
    private int range = 10;

    public boolean mouseScrolled(double delta) {
        if (!this.isActive()) {
            return false;
        }
        if (!AllKeys.ctrlDown()) {
            return false;
        }
        if (this.secondPos == null) {
            this.range = (int)Mth.clamp((double)((double)this.range + delta), (double)1.0, (double)100.0);
        }
        if (this.selectedFace == null) {
            return true;
        }
        AABB bb = new AABB(Vec3.atLowerCornerOf((Vec3i)this.firstPos), Vec3.atLowerCornerOf((Vec3i)this.secondPos));
        Vec3i vec = this.selectedFace.getNormal();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        if (bb.contains(projectedView)) {
            delta *= -1.0;
        }
        int intDelta = (int)(delta > 0.0 ? Math.ceil(delta) : Math.floor(delta));
        int x = vec.getX() * intDelta;
        int y = vec.getY() * intDelta;
        int z = vec.getZ() * intDelta;
        Direction.AxisDirection axisDirection = this.selectedFace.getAxisDirection();
        if (axisDirection == Direction.AxisDirection.NEGATIVE) {
            bb = bb.move((double)(-x), (double)(-y), (double)(-z));
        }
        double maxX = Math.max(bb.maxX - (double)(x * axisDirection.getStep()), bb.minX);
        double maxY = Math.max(bb.maxY - (double)(y * axisDirection.getStep()), bb.minY);
        double maxZ = Math.max(bb.maxZ - (double)(z * axisDirection.getStep()), bb.minZ);
        bb = new AABB(bb.minX, bb.minY, bb.minZ, maxX, maxY, maxZ);
        this.firstPos = BlockPos.containing((double)bb.minX, (double)bb.minY, (double)bb.minZ);
        this.secondPos = BlockPos.containing((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
        LocalPlayer player = Minecraft.getInstance().player;
        CreateLang.translate("schematicAndQuill.dimensions", (int)bb.getXsize() + 1, (int)bb.getYsize() + 1, (int)bb.getZsize() + 1).sendStatus((Player)player);
        return true;
    }

    public boolean onMouseInput(int button, boolean pressed) {
        if (!pressed || button != 1) {
            return false;
        }
        if (!this.isActive()) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player.isShiftKeyDown()) {
            this.discard();
            return true;
        }
        if (this.secondPos != null) {
            ScreenOpener.open((Screen)new SchematicPromptScreen());
            return true;
        }
        if (this.selectedPos == null) {
            CreateLang.translate("schematicAndQuill.noTarget", new Object[0]).sendStatus((Player)player);
            return true;
        }
        if (this.firstPos != null) {
            this.secondPos = this.selectedPos;
            CreateLang.translate("schematicAndQuill.secondPos", new Object[0]).sendStatus((Player)player);
            return true;
        }
        this.firstPos = this.selectedPos;
        CreateLang.translate("schematicAndQuill.firstPos", new Object[0]).sendStatus((Player)player);
        return true;
    }

    public void discard() {
        LocalPlayer player = Minecraft.getInstance().player;
        this.firstPos = null;
        this.secondPos = null;
        CreateLang.translate("schematicAndQuill.abort", new Object[0]).sendStatus((Player)player);
    }

    public void tick() {
        AABB currentSelectionBox;
        if (!this.isActive()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (AllKeys.ACTIVATE_TOOL.isPressed()) {
            float pt = AnimationTickHolder.getPartialTicks();
            Vec3 targetVec = player.getEyePosition(pt).add(player.getLookAngle().scale((double)this.range));
            this.selectedPos = BlockPos.containing((Position)targetVec);
        } else {
            BlockHitResult trace = RaycastHelper.rayTraceRange(player.level(), (Player)player, 75.0);
            if (trace != null && trace.getType() == HitResult.Type.BLOCK) {
                BlockPos hit = trace.getBlockPos();
                boolean replaceable = player.level().getBlockState(hit).canBeReplaced(new BlockPlaceContext(new UseOnContext((Player)player, InteractionHand.MAIN_HAND, trace)));
                if (trace.getDirection().getAxis().isVertical() && !replaceable) {
                    hit = hit.relative(trace.getDirection());
                }
                this.selectedPos = hit;
            } else {
                this.selectedPos = null;
            }
        }
        this.selectedFace = null;
        if (this.secondPos != null) {
            Vec3 projectedView;
            AABB bb = new AABB(Vec3.atLowerCornerOf((Vec3i)this.firstPos), Vec3.atLowerCornerOf((Vec3i)this.secondPos)).expandTowards(1.0, 1.0, 1.0).inflate((double)0.45f);
            boolean inside = bb.contains(projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
            RaycastHelper.PredicateTraceResult result = RaycastHelper.rayTraceUntil((Player)player, 70.0, pos -> inside ^ bb.contains(VecHelper.getCenterOf((Vec3i)pos)));
            Direction direction = result.missed() ? null : (this.selectedFace = inside ? result.getFacing().getOpposite() : result.getFacing());
        }
        if ((currentSelectionBox = this.getCurrentSelectionBox()) != null) {
            this.outliner().chaseAABB(this.outlineSlot, currentSelectionBox).colored(6850245).withFaceTextures((BindableTexture)AllSpecialTextures.CHECKERED, (BindableTexture)AllSpecialTextures.HIGHLIGHT_CHECKERED).lineWidth(0.0625f).highlightFace(this.selectedFace);
        }
    }

    private AABB getCurrentSelectionBox() {
        if (this.secondPos == null) {
            if (this.firstPos == null) {
                return this.selectedPos == null ? null : new AABB(this.selectedPos);
            }
            return this.selectedPos == null ? new AABB(this.firstPos) : new AABB(Vec3.atLowerCornerOf((Vec3i)this.firstPos), Vec3.atLowerCornerOf((Vec3i)this.selectedPos)).expandTowards(1.0, 1.0, 1.0);
        }
        return new AABB(Vec3.atLowerCornerOf((Vec3i)this.firstPos), Vec3.atLowerCornerOf((Vec3i)this.secondPos)).expandTowards(1.0, 1.0, 1.0);
    }

    private boolean isActive() {
        return this.isPresent() && AllItems.SCHEMATIC_AND_QUILL.isIn(Minecraft.getInstance().player.getMainHandItem());
    }

    private boolean isPresent() {
        return Minecraft.getInstance() != null && Minecraft.getInstance().level != null && Minecraft.getInstance().screen == null;
    }

    public void saveSchematic(String string, boolean convertImmediately) {
        SchematicExport.SchematicExportResult result = SchematicExport.saveSchematic(CreatePaths.SCHEMATICS_DIR, string, false, (Level)Minecraft.getInstance().level, this.firstPos, this.secondPos);
        LocalPlayer player = Minecraft.getInstance().player;
        if (result == null) {
            CreateLang.translate("schematicAndQuill.failed", new Object[0]).style(ChatFormatting.RED).sendStatus((Player)player);
            return;
        }
        Path file = result.file();
        CreateLang.translate("schematicAndQuill.saved", file.getFileName().toString()).sendStatus((Player)player);
        this.firstPos = null;
        this.secondPos = null;
        if (!convertImmediately) {
            return;
        }
        try {
            if (!ClientSchematicLoader.validateSizeLimitation(Files.size(file))) {
                return;
            }
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new InstantSchematicPacket(result.fileName(), result.origin(), result.bounds()));
        }
        catch (IOException e) {
            Create.LOGGER.error("Error instantly uploading Schematic file: " + String.valueOf(file), (Throwable)e);
        }
    }

    private Outliner outliner() {
        return Outliner.getInstance();
    }
}
