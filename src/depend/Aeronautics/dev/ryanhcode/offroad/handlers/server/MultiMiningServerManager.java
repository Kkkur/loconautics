/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
 *  com.simibubi.create.foundation.utility.BlockHelper
 *  dev.engine_room.flywheel.lib.util.LevelAttached
 *  foundry.veil.api.network.VeilPacketManager
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.offroad.handlers.server;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import dev.engine_room.flywheel.lib.util.LevelAttached;
import dev.ryanhcode.offroad.handlers.MultiminingDataTickResult;
import dev.ryanhcode.offroad.handlers.server.MultiMiningSupplier;
import dev.ryanhcode.offroad.network.borehead_bearing.ClientboundMultiMiningSync;
import foundry.veil.api.network.VeilPacketManager;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MultiMiningServerManager {
    public static final LevelAttached<MultiMiningServerManager> ATTACHED_DATA = new LevelAttached(level -> {
        if (level.isClientSide()) {
            return null;
        }
        return new MultiMiningServerManager();
    });
    private static final AtomicInteger MULTIMINING_IDS = BlockBreakingKineticBlockEntity.NEXT_BREAKER_ID;
    private final int breakingId = -MULTIMINING_IDS.incrementAndGet();
    private final Map<BlockPos, BlockBreakingData> breakingDataMap = new Object2ObjectOpenHashMap();
    private final Map<BlockPos, ClientboundMultiMiningSync> dirtyBreakingData = new Object2ObjectOpenHashMap();

    public static boolean addOrRefreshPos(Level level, BlockPos pos, MultiMiningSupplier supplier) {
        if (level.isClientSide) {
            return false;
        }
        MultiMiningServerManager breakingDataMap = (MultiMiningServerManager)ATTACHED_DATA.get((LevelAccessor)level);
        if (breakingDataMap != null) {
            return breakingDataMap.handleAddedPos(pos, supplier);
        }
        return false;
    }

    public static void tick(Level level) {
        if (level.isClientSide) {
            return;
        }
        MultiMiningServerManager breakingDataMap = (MultiMiningServerManager)ATTACHED_DATA.get((LevelAccessor)level);
        if (breakingDataMap != null) {
            breakingDataMap.handleTicking(level);
        }
    }

    public boolean handleAddedPos(BlockPos pos, MultiMiningSupplier supplier) {
        BlockBreakingData existingData = this.breakingDataMap.get(pos);
        if (existingData == null) {
            existingData = new BlockBreakingData(pos);
            this.breakingDataMap.put(pos, existingData);
            existingData.addSupplier(supplier, true);
            return true;
        }
        existingData.addSupplier(supplier, true);
        return false;
    }

    private void handleTicking(Level level) {
        Iterator<Map.Entry<BlockPos, BlockBreakingData>> iter = this.breakingDataMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<BlockPos, BlockBreakingData> set = iter.next();
            BlockBreakingData data = set.getValue();
            byte beforeProgress = data.getDestroyProgressByted();
            MultiminingDataTickResult result = data.tick(level);
            switch (result) {
                case BROKEN: {
                    iter.remove();
                    break;
                }
                case STOP: {
                    data.setDestroyProgress(-1.0f);
                    this.addSyncingData(data);
                    iter.remove();
                    break;
                }
                case CONTINUE: {
                    if (beforeProgress == data.getDestroyProgressByted()) break;
                    this.addSyncingData(data);
                }
            }
        }
        if (!this.dirtyBreakingData.isEmpty()) {
            for (Map.Entry<BlockPos, ClientboundMultiMiningSync> set : this.dirtyBreakingData.entrySet()) {
                VeilPacketManager.tracking((ServerLevel)((ServerLevel)level), (BlockPos)set.getKey()).sendPacket(new CustomPacketPayload[]{set.getValue()});
            }
            this.dirtyBreakingData.clear();
        }
    }

    private void addSyncingData(BlockBreakingData data) {
        if (data.getLastKnownSupplierPosition() != null) {
            this.dirtyBreakingData.computeIfAbsent((BlockPos)data.getLastKnownSupplierPosition(), (Function<BlockPos, ClientboundMultiMiningSync>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$addSyncingData$1(net.minecraft.core.BlockPos ), (Lnet/minecraft/core/BlockPos;)Ldev/ryanhcode/offroad/network/borehead_bearing/ClientboundMultiMiningSync;)((MultiMiningServerManager)this)).inData.put(data.getBreakingPos(), data);
        }
    }

    private /* synthetic */ ClientboundMultiMiningSync lambda$addSyncingData$1(BlockPos p) {
        return ClientboundMultiMiningSync.serverOutboundData(this.breakingId);
    }

    public static class BlockBreakingData {
        private static final int MAX_TIMEOUT = 20;
        private final List<MultiMiningSupplier> suppliers = new ArrayList<MultiMiningSupplier>();
        private final BlockPos breakingPos;
        private float timeoutTicks = 0.0f;
        private float destroyProgress = 0.0f;
        private BlockPos lastKnownSupplierPosition = null;

        public BlockBreakingData(BlockPos breakingPos) {
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
}
