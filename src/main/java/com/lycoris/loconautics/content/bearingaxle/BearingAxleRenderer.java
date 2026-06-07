package com.lycoris.loconautics.content.bearingaxle;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class BearingAxleRenderer extends KineticBlockEntityRenderer<BearingAxleBlockEntity> {

    public BearingAxleRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    // Return Create's own shaft blockstate instead of our block model.
    // This renders only the spinning shaft — the housing casing comes from
    // the static chunk-baked block model, exactly like EncasedShaft.
    @Override
    protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}