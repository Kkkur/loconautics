/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  foundry.veil.api.network.VeilPacketManager
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.debug;

import dev.ryanhcode.sable.SableClient;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.debug.GizmoSelection;
import dev.ryanhcode.sable.debug.SableClientGizmoHandler;
import dev.ryanhcode.sable.network.packets.tcp.ServerboundGizmoMoveSubLevelPacket;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.network.VeilPacketManager;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class GizmoScreen
extends Screen {
    private boolean dragging;
    @Nullable
    private GizmoSelection activeSelection;

    protected GizmoScreen() {
        super((Component)Component.literal((String)"Gizmo Mode"));
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    public boolean mouseClicked(double d, double e, int i) {
        SableClientGizmoHandler gizmoHandler = SableClient.GIZMO_HANDLER;
        if (gizmoHandler.getSelection() != null) {
            this.activeSelection = gizmoHandler.getSelection();
            this.dragging = true;
        }
        return super.mouseClicked(d, e, i);
    }

    public boolean mouseReleased(double d, double e, int i) {
        this.dragging = false;
        this.activeSelection = null;
        return super.mouseReleased(d, e, i);
    }

    public boolean mouseDragged(double x, double y, int i, double f, double g) {
        SableClientGizmoHandler gizmoHandler = SableClient.GIZMO_HANDLER;
        if (this.dragging) {
            Vector3d dir;
            boolean hitsPlane;
            Vector3d planeNormal;
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            ClientSubLevelContainer container = SubLevelContainer.getContainer(level);
            assert (container != null);
            UUID subLevelID = this.activeSelection.subLevel();
            SubLevel subLevel = container.getSubLevel(subLevelID);
            if (subLevel == null) {
                this.cancel();
                return super.mouseDragged(x, y, i, f, g);
            }
            int ordinal = (this.activeSelection.axis().ordinal() + 1) % 3;
            Direction.Axis axis = Direction.Axis.VALUES[ordinal];
            Vector3d dragNormal = JOMLConversion.atLowerCornerOf((Vec3i)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.activeSelection.axis()).getNormal());
            Vector3d pos = JOMLConversion.toJOML((Position)minecraft.player.getEyePosition());
            Vector3d relativePos = new Vector3d((Vector3dc)pos).sub((Vector3dc)subLevel.logicalPose().position());
            if (relativePos.dot((Vector3dc)(planeNormal = JOMLConversion.atLowerCornerOf((Vec3i)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis).getNormal()))) < 0.0) {
                planeNormal.negate();
            }
            boolean bl = hitsPlane = (dir = JOMLConversion.toJOML((Position)gizmoHandler.getMouseDir())).dot((Vector3dc)planeNormal) < 0.0;
            if (hitsPlane) {
                Vector3d negatedPlaneNormal = planeNormal.negate(new Vector3d());
                double d = planeNormal.dot((Vector3dc)relativePos);
                double rayLength = d / dir.dot((Vector3dc)negatedPlaneNormal);
                Vector3d hitPos = new Vector3d((Vector3dc)pos).fma(rayLength, (Vector3dc)dir);
                Vector3d subLevelPos = new Vector3d((Vector3dc)subLevel.logicalPose().position());
                subLevelPos.fma(-subLevelPos.dot((Vector3dc)dragNormal), (Vector3dc)dragNormal, subLevelPos);
                subLevelPos.fma(hitPos.dot((Vector3dc)dragNormal), (Vector3dc)dragNormal, subLevelPos);
                VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new ServerboundGizmoMoveSubLevelPacket(this.activeSelection.subLevel(), subLevelPos)});
            }
        }
        return super.mouseDragged(x, y, i, f, g);
    }

    public boolean isPauseScreen() {
        return false;
    }

    private void cancel() {
        this.dragging = false;
        this.activeSelection = null;
    }

    public void onClose() {
        SableClient.GIZMO_HANDLER.stop();
    }
}
