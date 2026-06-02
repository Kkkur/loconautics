/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.multiloader.tanks.CFluidType;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class DockingConnectorTank
extends SingleTank {
    private final DockingConnectorBlockEntity blockEntity;
    private BlockPos connectedPos;
    private DockingConnectorTank connectedTank;
    private boolean inserting = false;

    public DockingConnectorTank(DockingConnectorBlockEntity blockEntity) {
        super(1000);
        this.blockEntity = blockEntity;
    }

    public void connect(BlockPos pos, DockingConnectorTank other) {
        this.connectedPos = pos;
        this.connectedTank = other;
    }

    public void disconnect() {
        this.connectedPos = null;
        this.connectedTank = null;
    }

    private boolean canInteract() {
        BlockEntity blockEntity;
        Level level = this.blockEntity.getLevel();
        if (level == null) {
            return false;
        }
        if (this.connectedPos == null || !((blockEntity = level.getBlockEntity(this.connectedPos)) instanceof DockingConnectorBlockEntity)) {
            return false;
        }
        DockingConnectorBlockEntity other = (DockingConnectorBlockEntity)blockEntity;
        return this.connectedTank == other.tank;
    }

    @Override
    public long insert(CFluidType insertedType, long maxAmount, boolean simulate, Runnable beforeApply) {
        if (!this.canInteract()) {
            return 0L;
        }
        this.inserting = true;
        long v = DockingConnectorTank.calculateInsert(this.connectedTank, insertedType, maxAmount);
        if (!simulate) {
            if (beforeApply != null) {
                beforeApply.run();
            }
            DockingConnectorTank.applyInsert(this.connectedTank, insertedType, v);
        }
        return v;
    }

    @Override
    public long extract(CFluidType extractedType, long maxAmount, boolean simulate, @Nullable Runnable beforeApply) {
        this.inserting = false;
        return super.extract(extractedType, maxAmount, simulate, beforeApply);
    }

    @Override
    public Tuple<CFluidType, Long> createSnapshot() {
        if (this.inserting && this.canInteract()) {
            return new Tuple((Object)this.connectedTank.type, (Object)this.connectedTank.amount);
        }
        return super.createSnapshot();
    }

    @Override
    public void readSnapshot(CFluidType type, long amount) {
        if (this.inserting && this.canInteract()) {
            this.connectedTank.type = type;
            this.connectedTank.amount = amount;
        } else {
            super.readSnapshot(type, amount);
        }
    }
}
