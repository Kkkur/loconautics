/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.PonderElement
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.PonderSceneBuilder
 *  net.createmod.ponder.foundation.PonderSceneBuilder$PonderWorldInstructions
 *  net.createmod.ponder.foundation.element.ElementLinkImpl
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.foundation.ponder;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.crafter.ConnectedInputHandler;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;
import com.simibubi.create.foundation.ponder.instruction.AnimateBlockEntityInstruction;
import java.util.function.UnaryOperator;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.element.ElementLinkImpl;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CreateSceneBuilder.WorldInstructions
extends PonderSceneBuilder.PonderWorldInstructions {
    public CreateSceneBuilder.WorldInstructions() {
        super((PonderSceneBuilder)CreateSceneBuilder.this);
    }

    public void rotateBearing(BlockPos pos, float angle, int duration) {
        CreateSceneBuilder.this.addInstruction((PonderInstruction)AnimateBlockEntityInstruction.bearing(pos, angle, duration));
    }

    public void movePulley(BlockPos pos, float distance, int duration) {
        CreateSceneBuilder.this.addInstruction((PonderInstruction)AnimateBlockEntityInstruction.pulley(pos, distance, duration));
    }

    public void animateBogey(BlockPos pos, float distance, int duration) {
        CreateSceneBuilder.this.addInstruction((PonderInstruction)AnimateBlockEntityInstruction.bogey(pos, distance, duration + 1));
    }

    public void moveDeployer(BlockPos pos, float distance, int duration) {
        CreateSceneBuilder.this.addInstruction((PonderInstruction)AnimateBlockEntityInstruction.deployer(pos, distance, duration));
    }

    public void createItemOnBeltLike(BlockPos location, Direction insertionSide, ItemStack stack) {
        CreateSceneBuilder.this.addInstruction(scene -> {
            PonderLevel world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(location);
            if (!(blockEntity instanceof SmartBlockEntity)) {
                return;
            }
            SmartBlockEntity beltBlockEntity = (SmartBlockEntity)blockEntity;
            DirectBeltInputBehaviour behaviour = beltBlockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE);
            if (behaviour == null) {
                return;
            }
            behaviour.handleInsertion(stack, insertionSide.getOpposite(), false);
        });
        this.flapFunnel(location.above(), true);
    }

    public ElementLink<BeltItemElement> createItemOnBelt(BlockPos beltLocation, Direction insertionSide, ItemStack stack) {
        ElementLinkImpl link = new ElementLinkImpl(BeltItemElement.class);
        CreateSceneBuilder.this.addInstruction(arg_0 -> CreateSceneBuilder.WorldInstructions.lambda$createItemOnBelt$2(beltLocation, stack, insertionSide, (ElementLink)link, arg_0));
        this.flapFunnel(beltLocation.above(), true);
        return link;
    }

    public void removeItemsFromBelt(BlockPos beltLocation) {
        CreateSceneBuilder.this.addInstruction(scene -> {
            PonderLevel world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(beltLocation);
            if (!(blockEntity instanceof SmartBlockEntity)) {
                return;
            }
            SmartBlockEntity beltBlockEntity = (SmartBlockEntity)blockEntity;
            TransportedItemStackHandlerBehaviour transporter = beltBlockEntity.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
            if (transporter == null) {
                return;
            }
            transporter.handleCenteredProcessingOnAllItems(0.52f, tis -> TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
        });
    }

    public void stallBeltItem(ElementLink<BeltItemElement> link, boolean stalled) {
        CreateSceneBuilder.this.addInstruction(scene -> {
            BeltItemElement resolve = (BeltItemElement)scene.resolve(link);
            if (resolve != null) {
                resolve.ifPresent(tis -> {
                    tis.locked = stalled;
                });
            }
        });
    }

    public void changeBeltItemTo(ElementLink<BeltItemElement> link, ItemStack newStack) {
        CreateSceneBuilder.this.addInstruction(scene -> {
            BeltItemElement resolve = (BeltItemElement)scene.resolve(link);
            if (resolve != null) {
                resolve.ifPresent(tis -> {
                    tis.stack = newStack;
                });
            }
        });
    }

    public void setKineticSpeed(Selection selection, float speed) {
        this.modifyKineticSpeed(selection, f -> Float.valueOf(speed));
    }

    public void multiplyKineticSpeed(Selection selection, float modifier) {
        this.modifyKineticSpeed(selection, f -> Float.valueOf(f.floatValue() * modifier));
    }

    public void modifyKineticSpeed(Selection selection, UnaryOperator<Float> speedFunc) {
        this.modifyBlockEntityNBT(selection, SpeedGaugeBlockEntity.class, nbt -> {
            float newSpeed = ((Float)speedFunc.apply(Float.valueOf(nbt.getFloat("Speed")))).floatValue();
            nbt.putFloat("Value", SpeedGaugeBlockEntity.getDialTarget(newSpeed));
        });
        this.modifyBlockEntityNBT(selection, KineticBlockEntity.class, nbt -> nbt.putFloat("Speed", ((Float)speedFunc.apply(Float.valueOf(nbt.getFloat("Speed")))).floatValue()));
    }

    public void propagatePipeChange(BlockPos pos) {
        this.modifyBlockEntity(pos, PumpBlockEntity.class, be -> be.onSpeedChanged(0.0f));
    }

    public void setFilterData(Selection selection, Class<? extends BlockEntity> teType, ItemStack filter) {
        this.modifyBlockEntityNBT(selection, teType, nbt -> nbt.put("Filter", filter.saveOptional(CreateSceneBuilder.this.world().getHolderLookupProvider())));
    }

    public void instructArm(BlockPos armLocation, ArmBlockEntity.Phase phase, ItemStack heldItem, int targetedPoint) {
        this.modifyBlockEntityNBT(CreateSceneBuilder.this.scene.getSceneBuildingUtil().select().position(armLocation), ArmBlockEntity.class, compound -> {
            NBTHelper.writeEnum((CompoundTag)compound, (String)"Phase", (Enum)phase);
            compound.put("HeldItem", heldItem.saveOptional(CreateSceneBuilder.this.world().getHolderLookupProvider()));
            compound.putInt("TargetPointIndex", targetedPoint);
            compound.putFloat("MovementProgress", 0.0f);
        });
    }

    public void flapFunnel(BlockPos position, boolean outward) {
        this.modifyBlockEntity(position, FunnelBlockEntity.class, funnel -> funnel.flap(!outward));
    }

    public void setCraftingResult(BlockPos crafter, ItemStack output) {
        this.modifyBlockEntity(crafter, MechanicalCrafterBlockEntity.class, mct -> mct.setScriptedResult(output));
    }

    public void connectCrafterInvs(BlockPos position1, BlockPos position2) {
        CreateSceneBuilder.this.addInstruction(s -> {
            ConnectedInputHandler.toggleConnection((Level)s.getWorld(), position1, position2);
            s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
        });
    }

    public void toggleControls(BlockPos position) {
        this.cycleBlockProperty(position, (Property)ControlsBlock.VIRTUAL);
    }

    public void animateTrainStation(BlockPos position, boolean trainPresent) {
        this.modifyBlockEntityNBT(CreateSceneBuilder.this.getScene().getSceneBuildingUtil().select().position(position), StationBlockEntity.class, c -> c.putBoolean("ForceFlag", trainPresent));
    }

    public void conductorBlaze(BlockPos position, boolean conductor) {
        this.modifyBlockEntityNBT(CreateSceneBuilder.this.getScene().getSceneBuildingUtil().select().position(position), BlazeBurnerBlockEntity.class, c -> c.putBoolean("TrainHat", conductor));
    }

    public void changeSignalState(BlockPos position, SignalBlockEntity.SignalState state) {
        this.modifyBlockEntityNBT(CreateSceneBuilder.this.getScene().getSceneBuildingUtil().select().position(position), SignalBlockEntity.class, c -> NBTHelper.writeEnum((CompoundTag)c, (String)"State", (Enum)state));
    }

    public void setDisplayBoardText(BlockPos position, int line, Component text) {
        this.modifyBlockEntity(position, FlapDisplayBlockEntity.class, t -> t.applyTextManually(line, text));
    }

    public void dyeDisplayBoard(BlockPos position, int line, DyeColor color) {
        this.modifyBlockEntity(position, FlapDisplayBlockEntity.class, t -> t.setColour(line, color));
    }

    public void flashDisplayLink(BlockPos position) {
        this.modifyBlockEntity(position, LinkWithBulbBlockEntity.class, LinkWithBulbBlockEntity::pulse);
    }

    public void restoreBlocks(Selection selection) {
        super.restoreBlocks(selection);
        this.markSmartBlockEntityVirtual(selection);
    }

    public void setBlocks(Selection selection, BlockState state, boolean spawnParticles) {
        super.setBlocks(selection, state, spawnParticles);
        this.markSmartBlockEntityVirtual(selection);
    }

    public void modifyBlocks(Selection selection, UnaryOperator<BlockState> stateFunc, boolean spawnParticles) {
        super.modifyBlocks(selection, stateFunc, spawnParticles);
        this.markSmartBlockEntityVirtual(selection);
    }

    private void markSmartBlockEntityVirtual(Selection selection) {
        CreateSceneBuilder.this.addInstruction(scene -> selection.forEach(pos -> {
            BlockEntity patt0$temp = scene.getWorld().getBlockEntity(pos);
            if (patt0$temp instanceof SmartBlockEntity) {
                SmartBlockEntity smartBlockEntity = (SmartBlockEntity)patt0$temp;
                smartBlockEntity.markVirtual();
            }
        }));
    }

    private static /* synthetic */ void lambda$createItemOnBelt$2(BlockPos beltLocation, ItemStack stack, Direction insertionSide, ElementLink link, PonderScene scene) {
        PonderLevel world = scene.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(beltLocation);
        if (!(blockEntity instanceof BeltBlockEntity)) {
            return;
        }
        BeltBlockEntity beltBlockEntity = (BeltBlockEntity)blockEntity;
        DirectBeltInputBehaviour behaviour = beltBlockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE);
        behaviour.handleInsertion(stack, insertionSide.getOpposite(), false);
        BeltBlockEntity controllerBE = beltBlockEntity.getControllerBE();
        if (controllerBE != null) {
            controllerBE.tick();
        }
        TransportedItemStackHandlerBehaviour transporter = beltBlockEntity.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
        transporter.handleProcessingOnAllItems(tis -> {
            BeltItemElement tracker = new BeltItemElement((TransportedItemStack)tis);
            scene.addElement((PonderElement)tracker);
            scene.linkElement((PonderElement)tracker, link);
            return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
        });
    }
}
