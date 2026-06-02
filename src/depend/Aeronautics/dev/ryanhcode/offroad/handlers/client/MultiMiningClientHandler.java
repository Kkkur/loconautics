/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
 *  dev.engine_room.flywheel.lib.util.LevelAttached
 *  dev.ryanhcode.sable.util.LevelAccelerator
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.offroad.handlers.client;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import dev.engine_room.flywheel.lib.util.LevelAttached;
import dev.ryanhcode.offroad.handlers.MultiminingDataTickResult;
import dev.ryanhcode.offroad.mixin.client.multimining_destruction_progress.ClientLevelAccessor;
import dev.ryanhcode.offroad.mixin_interface.level_renderer.MultiMiningDestructionExtension;
import dev.ryanhcode.sable.util.LevelAccelerator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class MultiMiningClientHandler {
    public static final LevelAttached<MultiMiningClientHandler> LEVEL_ATTACHED = new LevelAttached(level -> {
        if (level instanceof PonderLevel || !level.isClientSide()) {
            return null;
        }
        MultiMiningClientHandler clientHandler = new MultiMiningClientHandler();
        clientHandler.accelerator = new LevelAccelerator((Level)level);
        return clientHandler;
    });
    private static int soundCount = 0;
    private final Map<BlockPos, ClientBlockBreakingData> clientBlockBreakingData = new Object2ObjectOpenHashMap();
    private final Map<BlockPos, ClientBlockBreakingData> dirtyData = new Object2ObjectOpenHashMap();
    private int breakingID;
    private LevelAccelerator accelerator;

    public static void handleInboundClientUpdate(Level level, Map<BlockPos, ClientBlockBreakingData> incomingData, int breakingID) {
        if (!level.isClientSide || incomingData.isEmpty()) {
            return;
        }
        MultiMiningClientHandler clientHandler = (MultiMiningClientHandler)LEVEL_ATTACHED.get((LevelAccessor)level);
        if (clientHandler == null) {
            return;
        }
        clientHandler.clientBlockBreakingData.putAll(incomingData);
        clientHandler.breakingID = breakingID;
    }

    public static void tick(Level level) {
        if (level instanceof PonderLevel || !level.isClientSide) {
            return;
        }
        MultiMiningClientHandler clientHandler = (MultiMiningClientHandler)LEVEL_ATTACHED.get((LevelAccessor)level);
        if (clientHandler != null) {
            clientHandler.handleTick(level);
        }
    }

    private void handleTick(Level level) {
        int blocksBeingBroken = this.clientBlockBreakingData.size();
        Iterator<Map.Entry<BlockPos, ClientBlockBreakingData>> iter = this.clientBlockBreakingData.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<BlockPos, ClientBlockBreakingData> dataSet = iter.next();
            MultiminingDataTickResult result = dataSet.getValue().tick(level, this.accelerator, dataSet.getKey(), blocksBeingBroken);
            switch (result) {
                case BROKEN: {
                    this.dirtyData.put(dataSet.getKey(), dataSet.getValue());
                    iter.remove();
                    break;
                }
                case CONTINUE: {
                    this.dirtyData.put(dataSet.getKey(), dataSet.getValue());
                }
            }
        }
        this.accelerator.clearCache();
        if (level.getGameTime() % 20L == 0L) {
            soundCount = 0;
        }
        if (!this.dirtyData.isEmpty()) {
            this.bulkUpdateDestructionProgress(level);
            this.dirtyData.clear();
        }
    }

    private void bulkUpdateDestructionProgress(Level level) {
        LevelRenderer levelRenderer = ((ClientLevelAccessor)level).getLevelRenderer();
        ((MultiMiningDestructionExtension)levelRenderer).offroad$manuallyAddMultiDestructionProgress(this.breakingID, this.dirtyData);
    }

    public static class ClientBlockBreakingData {
        public boolean invalid;
        public float destroyProgress;

        public MultiminingDataTickResult tick(Level level, LevelAccelerator accelerator, BlockPos pos, int blocksBeingBroken) {
            BlockState state = accelerator.getBlockState(pos);
            if (!BlockBreakingKineticBlockEntity.isBreakable((BlockState)state, (float)state.getDestroySpeed((BlockGetter)level, pos)) || this.invalid || this.destroyProgress >= 10.0f || this.destroyProgress < 0.0f) {
                this.destroyProgress = -1.0f;
                return MultiminingDataTickResult.BROKEN;
            }
            ClientLevel clientLevel = (ClientLevel)level;
            double radius = 0.5;
            if (accelerator.getBlockState(pos.below()).isAir()) {
                if ((double)level.random.nextFloat() > 0.8) {
                    BlockParticleOption blockBreakingParticles = new BlockParticleOption(ParticleTypes.BLOCK, state);
                    clientLevel.addParticle((ParticleOptions)blockBreakingParticles, (double)pos.getX() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, 0.0, -1.0, 0.0);
                    if ((double)level.random.nextFloat() > 0.8) {
                        clientLevel.addParticle((ParticleOptions)ParticleTypes.ASH, (double)pos.getX() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, 0.0, -1.0, 0.0);
                    }
                }
            } else if (accelerator.getBlockState(pos.above()).isAir() && (double)level.random.nextFloat() > 0.9 && (double)level.random.nextFloat() > 0.5) {
                clientLevel.addParticle((ParticleOptions)ParticleTypes.CRIT, (double)pos.getX() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5 + ((double)level.random.nextFloat() - 0.5) * 2.0 * 0.5, 0.0, 0.0, 0.0);
            }
            double chance = 1.0 - 0.4 / Math.sqrt(Math.pow(blocksBeingBroken, 2.0));
            if (soundCount < 64 && (double)level.random.nextFloat() > chance) {
                ++soundCount;
                clientLevel.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, state.getSoundType().getHitSound(), SoundSource.BLOCKS, 0.4f, 0.1f, false);
            }
            return MultiminingDataTickResult.CONTINUE;
        }
    }
}
