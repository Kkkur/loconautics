package com.lycoris.loconautics.content.transmission;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.function.Consumer;

public class TransmissionVisual extends KineticBlockEntityVisual<TransmissionBlockEntity> {

    private final RotatingInstance outputShaft;

    public TransmissionVisual(VisualizationContext context, TransmissionBlockEntity be, float partialTick) {
        super(context, be, partialTick);

        Direction.Axis axis = rotationAxis();
        Direction outputDir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

        // Output shaft half: spins at generated (redstone-set) speed
        outputShaft = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();
        outputShaft
                .rotateToFace(Direction.SOUTH, outputDir)
                .setup(be, axis, be.getGeneratedSpeed())
                .setPosition((Vec3i) getVisualPosition())
                .setChanged();
    }

    @Override
    public void update(float pt) {
        Direction.Axis axis = rotationAxis();
        outputShaft.setup(blockEntity, axis, blockEntity.getGeneratedSpeed()).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight((FlatLit) outputShaft);
    }

    @Override
    protected void _delete() {
        outputShaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(outputShaft);
    }
}