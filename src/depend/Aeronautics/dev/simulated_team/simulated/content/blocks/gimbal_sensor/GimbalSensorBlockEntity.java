/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Dual
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.gimbal_sensor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlock;
import dev.simulated_team.simulated.data.SimLang;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class GimbalSensorBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    private static final Pose3dc IDENTITY_POSE = new Pose3d(new Vector3d(), new Quaterniond(), new Vector3d(), new Vector3d(1.0));
    private static final double MAX_ANGLE_X = Math.toRadians(90.0);
    private static final double MAX_ANGLE_Z = Math.toRadians(90.0);
    private final EnumMap<Direction, Integer> redstoneMap;
    private final Vector3d previousAngles = new Vector3d(0.0, 0.0, 0.0);
    private final Vector3d angleInertia = new Vector3d(110.0, 110.0, 34.0);
    private final Vector3d angleDamping = new Vector3d(0.2, 0.2, 0.2);
    public boolean updateVisualRotation = true;
    CompassTarget compassTarget = new CompassTarget();
    private GimbalSensorScrollValueBehaviour axisBehaviour;
    private Vector3d eulerAngles = new Vector3d(0.0, 0.0, 0.0);
    private Vector3d angleVelocities = new Vector3d(0.0, 0.0, 0.0);
    private Quaterniond lastShellOrientation = null;
    private double ZAngle;
    private double XAngle;

    public GimbalSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.redstoneMap = new EnumMap(Direction.class);
        for (Direction dir : Iterate.directions) {
            this.redstoneMap.put(dir, 0);
        }
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.axisBehaviour = new GimbalSensorScrollValueBehaviour(this);
        behaviours.add((BlockEntityBehaviour)this.axisBehaviour.between(-90, 90));
        this.axisBehaviour.primaryValue = 45;
        this.axisBehaviour.secondaryValue = 45;
    }

    public void initialize() {
        super.initialize();
        this.randomNudge();
    }

    public void tick() {
        Pose3dc pose;
        super.tick();
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        Object object = pose = subLevel != null ? subLevel.logicalPose() : IDENTITY_POSE;
        if (this.level.isClientSide) {
            this.animateClientRotation(subLevel, pose);
        }
        if (subLevel == null) {
            return;
        }
        Vector3d ld = JOMLConversion.toJOML((Position)Vec3.atLowerCornerOf((Vec3i)Direction.DOWN.getNormal()));
        subLevel.logicalPose().orientation().transformInverse(ld);
        this.XAngle = ld.y() < 0.0 || ld.z() * ld.z() > 0.001 ? Math.atan2(ld.z(), -ld.y()) : 0.0;
        this.ZAngle = ld.y() < 0.0 || ld.x() * ld.x() > 0.001 ? Math.atan2(ld.x(), -ld.y()) : 0.0;
        this.setPower(this.ZAngle, Direction.EAST);
        this.setPower(-this.ZAngle, Direction.WEST);
        this.setPower(this.XAngle, Direction.SOUTH);
        this.setPower(-this.XAngle, Direction.NORTH);
    }

    public void randomNudge() {
        Vec3 v = VecHelper.offsetRandomly((Vec3)new Vec3(0.0, 0.0, 0.0), (RandomSource)this.level.random, (float)0.2f);
        this.angleVelocities.set(v.x, v.y, v.z);
        this.eulerAngles.set(0.0, 0.0, (double)this.level.random.nextFloat() * Math.PI * 2.0);
    }

    void animateClientRotation(SubLevel subLevel, Pose3dc pose) {
        this.previousAngles.set((Vector3dc)this.eulerAngles);
        Vector3d shellVelocity = this.getShellVelocity(subLevel);
        Vector3d acceleration = new Vector3d();
        if (this.updateVisualRotation) {
            this.addGravityTorque(pose, acceleration);
        }
        Vec3 globalPosition = pose.transformPosition(Vec3.atCenterOf((Vec3i)this.getBlockPos()));
        this.compassTarget.update(globalPosition, this.level);
        Vector3d target = new Vector3d();
        this.compassTarget.getTarget(target);
        this.addCompassTorque(pose, acceleration, target);
        if (this.compassTarget.isRandom()) {
            acceleration.z += (double)(2.0f * this.level.random.nextFloat() - 1.0f) * 2.1;
        }
        acceleration.div(this.angleInertia);
        Vector3d relativeVelocity = this.angleVelocities.add((Vector3dc)shellVelocity, new Vector3d());
        Vector3d currentDamping = relativeVelocity.mul((Vector3dc)this.angleDamping);
        this.angleVelocities.add((Vector3dc)acceleration).sub((Vector3dc)currentDamping);
        Vector3d totalVelocity = this.angleVelocities.add((Vector3dc)shellVelocity, new Vector3d());
        this.eulerAngles.add((Vector3dc)totalVelocity);
        this.collide(this.eulerAngles, totalVelocity, 1, Math.abs(Math.toRadians(this.axisBehaviour.primaryValue)));
        this.collide(this.eulerAngles, totalVelocity, 0, Math.abs(Math.toRadians(this.axisBehaviour.secondaryValue)));
        totalVelocity.sub((Vector3dc)shellVelocity, this.angleVelocities);
    }

    void addGravityTorque(Pose3dc pose, Vector3d torque) {
        Vector3d globalPosition = Sable.HELPER.projectOutOfSubLevel(this.level, JOMLConversion.atCenterOf((Vec3i)this.getBlockPos()));
        Vector3d localGravity = new Vector3d((Vector3dc)DimensionPhysicsData.getGravity((Level)this.level, (Vector3dc)globalPosition));
        this.transformBaseInverse(localGravity, pose);
        this.transformPrimaryInverse(localGravity);
        Vector3d localDown = new Vector3d(0.0, -1.0, 0.0).rotateX(this.eulerAngles.y);
        Vector3d localTorque = localDown.cross((Vector3dc)localGravity);
        torque.x += localTorque.z;
        torque.y += localTorque.x;
    }

    void addCompassTorque(Pose3dc pose, Vector3d torque, Vector3d target) {
        this.transformBaseInverse(target, pose);
        this.transformPrimaryInverse(target);
        this.transformSecondaryInverse(target);
        this.transformCompassInverse(target);
        Vector3d localTorque = new Vector3d(0.0, 0.0, -1.0).cross((Vector3dc)target);
        torque.z += localTorque.y;
    }

    private Vector3d getShellVelocity(SubLevel subLevel) {
        Vector3d shellVelocity = new Vector3d();
        if (subLevel != null) {
            Pose3d pose = subLevel.logicalPose();
            if (this.lastShellOrientation == null) {
                this.lastShellOrientation = new Quaterniond((Quaterniondc)pose.orientation());
            } else {
                Quaterniond rotationDiff = this.lastShellOrientation.div((Quaterniondc)pose.orientation(), new Quaterniond());
                Vector3d angularVelocity = new Vector3d(rotationDiff.x, rotationDiff.y, rotationDiff.z).mul(2.0);
                this.transformBaseInverse(angularVelocity, (Pose3dc)pose);
                shellVelocity.x = angularVelocity.z;
                this.transformPrimaryInverse(angularVelocity);
                shellVelocity.y = angularVelocity.x;
                this.transformSecondaryInverse(angularVelocity);
                shellVelocity.z = angularVelocity.y;
                this.lastShellOrientation.set((Quaterniondc)pose.orientation());
            }
        } else {
            this.lastShellOrientation = null;
        }
        return shellVelocity;
    }

    private void collide(Vector3d position, Vector3d velocity, int index, double limit) {
        double p = position.get(index);
        double v = velocity.get(index);
        double m = p > 0.0 ? 1.0 : -1.0;
        p *= m;
        v *= m;
        if (p >= limit) {
            p = limit;
            if (v > 0.0) {
                v *= -0.9;
            }
        }
        position.setComponent(index, p * m);
        velocity.setComponent(index, v * m);
    }

    private void setPower(double angle, Direction dir) {
        int newPower;
        boolean alongPrimary = dir.getAxis() == this.getBlockState().getValue(GimbalSensorBlock.HORIZONTAL_AXIS);
        float angleLimit = alongPrimary ? (float)this.axisBehaviour.primaryValue : (float)this.axisBehaviour.secondaryValue;
        int n = newPower = angleLimit == 0.0f ? 0 : Math.max(Math.min((int)(14.5 * angle / Math.toRadians(angleLimit) + 0.5), 15), 0);
        if (this.redstoneMap.get(dir) != newPower) {
            this.redstoneMap.put(dir, newPower);
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
            this.level.updateNeighborsAt(this.worldPosition.relative(dir), this.getBlockState().getBlock());
        }
    }

    public Quaternionf getBaseQuaternion() {
        Quaternionf Q = new Quaternionf();
        float angle = Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)this.getBlockState().getValue(GimbalSensorBlock.HORIZONTAL_AXIS)), (Direction.AxisDirection)Direction.AxisDirection.POSITIVE).toYRot();
        Q.rotateY((float)Math.toRadians(angle));
        return Q;
    }

    public Quaternionf applyPrimaryQuaternion(Quaternionf Q, float partialTick) {
        Q.rotateZ(this.lerp((float)this.previousAngles.x, (float)this.eulerAngles.x, partialTick));
        return Q;
    }

    public Quaternionf applySecondaryQuaternion(Quaternionf Q, float partialTick) {
        Q.rotateX(this.lerp((float)this.previousAngles.y, (float)this.eulerAngles.y, partialTick));
        return Q;
    }

    public Quaternionf applyCompassQuaternion(Quaternionf Q, float partialTick) {
        Q.rotateY(this.lerp((float)this.previousAngles.z, (float)this.eulerAngles.z, partialTick));
        return Q;
    }

    private Vector3d transformBaseInverse(Vector3d v, Pose3dc ctx) {
        float angle = Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)this.getBlockState().getValue(GimbalSensorBlock.HORIZONTAL_AXIS)), (Direction.AxisDirection)Direction.AxisDirection.POSITIVE).toYRot();
        ctx.orientation().transformInverse(v);
        v.rotateY(-Math.toRadians(angle));
        return v;
    }

    private Vector3d transformPrimaryInverse(Vector3d v) {
        v.rotateZ(-this.eulerAngles.x);
        return v;
    }

    private Vector3d transformSecondaryInverse(Vector3d v) {
        v.rotateX(-this.eulerAngles.y);
        return v;
    }

    private Vector3d transformCompassInverse(Vector3d v) {
        v.rotateY(-this.eulerAngles.z);
        return v;
    }

    float lerp(float a, float b, float t) {
        return a * (1.0f - t) + b * t;
    }

    public int getPower(Direction dir) {
        return this.redstoneMap.get(dir);
    }

    public double getZAngle() {
        return this.ZAngle;
    }

    public double getXAngle() {
        return this.XAngle;
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        CompoundTag powers = new CompoundTag();
        for (Map.Entry<Direction, Integer> entry : this.redstoneMap.entrySet()) {
            powers.putInt(entry.getKey().getName(), entry.getValue().intValue());
        }
        if (!clientPacket) {
            tag.putFloat("Angle1", (float)this.eulerAngles.x);
            tag.putFloat("Angle2", (float)this.eulerAngles.y);
            tag.putFloat("Angle3", (float)this.eulerAngles.z);
            tag.putFloat("Vel1", (float)this.angleVelocities.x);
            tag.putFloat("Vel2", (float)this.angleVelocities.y);
            tag.putFloat("Vel3", (float)this.angleVelocities.z);
        }
        tag.putDouble("x_angle", this.XAngle);
        tag.putDouble("zz_angle", this.ZAngle);
        tag.put("Powers", (Tag)powers);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("Powers")) {
            CompoundTag powers = (CompoundTag)tag.get("Powers");
            for (Map.Entry<Direction, Integer> entry : this.redstoneMap.entrySet()) {
                entry.setValue(powers.getInt(entry.getKey().getName()));
            }
        }
        if (!clientPacket) {
            float x = tag.getFloat("Angle1");
            float y = tag.getFloat("Angle2");
            float z = tag.getFloat("Angle3");
            this.eulerAngles = new Vector3d((double)x, (double)y, (double)z);
            x = tag.getFloat("Vel1");
            y = tag.getFloat("Vel2");
            z = tag.getFloat("Vel3");
            this.angleVelocities = new Vector3d((double)x, (double)y, (double)z);
        }
        this.XAngle = tag.getDouble("x_angle");
        this.ZAngle = tag.getDouble("z_angle");
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        MutableComponent x = SimLang.text("%.2f".formatted(Math.toDegrees(this.getXAngle()))).style(ChatFormatting.RED).component();
        MutableComponent z = SimLang.text("%.2f".formatted(Math.toDegrees(this.getZAngle()))).style(ChatFormatting.BLUE).component();
        SimLang.blockName(this.getBlockState()).forGoggles(tooltip, 1);
        SimLang.translate("gimbal_sensor.x_angle", x).style(ChatFormatting.GRAY).forGoggles(tooltip, 2);
        SimLang.translate("gimbal_sensor.z_angle", z).style(ChatFormatting.GRAY).forGoggles(tooltip, 2);
        return true;
    }

    static class CompassTarget {
        private final Vector3d target = new Vector3d(0.0, 0.0, 0.0);
        private final Vector3d randomTarget = new Vector3d(0.0, 0.0, 0.0);
        private int randomTargetTimer = 0;
        private double randomTargetLength = 3.0;
        private boolean isRandom = false;

        CompassTarget() {
        }

        public void update(Vec3 pos, Level level) {
            boolean bl = this.isRandom = !level.dimensionType().natural();
            if (!this.isRandom) {
                this.target.set(0.0, 0.0, -1.0);
            } else {
                RandomSource r = level.random;
                if (this.randomTargetTimer-- < 0) {
                    float radius = 1.0f;
                    this.randomTarget.set((double)((r.nextFloat() - 0.5f) * 2.0f * 1.0f), (double)((r.nextFloat() - 0.5f) * 2.0f * 1.0f), (double)((r.nextFloat() - 0.5f) * 2.0f * 1.0f));
                    this.randomTargetTimer = level.random.nextInt(5, 15);
                }
                float nudge = 0.3f;
                this.randomTarget.add((double)((r.nextFloat() - 0.5f) * 2.0f * 0.3f), (double)((r.nextFloat() - 0.5f) * 2.0f * 0.3f), (double)((r.nextFloat() - 0.5f) * 2.0f * 0.3f));
                this.randomTarget.normalize();
                double step = 0.5;
                this.target.mul(0.5).fma(0.5, (Vector3dc)this.randomTarget);
                this.target.normalize();
            }
        }

        public boolean isRandom() {
            return this.isRandom;
        }

        public void setRandomTargetLength(double s) {
            this.randomTargetLength = s;
        }

        public Vector3d getTarget(Vector3d v) {
            return this.target.mul(this.isRandom() ? this.randomTargetLength : 1.0, v);
        }
    }

    public static class GimbalSensorScrollValueBehaviour
    extends ScrollValueBehaviour {
        protected Direction lastSide = Direction.NORTH;
        protected int primaryValue;
        protected int secondaryValue;
        protected Function<Integer, String> formatter = v -> Math.abs(v) + Component.translatable((String)"create.generic.unit.degrees").getString();
        protected int min;
        protected int max;

        public GimbalSensorScrollValueBehaviour(GimbalSensorBlockEntity be) {
            super((Component)Component.translatable((String)"create.kinetics.valve_handle.rotated_angle"), (SmartBlockEntity)be, (ValueBoxTransform)new GimbalSensorValueBox(be));
            this.withFormatter(this.formatter);
            this.primaryValue = 0;
            this.secondaryValue = 0;
        }

        public ScrollValueBehaviour between(int min, int max) {
            this.min = min;
            this.max = max;
            return super.between(min, max);
        }

        public boolean isPrimaryAxis() {
            Direction.Axis blockAxis = (Direction.Axis)this.blockEntity.getBlockState().getValue(GimbalSensorBlock.HORIZONTAL_AXIS);
            return this.lastSide.getAxis().isHorizontal() && this.lastSide.getAxis() != blockAxis;
        }

        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ImmutableList rows = ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD), (Object)Component.literal((String)"\u27f2").withStyle(ChatFormatting.BOLD));
            return new ValueSettingsBoard(this.label, 90, 15, (List)rows, new ValueSettingsFormatter(this::formatValue));
        }

        public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlHeld) {
            int value = Math.max(0, valueSetting.value());
            if (!valueSetting.equals((Object)this.getValueSettings())) {
                this.playFeedbackSound((BlockEntityBehaviour)this);
            }
            this.setValue(valueSetting.row() == 0 ? -value : value);
        }

        public ValueSettingsBehaviour.ValueSettings getValueSettings() {
            return new ValueSettingsBehaviour.ValueSettings(this.getValue() < 0 ? 0 : 1, Math.abs(this.getValue()));
        }

        public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
            return SimLang.number(Math.max(0, Math.abs(settings.value()))).add(Component.translatable((String)"create.generic.unit.degrees")).component();
        }

        public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
            nbt.putInt("ScrollValue1", this.primaryValue);
            nbt.putInt("ScrollValue2", this.secondaryValue);
            super.write(nbt, registries, clientPacket);
        }

        public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
            this.primaryValue = nbt.getInt("ScrollValue1");
            this.secondaryValue = nbt.getInt("ScrollValue2");
            super.read(nbt, registries, clientPacket);
        }

        public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
         @NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
            if (!this.acceptsValueSettings()) {
                return false;
            }
            tag.putInt("ScrollValue1", this.primaryValue);
            tag.putInt("ScrollValue2", this.secondaryValue);
            return true;
        }

        public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
         @NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
            if (!this.acceptsValueSettings()) {
                return false;
            }
            if (!tag.contains("ScrollValue1") || !tag.contains("ScrollValue2")) {
                return true;
            }
            if (simulate) {
                return true;
            }
            this.primaryValue = tag.getInt("ScrollValue1");
            this.secondaryValue = tag.getInt("ScrollValue2");
            this.blockEntity.setChanged();
            this.blockEntity.sendData();
            return true;
        }

        public int getValue() {
            return this.isPrimaryAxis() ? this.primaryValue : this.secondaryValue;
        }

        public void setValue(int value) {
            if ((value = Mth.clamp((int)value, (int)this.min, (int)this.max)) == this.getValue()) {
                return;
            }
            if (this.isPrimaryAxis()) {
                this.primaryValue = value;
            } else {
                this.secondaryValue = value;
            }
            this.blockEntity.setChanged();
            this.blockEntity.sendData();
        }

        public String formatValue() {
            return this.formatter.apply(this.getValue());
        }
    }

    public static class DualGimbalSensorValueBox
    extends ValueBoxTransform.Dual {
        protected Direction direction = Direction.UP;

        public DualGimbalSensorValueBox(boolean first) {
            super(first);
        }

        public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState state) {
            Vec3 location = this.getSouthLocation();
            location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
            location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.verticalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.X);
            return location;
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)16.0);
        }

        public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, PoseStack poseStack) {
            float yRot = AngleHelper.horizontalAngle((Direction)this.getSide()) + 180.0f;
            float xRot = this.getSide() == Direction.UP ? 90.0f : (this.getSide() == Direction.DOWN ? 270.0f : 0.0f);
            ((PoseTransformStack)TransformStack.of((PoseStack)poseStack).rotateYDegrees(yRot)).rotateXDegrees(xRot);
        }

        public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.shouldRender(level, pos, state) && this.isSideActive(state, this.getSide());
        }

        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            boolean a = direction.getAxis() == state.getValue(GimbalSensorBlock.HORIZONTAL_AXIS);
            return direction.getAxis().isHorizontal() && a == this.first;
        }

        public Direction getSide() {
            return Direction.NORTH;
        }
    }

    public static class GimbalSensorValueBox
    extends ValueBoxTransform.Sided {
        GimbalSensorBlockEntity be;

        public GimbalSensorValueBox(GimbalSensorBlockEntity be) {
            this.be = be;
        }

        public ValueBoxTransform.Sided fromSide(Direction direction) {
            this.direction = direction;
            this.be.axisBehaviour.lastSide = direction;
            return this;
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            boolean a = direction.getAxis() == state.getValue(GimbalSensorBlock.HORIZONTAL_AXIS);
            return direction.getAxis().isHorizontal();
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)16.0);
        }

        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
        }
    }
}
