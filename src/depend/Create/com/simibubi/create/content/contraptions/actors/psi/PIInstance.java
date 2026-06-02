/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.InstancerProvider
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.actors.psi;

import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class PIInstance {
    private final InstancerProvider instancerProvider;
    private final BlockState blockState;
    private final BlockPos instancePos;
    private final float angleX;
    private final float angleY;
    private boolean lit;
    TransformedInstance middle;
    TransformedInstance top;

    public PIInstance(InstancerProvider instancerProvider, BlockState blockState, BlockPos instancePos, boolean lit) {
        this.instancerProvider = instancerProvider;
        this.blockState = blockState;
        this.instancePos = instancePos;
        Direction facing = (Direction)blockState.getValue((Property)PortableStorageInterfaceBlock.FACING);
        this.angleX = facing == Direction.UP ? 0.0f : (facing == Direction.DOWN ? 180.0f : 90.0f);
        this.angleY = AngleHelper.horizontalAngle((Direction)facing);
        this.lit = lit;
        this.middle = (TransformedInstance)instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)PortableStorageInterfaceRenderer.getMiddleForState(blockState, lit))).createInstance();
        this.top = (TransformedInstance)instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)PortableStorageInterfaceRenderer.getTopForState(blockState))).createInstance();
    }

    public void beginFrame(float progress) {
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.middle.setIdentityTransform().translate((Vec3i)this.instancePos)).center()).rotateYDegrees(this.angleY)).rotateXDegrees(this.angleX)).uncenter();
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.top.setIdentityTransform().translate((Vec3i)this.instancePos)).center()).rotateYDegrees(this.angleY)).rotateXDegrees(this.angleX)).uncenter();
        this.middle.translate(0.0f, progress * 0.5f + 0.375f, 0.0f);
        this.top.translate(0.0f, progress, 0.0f);
        this.middle.setChanged();
        this.top.setChanged();
    }

    public void tick(boolean lit) {
        if (this.lit != lit) {
            this.lit = lit;
            this.instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)PortableStorageInterfaceRenderer.getMiddleForState(this.blockState, lit))).stealInstance((Instance)this.middle);
        }
    }

    public void remove() {
        this.middle.delete();
        this.top.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.middle);
        consumer.accept((Instance)this.top);
    }
}
