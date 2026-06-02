/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.OrientedRotatingVisual
 *  com.simibubi.create.content.kinetics.base.RotatingInstance
 *  com.simibubi.create.foundation.render.AllInstanceTypes
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class SwivelBearingVisual
extends OrientedRotatingVisual<SwivelBearingBlockEntity> {
    private final RotatingInstance topShaft;
    private final RotatingInstance cogInstance;

    public SwivelBearingVisual(VisualizationContext context, SwivelBearingBlockEntity blockEntity, float partialTick) {
        super(context, (KineticBlockEntity)blockEntity, partialTick, Direction.SOUTH, ((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite(), Models.partial((PartialModel)SimPartialModels.SHAFT_SIXTEENTH));
        this.topShaft = ((RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)SimPartialModels.SHAFT_SIXTEENTH)).createInstance()).rotateToFace(Direction.SOUTH, (Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).setup((KineticBlockEntity)blockEntity).setPosition((Vec3i)this.getVisualPosition());
        this.cogInstance = ((RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)SimPartialModels.SWIVEL_BEARING_COG)).createInstance()).rotateToFace(Direction.UP, ((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite()).setup(blockEntity.getExtraKinetics()).setPosition((Vec3i)this.getVisualPosition());
        this.topShaft.setVisible(!((SwivelBearingBlockEntity)this.blockEntity).isAssembled());
        this.topShaft.setChanged();
        this.cogInstance.setChanged();
    }

    public void update(float pt) {
        super.update(pt);
        this.topShaft.setVisible(!((SwivelBearingBlockEntity)this.blockEntity).isAssembled());
        this.topShaft.setup((KineticBlockEntity)this.blockEntity).setChanged();
        this.cogInstance.setup(((SwivelBearingBlockEntity)this.blockEntity).getExtraKinetics()).setChanged();
    }

    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.topShaft});
        this.relight(new FlatLit[]{this.cogInstance});
    }

    protected void _delete() {
        super._delete();
        this.topShaft.delete();
        this.cogInstance.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.topShaft);
        consumer.accept((Instance)this.cogInstance);
    }
}
