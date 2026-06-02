/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.utility.CreateLang
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.throttle_lever;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.simulated_team.simulated.content.blocks.behaviour.HoldTipBehaviour;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlock;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverClientGripHandler;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimClickInteractions;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThrottleLeverBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    protected int state = 0;
    protected int lastChange;
    protected LerpedFloat clientAngle;
    public final LerpedFloat clientPressedLerp = LerpedFloat.linear().chase(0.0, 0.45, LerpedFloat.Chaser.EXP);
    private static final MutableComponent HOLD_TIP = SimLang.translate("gui.hold_tip.hold_to_adjust", new Object[0]).component();

    public ThrottleLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.clientAngle = LerpedFloat.linear();
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("State", this.state);
        compound.putInt("ChangeTimer", this.lastChange);
        super.write(compound, registries, clientPacket);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.state = compound.getInt("State");
        this.lastChange = compound.getInt("ChangeTimer");
        this.clientAngle.chase((Boolean)this.getBlockState().getValue((Property)ThrottleLeverBlock.INVERTED) != false ? (double)(15 - this.state) : (double)this.state, 0.5, LerpedFloat.Chaser.EXP);
        super.read(compound, registries, clientPacket);
    }

    public void tick() {
        super.tick();
        if (this.lastChange > 0) {
            --this.lastChange;
            if (this.lastChange == 0) {
                this.updateOutput();
            }
        }
        if (this.level.isClientSide) {
            this.clientAngle.tickChaser();
            boolean pressed = SimClickInteractions.THROTTLE_LEVER_MANAGER.isBlockActive(this.getBlockPos());
            this.clientPressedLerp.updateChaseTarget(pressed ? 1.0f : 0.0f);
            this.clientPressedLerp.tickChaser();
            ThrottleLeverClientGripHandler.tickGrip(this);
        }
    }

    public void initialize() {
        super.initialize();
    }

    private void updateOutput() {
        ThrottleLeverBlock.updateNeighbors(this.getBlockState(), this.level, this.worldPosition);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new HoldTipBehaviour((SmartBlockEntity)this, HOLD_TIP));
    }

    public void changeState(boolean back) {
        int prevState = this.state;
        this.state += back ? -1 : 1;
        this.state = Mth.clamp((int)this.state, (int)0, (int)15);
        if (prevState != this.state) {
            this.lastChange = 15;
        }
        this.sendData();
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.builder().add(CreateLang.translateDirect((String)"tooltip.analogStrength", (Object[])new Object[]{this.state})).forGoggles(tooltip);
        return true;
    }

    public AABB getRenderBoundingBox() {
        return AABB.ofSize((Vec3)this.getBlockPos().getCenter(), (double)1.5, (double)1.5, (double)1.5);
    }

    public int getState() {
        return this.state;
    }

    public void setSignal(int signal) {
        this.state = (Boolean)this.getBlockState().getValue((Property)ThrottleLeverBlock.INVERTED) != false ? 15 - signal : signal;
        this.lastChange = 2;
        this.level.playSound(null, this.getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.2f, 0.25f + (float)(signal + 5) / 15.0f * 0.5f);
        this.sendData();
    }
}
