/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
 *  com.simibubi.create.foundation.utility.BlockHelper
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.offroad.handlers.server;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import dev.ryanhcode.offroad.handlers.MultiminingDataTickResult;
import dev.ryanhcode.offroad.handlers.server.MultiMiningSupplier;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public static class MultiMiningServerManager.BlockBreakingData {
    private static final int MAX_TIMEOUT = 20;
    private final List<MultiMiningSupplier> suppliers = new ArrayList<MultiMiningSupplier>();
    private final BlockPos breakingPos;
    private float timeoutTicks = 0.0f;
    private float destroyProgress = 0.0f;
    private BlockPos lastKnownSupplierPosition = null;

    public MultiMiningServerManager.BlockBreakingData(BlockPos breakingPos) {
        this.breakingPos = breakingPos;
    }

    public MultiminingDataTickResult tick(Level level) {
        float f = this.timeoutTicks;
        this.timeoutTicks = f + 1.0f;
        if (f > 20.0f) {
            this.cleanData();
            return MultiminingDataTickResult.STOP;
        }
        this.suppliers.removeIf(supplier -> !supplier.isActive());
        if (this.suppliers.isEmpty()) {
            this.cleanData();
            return MultiminingDataTickResult.STOP;
        }
        this.lastKnownSupplierPosition = this.suppliers.getFirst().getLocation();
        BlockState state = level.getBlockState(this.getBreakingPos());
        float hardness = state.getDestroySpeed((BlockGetter)level, this.getBreakingPos());
        if (state.isAir() || !BlockBreakingKineticBlockEntity.isBreakable((BlockState)state, (float)hardness)) {
            this.cleanData();
            return MultiminingDataTickResult.STOP;
        }
        if (this.timeoutTicks < 5.0f) {
            float averageMiningSpeed = 0.0f;
            for (MultiMiningSupplier supplier2 : this.suppliers) {
                averageMiningSpeed += Math.abs(supplier2.getBreakingSpeed(level, this.breakingPos, state)) / (float)this.suppliers.size();
            }
            double nextProgressStep = averageMiningSpeed / hardness;
            this.setDestroyProgress((float)((double)this.getDestroyProgress() + nextProgressStep));
            if (this.getDestroyProgress() >= 10.0f) {
                Collections.shuffle(this.suppliers);
                BlockHelper.destroyBlock((Level)level, (BlockPos)this.getBreakingPos(), (float)1.0f, stack -> {
                    for (MultiMiningSupplier supplier : this.suppliers) {
                        supplier.itemCallback((ItemStack)stack);
                        if (!stack.isEmpty()) continue;
                        break;
                    }
                    if (!stack.isEmpty()) {
                        Block.popResource((Level)level, (BlockPos)this.getBreakingPos(), (ItemStack)stack);
                    }
                });
                this.cleanData();
                return MultiminingDataTickResult.BROKEN;
            }
        }
        return MultiminingDataTickResult.CONTINUE;
    }

    private void cleanData() {
        this.suppliers.clear();
    }

    public void addSupplier(MultiMiningSupplier supplier, boolean refreshTimeout) {
        if (!this.suppliers.contains(supplier)) {
            this.suppliers.add(supplier);
        }
        if (refreshTimeout) {
            this.timeoutTicks = 0.0f;
        }
    }

    public byte getDestroyProgressByted() {
        return (byte)this.getDestroyProgress();
    }

    public void clientAimedSerialization(ByteBuf buf) {
        boolean invalid = this.getDestroyProgress() >= 10.0f;
        buf.writeBoolean(invalid);
        if (!invalid) {
            buf.writeByte((int)((byte)this.getDestroyProgress()));
        }
    }

    public BlockPos getBreakingPos() {
        return this.breakingPos;
    }

    public BlockPos getLastKnownSupplierPosition() {
        return this.lastKnownSupplierPosition;
    }

    public float getDestroyProgress() {
        return this.destroyProgress;
    }

    public void setDestroyProgress(float destroyProgress) {
        this.destroyProgress = destroyProgress;
    }
}
