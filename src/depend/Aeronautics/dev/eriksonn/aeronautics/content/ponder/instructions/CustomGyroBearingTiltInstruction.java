/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import com.mojang.math.Axis;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.gyroscopic_propeller_bearing.GyroscopicPropellerBearingBlockEntity;
import java.util.Objects;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;

public class CustomGyroBearingTiltInstruction
extends TickingInstruction {
    protected final BlockPos location;
    protected final int ticks;
    protected final ElementLink<WorldSectionElement> link;
    protected final boolean directMotion;
    protected final boolean reversed;
    protected WorldSectionElement element;
    protected Quaternionf blockRot;
    protected Vec3 blockNormal;

    public CustomGyroBearingTiltInstruction(ElementLink<WorldSectionElement> link, BlockPos location, int ticks, boolean directMotion) {
        this(link, location, ticks, directMotion, false);
    }

    public CustomGyroBearingTiltInstruction(ElementLink<WorldSectionElement> link, BlockPos location, int ticks, boolean directMotion, boolean reversed) {
        super(false, ticks);
        this.location = location;
        this.ticks = ticks;
        this.link = link;
        this.directMotion = directMotion;
        this.reversed = reversed;
    }

    static Quaternionf getBlockStateOrientation(Direction facing) {
        Quaternionf orientation = facing.getAxis().isHorizontal() ? Axis.YP.rotationDegrees(AngleHelper.horizontalAngle((Direction)facing.getOpposite())) : new Quaternionf();
        orientation.mul((Quaternionfc)Axis.XP.rotationDegrees(-90.0f - AngleHelper.verticalAngle((Direction)facing)));
        return orientation;
    }

    protected final void firstTick(PonderScene scene) {
        super.firstTick(scene);
        PonderLevel level = scene.getWorld();
        if (this.link != null) {
            this.element = Objects.requireNonNull((WorldSectionElement)scene.resolve(this.link), "element");
        }
        if (level.getBlockState(this.location).hasProperty((Property)BlockStateProperties.FACING)) {
            Quaternionf q = CustomGyroBearingTiltInstruction.getBlockStateOrientation((Direction)level.getBlockState(this.location).getValue((Property)BlockStateProperties.FACING));
            this.blockNormal = Vec3.atLowerCornerOf((Vec3i)((Direction)level.getBlockState(this.location).getValue((Property)BlockStateProperties.FACING)).getNormal());
            this.blockRot = new Quaternionf((Quaternionfc)q);
        }
    }

    public void tick(PonderScene scene) {
        super.tick(scene);
        PonderLevel level = scene.getWorld();
        BlockEntity be = level.getBlockEntity(this.location);
        if (be instanceof GyroscopicPropellerBearingBlockEntity) {
            GyroscopicPropellerBearingBlockEntity gbe = (GyroscopicPropellerBearingBlockEntity)be;
            Vector3d target = new Vector3d(0.0, 1.0, 0.0);
            if (this.element != null) {
                Vec3 rot = this.element.getAnimatedRotation();
                target.set(0.0, 1.0, 0.0);
                target.rotateX(0.01745329238474369 * -rot.x).rotateZ(0.01745329238474369 * -rot.z).rotateY(0.01745329238474369 * -rot.y);
            }
            float lerpAmount = 1.0f;
            if (!this.directMotion) {
                lerpAmount = 1.0f - (float)this.remainingTicks / (float)this.totalTicks;
            }
            if (this.reversed) {
                lerpAmount = 1.0f - lerpAmount;
            }
            gbe.setStrictTilt(target, lerpAmount, 1.0);
        }
    }
}
