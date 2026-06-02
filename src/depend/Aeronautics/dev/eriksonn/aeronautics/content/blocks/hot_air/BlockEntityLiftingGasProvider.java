/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData
 *  javax.annotation.Nullable
 *  joptsimple.internal.Strings
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air;

import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonBuilder;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonLayerGraph;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.SavedBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface BlockEntityLiftingGasProvider {
    public static double getPredictedVolume(ClientBalloonInfo info, int ticksSinceSync) {
        double volumeInterp = info.clientBalloonFilled + info.clientBalloonChange * (double)ticksSinceSync;
        if (Math.abs(info.clientBalloonFilled - info.clientBalloonTarget - info.clientBalloonChange) > 0.01) {
            double r = (info.clientBalloonFilled - info.clientBalloonTarget) / (info.clientBalloonFilled - info.clientBalloonTarget - info.clientBalloonChange);
            volumeInterp = Math.pow(r, ticksSinceSync) * (info.clientBalloonFilled - info.clientBalloonTarget) + info.clientBalloonTarget;
        }
        return volumeInterp;
    }

    public static MutableComponent barComponent(int amount, int target, int total) {
        int lower = Math.min(amount, target - 1);
        int upper = Math.max(amount - target, 0);
        return Component.empty().append((Component)BlockEntityLiftingGasProvider.bars(Math.max(0, lower), ChatFormatting.DARK_AQUA)).append((Component)BlockEntityLiftingGasProvider.bars(Math.max(0, target - lower - 1), ChatFormatting.DARK_GRAY)).append((Component)BlockEntityLiftingGasProvider.bars(target == 0 ? 0 : 1, ChatFormatting.GOLD)).append((Component)BlockEntityLiftingGasProvider.bars(upper, ChatFormatting.DARK_AQUA)).append((Component)BlockEntityLiftingGasProvider.bars(Math.max(0, total - target - upper), ChatFormatting.DARK_GRAY));
    }

    private static MutableComponent bars(int count, ChatFormatting format) {
        return Component.literal((String)Strings.repeat((char)'|', (int)count)).withStyle(format);
    }

    default public BlockPos getRaycastedPosition(Level level, Vec3 rayStart, Vec3 rayEnd) {
        BlockHitResult clip = level.clip(new ClipContext(rayStart, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        BlockPos hitBlockPos = clip.getBlockPos();
        if (clip.getType() == HitResult.Type.MISS || !level.getBlockState(hitBlockPos).is(AeroTags.BlockTags.AIRTIGHT)) {
            return null;
        }
        return hitBlockPos.relative(clip.getDirection());
    }

    public Balloon getBalloon();

    public void setBalloon(Balloon var1);

    default public void tryJoinBalloon() {
        Balloon existingBalloon;
        if (this.getBalloon() != null) {
            return;
        }
        BlockPos castPos = this.getCastPosition();
        if (castPos != null && (existingBalloon = ((BalloonMap)BalloonMap.MAP.get((LevelAccessor)this.getLevel())).getBalloon(castPos)) != null) {
            existingBalloon.addHeater(this);
            this.setBalloon(existingBalloon);
        }
    }

    default public void tryCreateBalloon() {
        if (this.getBalloon() != null) {
            return;
        }
        Level level = this.getLevel();
        BlockPos castPos = this.getCastPosition();
        BalloonMap balloonMap = (BalloonMap)BalloonMap.MAP.get((LevelAccessor)level);
        if (castPos == null) {
            return;
        }
        Balloon newBalloon = BalloonBuilder.attemptBuildBalloon(this, castPos);
        if (newBalloon == null) {
            return;
        }
        if (newBalloon instanceof ServerBalloon) {
            ServerBalloon serverBalloon = (ServerBalloon)newBalloon;
            Collection<SavedBalloon> unloadedBalloons = balloonMap.getUnloadedBalloons();
            Iterator iter = unloadedBalloons.iterator();
            while (iter.hasNext()) {
                SavedBalloon unloaded = (SavedBalloon)iter.next();
                BalloonLayerGraph graph = newBalloon.getGraph();
                if (!graph.hasBlockAt(unloaded.controllerPos())) continue;
                serverBalloon.loadFrom(unloaded);
                balloonMap.markDirty();
                iter.remove();
                break;
            }
        }
        this.setBalloon(newBalloon);
        balloonMap.addBalloon(newBalloon);
    }

    default public void removeFromBalloon() {
        Balloon balloon = this.getBalloon();
        if (balloon instanceof ServerBalloon) {
            ServerBalloon serverBalloon = (ServerBalloon)balloon;
            balloon.removeHeater(this);
            if (this.isChunkUnloaded() && balloon.getHeaters().isEmpty()) {
                Level level = this.getLevel();
                if (!1.$assertionsDisabled && level == null) {
                    throw new AssertionError();
                }
                ((BalloonMap)BalloonMap.MAP.get((LevelAccessor)level)).unloadBalloon(serverBalloon);
            }
            this.setBalloon(null);
        }
    }

    default public void addBalloonGoggleInformation(List<Component> tooltip, ClientBalloonInfo info, int ticksSinceSync, double airPressure) {
        if (info != null) {
            int totalVolume = info.clientBalloonVolume;
            if (totalVolume == 0) {
                AeroLang.translate("lifting_gas.no_suitable_balloon", new Object[0]).style(ChatFormatting.RED).forGoggles(tooltip, 2);
                return;
            }
            MutableComponent gasOutputComponent = AeroLang.translate("unit.meter_cubed", String.format("%.2f", this.getGasOutput())).style(ChatFormatting.AQUA).component();
            AeroLang.translate("lifting_gas.gas_output", this.getLiftingGasType().getName(), gasOutputComponent).style(ChatFormatting.GRAY).forGoggles(tooltip, 2);
            AeroLang.emptyLine(tooltip);
            AeroLang.translate("lifting_gas.balloon", new Object[0]).forGoggles(tooltip, 1);
            int totalBar = 30;
            int targetBar = (int)Math.ceil(30.0 * info.clientBalloonTarget / (double)totalVolume);
            double volumeInterp = BlockEntityLiftingGasProvider.getPredictedVolume(info, ticksSinceSync);
            int volumeBar = Mth.clamp((int)((int)Math.ceil(30.0 * volumeInterp / (double)totalVolume)), (int)0, (int)30);
            MutableComponent base = BlockEntityLiftingGasProvider.barComponent(volumeBar, targetBar, 30);
            AeroLang.translate("lifting_gas.fill", base).style(ChatFormatting.GRAY).forGoggles(tooltip, 2);
            double lift = info.clientBalloonLift * airPressure;
            if (info.clientBalloonFilled > 0.01) {
                lift *= volumeInterp / info.clientBalloonFilled;
            }
            MutableComponent liftComponent = AeroLang.kilopixelGram(lift).style(ChatFormatting.AQUA).component();
            AeroLang.translate("lifting_gas.total_lift", liftComponent).style(ChatFormatting.GRAY).forGoggles(tooltip, 2);
            MutableComponent balloonVolumeComponent = AeroLang.translate("unit.meter_cubed", totalVolume).style(ChatFormatting.AQUA).component();
            AeroLang.translate("lifting_gas.balloon_volume", balloonVolumeComponent).style(ChatFormatting.GRAY).forGoggles(tooltip, 2);
        }
    }

    default public double getAirPressure(ClientBalloonInfo balloonInfo, Level level) {
        Vector3d globalPosition = Sable.HELPER.projectOutOfSubLevel(level, JOMLConversion.toJOML((Position)balloonInfo.gasCenter()));
        return DimensionPhysicsData.getAirPressure((Level)level, (Vector3dc)globalPosition);
    }

    @Nullable
    public BlockPos getCastPosition();

    @Nullable
    public void doRaycast();

    public double getGasOutput();

    public LiftingGasType getLiftingGasType();

    public boolean canOutputGas();

    public double getClientPredictedVolume();

    public BlockPos getBlockPos();

    public Level getLevel();

    public boolean isChunkUnloaded();

    default public void tickBalloonLogic() {
        ServerBalloon balloon;
        Balloon balloon2;
        this.doRaycast();
        if (this.getBalloon() == null) {
            this.tryJoinBalloon();
        }
        if (this.getBalloon() == null) {
            this.tryCreateBalloon();
        }
        if ((balloon2 = this.getBalloon()) instanceof ServerBalloon && (balloon = (ServerBalloon)balloon2).getTotalFilledVolume() > 1.0) {
            AeroAdvancements.HEAD_IN_THE_CLOUDS.awardToNearby(this.getBlockPos(), this.getLevel());
        }
    }

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }

    public record ClientBalloonInfo(int clientBalloonVolume, double clientBalloonFilled, double clientBalloonTarget, double clientBalloonLift, double clientBalloonChange, Vec3 gasCenter) {
        public static void writeToNBT(CompoundTag tag, ServerBalloon balloon) {
            if (balloon != null && balloon.getCenter() != null) {
                tag.putInt("Volume", balloon.getCapacity());
                tag.putDouble("Filled", balloon.getTotalFilledVolume());
                tag.putDouble("Target", balloon.getTotalTargetVolume());
                tag.putDouble("Delta", balloon.getTotalVolumeChange());
                tag.putDouble("Lift", balloon.getTotalLift());
                tag.putDouble("CenterX", balloon.getCenter().x);
                tag.putDouble("CenterY", balloon.getCenter().y);
                tag.putDouble("CenterZ", balloon.getCenter().z);
            }
        }

        public static ClientBalloonInfo readFromNBT(CompoundTag tag) {
            return new ClientBalloonInfo(tag.getInt("Volume"), tag.getDouble("Filled"), tag.getDouble("Target"), tag.getDouble("Lift"), tag.getDouble("Delta"), new Vec3(tag.getDouble("CenterX"), tag.getDouble("CenterY"), tag.getDouble("CenterZ")));
        }
    }
}
