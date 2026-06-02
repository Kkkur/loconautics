/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.redstone.redstone_inductor;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_inductor.RedstoneInductorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class RedstoneInductorVisual
extends AbstractBlockEntityVisual<RedstoneInductorBlockEntity>
implements SimpleDynamicVisual {
    private final OrientedInstance redstoneIndicator = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)SimPartialModels.REDSTONE_INDUCTOR_INDICATOR)).createInstance();

    public RedstoneInductorVisual(VisualizationContext ctx, RedstoneInductorBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.redstoneIndicator.position((Vec3i)this.getVisualPosition()).translatePosition(0.5f, 0.0f, 0.5f).translatePivot(-0.5f, 0.0f, -0.5f).rotateYDegrees(AngleHelper.horizontalAngle((Direction)((Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING))));
        this.redstoneIndicator.colorArgb(SimColors.redstone(((RedstoneInductorBlockEntity)this.blockEntity).lerpedState.getValue(partialTick) / 15.0f));
    }

    public void beginFrame(DynamicVisual.Context context) {
        this.redstoneIndicator.colorArgb(SimColors.redstone(((RedstoneInductorBlockEntity)this.blockEntity).lerpedState.getValue(context.partialTick()) / 15.0f));
        this.redstoneIndicator.setChanged();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept((Instance)this.redstoneIndicator);
    }

    public void updateLight(float v) {
        this.relight(new FlatLit[]{this.redstoneIndicator});
    }

    protected void _delete() {
        this.redstoneIndicator.delete();
    }
}
