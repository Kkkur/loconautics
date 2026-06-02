/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.util.SmartRecycler
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidType
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.content.fluids.FluidInstance;
import com.simibubi.create.content.fluids.FluidMesh;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import java.util.function.Consumer;
import java.util.function.Function;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class GlassPipeVisual
extends AbstractBlockEntityVisual<StraightPipeBlockEntity>
implements SimpleDynamicVisual {
    private int light;
    private final SmartRecycler<TextureAtlasSprite, FluidInstance> stream = new SmartRecycler(sprite -> (FluidInstance)ctx.instancerProvider().instancer(AllInstanceTypes.FLUID, FluidMesh.stream(sprite)).createInstance());
    private final SmartRecycler<TextureAtlasSprite, TransformedInstance> surface = new SmartRecycler(sprite -> (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(sprite, 0.1875f)).createInstance());

    public GlassPipeVisual(VisualizationContext ctx, StraightPipeBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.stream.resetCount();
        this.surface.resetCount();
        FluidTransportBehaviour pipe = ((StraightPipeBlockEntity)this.blockEntity).getBehaviour(FluidTransportBehaviour.TYPE);
        if (pipe == null) {
            this.stream.discardExtra();
            this.surface.discardExtra();
            return;
        }
        for (Direction side : Iterate.directions) {
            LerpedFloat progressLerp;
            FluidStack fluidStack;
            PipeConnection.Flow flow = pipe.getFlow(side);
            if (flow == null || (fluidStack = flow.fluid).isEmpty() || (progressLerp = flow.progress) == null) continue;
            float progress = progressLerp.getValue(ctx.partialTick());
            boolean inbound = flow.inbound;
            if (progress == 1.0f) {
                if (inbound) {
                    PipeConnection.Flow opposite = pipe.getFlow(side.getOpposite());
                    if (opposite == null) {
                        progress -= 1.0E-6f;
                    }
                } else {
                    FluidTransportBehaviour adjacent = BlockEntityBehaviour.get((BlockGetter)this.level, this.pos.relative(side), FluidTransportBehaviour.TYPE);
                    if (adjacent == null) {
                        progress -= 1.0E-6f;
                    } else {
                        PipeConnection.Flow other = adjacent.getFlow(side.getOpposite());
                        if (other == null || !other.inbound && !other.complete) {
                            progress -= 1.0E-6f;
                        }
                    }
                }
            }
            Fluid fluid = fluidStack.getFluid();
            IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of((Fluid)fluid);
            FluidType fluidAttributes = fluid.getFluidType();
            Function atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            TextureAtlasSprite flowTexture = (TextureAtlasSprite)atlas.apply(clientFluid.getFlowingTexture(fluidStack));
            int color = clientFluid.getTintColor(fluidStack);
            int blockLightIn = this.light >> 4 & 0xF;
            int luminosity = Math.max(blockLightIn, fluidAttributes.getLightLevel(fluidStack));
            int light = this.light & 0xF00000 | luminosity << 4;
            if (inbound) {
                side = side.getOpposite();
            }
            float yStart = inbound ? 0.0f : 0.5f;
            float progressOffset = Mth.clamp((float)(progress * 0.5f), (float)0.0f, (float)1.0f);
            FluidInstance fluidInstance = (FluidInstance)this.stream.get((Object)flowTexture);
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)fluidInstance.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateTo(Direction.UP, side)).translate(0.0f, -0.5f + yStart, 0.0f);
            fluidInstance.light(light).colorArgb(color);
            fluidInstance.vScale = (flowTexture.getV1() - flowTexture.getV0()) * 0.5f;
            fluidInstance.v0 = flowTexture.getV0() + yStart * fluidInstance.vScale;
            fluidInstance.progress = progressOffset;
            fluidInstance.setChanged();
            if (progress == 1.0f) continue;
            TextureAtlasSprite stillTexture = (TextureAtlasSprite)atlas.apply(clientFluid.getStillTexture(fluidStack));
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.surface.get((Object)stillTexture)).setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateTo(Direction.UP, side)).translate(0.0f, -0.5f + yStart + progressOffset, 0.0f).light(light).colorArgb(color).setChanged();
        }
        this.stream.discardExtra();
        this.surface.discardExtra();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
    }

    public void updateLight(float partialTick) {
        this.light = this.computePackedLight();
    }

    protected void _delete() {
        this.stream.delete();
        this.surface.delete();
    }
}
