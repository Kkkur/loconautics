/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.Create
 *  com.simibubi.create.content.contraptions.IControlContraption$MovementMode
 *  com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity$RotationDirection
 *  com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.lang.LangBuilder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CakeBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.portable_engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlock;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineInventory;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.service.SimItemService;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;

public class PortableEngineBlockEntity
extends GeneratingKineticBlockEntity
implements Clearable {
    public static int INFINITE_THRESHOLD = 51840000;
    public PortableEngineInventory inventory;
    private int burnTime = 0;
    private boolean superHeated = false;
    protected float generatedSpeed;
    protected ScrollOptionBehaviour<IControlContraption.MovementMode> movementDirection;
    protected float clientAngle;
    public float lastHatchOpenTime = 0.0f;
    public float hatchOpenTime = 0.0f;
    protected boolean eatingCake = false;
    protected LerpedFloat visualSpeed = LerpedFloat.linear();
    protected LerpedFloat visualStrength = LerpedFloat.linear();
    public boolean openHatchOverride;

    public PortableEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.inventory = new PortableEngineInventory(this);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.movementDirection = new ScrollOptionBehaviour(WindmillBearingBlockEntity.RotationDirection.class, (Component)Component.translatable((String)"create.contraptions.windmill.rotation_direction"), (SmartBlockEntity)this, (ValueBoxTransform)new PortableEngineValueBoxTransform());
        this.movementDirection.withCallback(t -> this.onDirectionChanged());
        behaviours.add((BlockEntityBehaviour)this.movementDirection);
        super.addBehaviours(behaviours);
    }

    public void clearContent() {
        this.inventory.clearContent();
    }

    private void onDirectionChanged() {
        if (!this.level.isClientSide) {
            this.updateGeneratedRotation();
        }
    }

    protected static BlockPos getCameraPos() {
        Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
        if (renderViewEntity == null) {
            return BlockPos.ZERO;
        }
        BlockPos playerLocation = renderViewEntity.blockPosition();
        return playerLocation;
    }

    public float getGeneratedSpeed() {
        return PortableEngineBlockEntity.convertToDirection((float)(this.generatedSpeed * (float)(this.movementDirection.getValue() > 0 ? -1 : 1)), (Direction)((Direction)this.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING))) * (float)(this.superHeated ? 2 : 1);
    }

    public void tick() {
        float newSpeed;
        boolean isLit;
        super.tick();
        boolean bl = isLit = this.burnTime > 0;
        if (this.level.isClientSide) {
            float targetSpeed = this.isVirtual() ? this.speed : this.getGeneratedSpeed();
            this.visualSpeed.updateChaseTarget(targetSpeed);
            this.visualSpeed.tickChaser();
            float heatTarget = isLit ? 1.0f : 0.0f;
            float heatSpeed = 0.02f;
            if (this.visualStrength.getValue() > heatTarget) {
                heatSpeed = 0.1f;
            }
            this.visualStrength.chase((double)heatTarget, (double)heatSpeed, LerpedFloat.Chaser.EXP);
            this.visualStrength.tickChaser();
            float s = this.visualSpeed.getValue() * 3.0f / 10.0f;
            float soundAngle = Math.abs(this.clientAngle % 90.0f) - 45.0f;
            if (soundAngle > 0.0f && soundAngle < Math.abs(s)) {
                double pRand = this.level.getRandom().nextDouble();
                double distSq = Sable.HELPER.distanceSquaredWithSubLevels(this.level, (Vector3dc)JOMLConversion.atCenterOf((Vec3i)PortableEngineBlockEntity.getCameraPos()), (Vector3dc)JOMLConversion.atCenterOf((Vec3i)this.worldPosition));
                double dist = Math.sqrt(distSq);
                double ratio = 1.0 - dist / 8.0;
                if (ratio > 0.0) {
                    SimSoundEvents.PORTABLE_ENGINE_PUFF.playAt(this.level, (Vec3i)this.worldPosition, (float)ratio, 1.0f, false);
                }
                if (pRand < 0.05) {
                    SimSoundEvents.PORTABLE_ENGINE_AMBIENT.playAt(this.level, (Vec3i)this.worldPosition, 0.8f, 1.0f, false);
                }
            }
            this.clientAngle += s;
            this.clientAngle %= 360.0f;
            if (isLit && !this.isVirtual()) {
                this.spawnParticles();
            }
            this.updateHatchTime();
        }
        if (this.isVirtual()) {
            return;
        }
        if (this.getGeneratedSpeed() != 0.0f && this.getSpeed() == 0.0f) {
            this.updateGeneratedRotation();
        }
        ContainerSlot slot = this.inventory.slot;
        ItemStack stack = slot.getStack();
        boolean previousSuperHeated = false;
        if (this.burnTime > 0 && !this.isCurrentFuelInfinite()) {
            --this.burnTime;
        }
        if (this.burnTime <= 0 && !this.inventory.isEmpty()) {
            this.burnTime = SimItemService.INSTANCE.getBurnTime(stack);
            this.superHeated = this.getNextSuperHeated();
            if (this.burnTime > 0) {
                if (stack.getCount() == 1 && stack.getItem().hasCraftingRemainingItem()) {
                    slot.setStack(slot.getType().getCraftingRemainingItem().getDefaultInstance());
                } else {
                    slot.shrink(1L);
                }
            }
        }
        if (this.burnTime <= 0) {
            this.superHeated = false;
        }
        boolean isLitState = PortableEngineBlock.isLitState(this.getBlockState());
        int generatedSpeed = 32;
        if (this.generatedSpeed == 0.0f && isLit && (double)this.getSpeed() != 0.0 && Mth.sign((double)(newSpeed = PortableEngineBlockEntity.convertToDirection((float)(this.movementDirection.getValue() > 0 ? -1 : 1), (Direction)((Direction)this.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING))))) != Mth.sign((double)this.getSpeed())) {
            this.generatedSpeed = isLit ? 32.0f : 0.0f;
            IControlContraption.MovementMode[] directions = IControlContraption.MovementMode.values();
            IControlContraption.MovementMode existingValue = directions[this.movementDirection.getValue()];
            this.movementDirection.setValue((existingValue.ordinal() + 1) % directions.length);
            this.updateGeneratedRotation();
        }
        float f = this.generatedSpeed = isLit ? 32.0f : 0.0f;
        if (isLitState && !isLit) {
            this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)BlockStateProperties.LIT, (Comparable)Boolean.valueOf(false)), 2);
            this.updateGeneratedRotation();
        }
        if (!isLitState && isLit) {
            this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)BlockStateProperties.LIT, (Comparable)Boolean.valueOf(true)), 2);
            this.level.playSound(null, this.worldPosition, SimSoundEvents.PORTABLE_ENGINE_ROARS.event(), SoundSource.BLOCKS, 0.125f + this.level.random.nextFloat() * 0.125f, 0.75f - this.level.random.nextFloat() * 0.25f);
            Vec3 pos = VecHelper.getCenterOf((Vec3i)this.worldPosition);
            Direction direction = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
            Vec3i N = direction.getNormal();
            Vec3 N2 = new Vec3((double)N.getX(), (double)N.getY(), (double)N.getZ());
            pos = pos.add((double)(-N.getX()) * 0.53, -0.1, (double)(-N.getZ()) * 0.53);
            Vec3 speed = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.01f).add(N2.scale(-0.03));
            for (int i = 0; i < 2; ++i) {
                Vec3 random = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.1f);
                random = random.subtract(N2.scale(random.dot(N2)));
                pos = pos.add(random);
                this.level.addParticle((ParticleOptions)ParticleTypes.FLAME, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
            }
            this.updateGeneratedRotation();
        }
        if (this.superHeated && isLitState && isLit) {
            this.updateGeneratedRotation();
        }
        if (!this.level.isClientSide()) {
            if (this.eatingCake) {
                this.eatingCake = false;
                this.sendData();
            }
            Direction direction = ((Direction)this.getBlockState().getValue(PortableEngineBlock.HORIZONTAL_FACING)).getOpposite();
            BlockPos front = this.getBlockPos().relative(direction);
            long time = this.level.getGameTime() % 60L;
            if (time == 0L && this.level.getBlockState(front).is(Blocks.CAKE)) {
                this.eatingCake = true;
                this.sendData();
                BlockState state = this.level.getBlockState(front);
                if ((Integer)state.getValue((Property)CakeBlock.BITES) < 6) {
                    this.level.setBlock(front, (BlockState)state.cycle((Property)CakeBlock.BITES), 2);
                } else {
                    this.level.removeBlock(front, false);
                }
                this.burnTime += 100;
                this.level.playSound(null, this.getBlockPos(), SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    public float getHatchOpenTime(float partialTicks) {
        return Mth.lerp((float)partialTicks, (float)this.lastHatchOpenTime, (float)this.hatchOpenTime);
    }

    private void updateHatchTime() {
        int dir;
        Player player;
        boolean openHatch = false;
        BlockPos pos = this.getBlockPos();
        Vec3 center = pos.getCenter();
        List players = this.level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(7.0));
        Iterator iterator = players.iterator();
        while (!(!iterator.hasNext() || Sable.HELPER.distanceSquaredWithSubLevels(this.level, (Position)(player = (Player)iterator.next()).getEyePosition(), (Position)center) < Mth.square((double)(player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 0.7)) && (openHatch = this.canOpenHatch(player)))) {
        }
        float speed = 1.35f;
        int n = dir = (openHatch |= this.openHatchOverride) ? 1 : -1;
        if (this.eatingCake) {
            dir = -dir * 5;
        }
        this.lastHatchOpenTime = this.hatchOpenTime;
        this.hatchOpenTime = Math.clamp(this.hatchOpenTime + (float)dir * 1.35f, 0.0f, 10.0f);
    }

    private boolean canOpenHatch(Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return this.inventory.insertGeneral(ItemInfoWrapper.generateFromStack(heldItem), heldItem.getCount(), true) > 0;
    }

    public void spawnParticles() {
        Vec3 hatchPos = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        Direction direction = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        Vec3i facingDirI = direction.getNormal();
        Vec3 facingDir = new Vec3((double)facingDirI.getX(), (double)facingDirI.getY(), (double)facingDirI.getZ());
        Vec3 rightDir = facingDir.yRot(1.5707964f);
        hatchPos = hatchPos.add((double)(-facingDirI.getX()) * 0.53, -0.1, (double)(-facingDirI.getZ()) * 0.53);
        if ((double)Create.RANDOM.nextFloat() < 0.12) {
            Vec3 random = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.15f);
            random = random.subtract(facingDir.scale(random.dot(facingDir)));
            hatchPos = hatchPos.add(random);
            SimpleParticleType particle = this.isSuperHeated() && (double)Create.RANDOM.nextFloat() < 0.3 ? ParticleTypes.FLAME : ParticleTypes.SMOKE;
            Vec3 vec3 = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.01f);
        }
        for (int i = -1; i < 2; i += 2) {
            if (!((double)Create.RANDOM.nextFloat() < 0.25)) continue;
            Vec3 random = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.0625f);
            Vec3 pos = Vec3.upFromBottomCenterOf((Vec3i)this.worldPosition, (double)0.6875).add(facingDir.scale(0.5)).add(rightDir.scale(0.5 * (double)i)).add(random);
            Vec3 speed = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.01f);
            this.level.addParticle((ParticleOptions)ParticleTypes.SMOKE, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
        }
        if (this.hatchOpenTime > 0.0f && (double)Create.RANDOM.nextFloat() < 0.08) {
            Vec3 random = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.1f);
            random = random.subtract(facingDir.scale(random.dot(facingDir)));
            hatchPos = hatchPos.add(random);
            this.level.addParticle((ParticleOptions)(this.isSuperHeated() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME), hatchPos.x, hatchPos.y, hatchPos.z, 0.0, 0.0, 0.0);
        }
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putBoolean("SuperHeated", this.superHeated);
        compound.putFloat("GeneratedSpeed", this.generatedSpeed);
        compound.putBoolean("EatingCake", this.eatingCake);
        compound.put("Inventory", (Tag)this.inventory.write(registries));
        compound.putInt("BurnTime", this.burnTime);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.superHeated = compound.getBoolean("SuperHeated");
        this.inventory.read(registries, compound.getCompound("Inventory"));
        this.burnTime = compound.getInt("BurnTime");
        this.generatedSpeed = compound.getFloat("GeneratedSpeed");
        this.eatingCake = compound.getBoolean("EatingCake");
        if (clientPacket || this.isVirtual()) {
            this.visualSpeed.chase((double)this.getGeneratedSpeed(), 0.125, LerpedFloat.Chaser.EXP);
        }
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        SimLang.translate("portable_engine.tooltip_name", new Object[0]).text(":").forGoggles(tooltip);
        ItemStack currentStack = this.inventory.slot.getStack();
        boolean hasByProduct = !currentStack.isEmpty() && SimItemService.INSTANCE.getBurnTime(currentStack) == 0;
        LangBuilder noFuel = SimLang.translate("portable_engine.none", new Object[0]).style(ChatFormatting.RED);
        LangBuilder stackName = SimLang.builder().add(currentStack.getHoverName()).text(" x" + currentStack.getCount()).style(ChatFormatting.GREEN);
        if (!this.isCurrentFuelInfinite()) {
            String langKey = hasByProduct ? "byproduct" : "fuel";
            SimLang.translate("portable_engine." + langKey, currentStack.isEmpty() ? noFuel : stackName).style(ChatFormatting.GRAY).forGoggles(tooltip);
        }
        if (this.burnTime > 0) {
            int seconds = this.getCurrentBurnTime() / 20;
            int secondsTotal = this.getTotalBurnTime() / 20;
            LangBuilder infiniteLang = SimLang.translate("portable_engine.infinite", new Object[0]).style(ChatFormatting.LIGHT_PURPLE);
            LangBuilder timeLang = SimLang.text(this.getTime(secondsTotal)).style(this.isSuperHeated() ? ChatFormatting.GOLD : ChatFormatting.AQUA);
            SimLang.translate("portable_engine.time", this.isTotalFuelInfinite() ? infiniteLang : timeLang).style(ChatFormatting.GRAY).forGoggles(tooltip);
            if (this.superHeated) {
                if (this.isCurrentFuelInfinite()) {
                    SimLang.translate("portable_engine.superheated", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
                } else {
                    SimLang.translate("portable_engine.superheated_time", this.getTime(this.getNextSuperHeated() ? secondsTotal : seconds)).style(ChatFormatting.GOLD).forGoggles(tooltip);
                }
            }
        }
        return true;
    }

    private String getTime(int sec) {
        Object s = "";
        int min = sec / 60;
        int hour = min / 60;
        sec = Math.floorMod(sec, 60);
        min = Math.floorMod(min, 60);
        if (hour > 0) {
            s = (String)s + hour + "h ";
        }
        if (min < 10 && hour > 0) {
            s = (String)s + "0";
        }
        if (min > 0 || hour > 0) {
            s = (String)s + min + "m ";
        }
        if (sec < 10 && min > 0) {
            s = (String)s + "0";
        }
        s = (String)s + sec + "s";
        return s;
    }

    public boolean isCurrentFuelInfinite() {
        return this.burnTime >= INFINITE_THRESHOLD;
    }

    public boolean isTotalFuelInfinite() {
        return this.getNextBurnTime() >= INFINITE_THRESHOLD || this.isCurrentFuelInfinite();
    }

    public int getCurrentBurnTime() {
        return this.burnTime;
    }

    public void setCurrentBurnTime(int value) {
        this.burnTime = value;
    }

    public int getTotalBurnTime() {
        return this.getCurrentBurnTime() + this.inventory.slot.getStack().getCount() * this.getNextBurnTime();
    }

    private int getNextBurnTime() {
        return SimItemService.INSTANCE.getBurnTime(this.inventory.slot.getStack());
    }

    public boolean isSuperHeated() {
        return this.superHeated;
    }

    public void setSuperHeated(boolean value) {
        this.superHeated = value;
    }

    private boolean getNextSuperHeated() {
        return SimItemService.INSTANCE.getSuperheatedBurnTime(this.inventory.slot.getStack()) > 0;
    }

    private static class PortableEngineValueBoxTransform
    extends ValueBoxTransform {
        private PortableEngineValueBoxTransform() {
        }

        public Vec3 getLocalOffset(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
            Direction facing = (Direction)blockState.getValue(PortableEngineBlock.HORIZONTAL_FACING);
            float yRot = AngleHelper.horizontalAngle((Direction)facing);
            return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)13.5, (double)7.4f), (double)yRot, (Direction.Axis)Direction.Axis.Y);
        }

        public void rotate(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, PoseStack poseStack) {
            float yRot = AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)));
            ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).rotateYDegrees(yRot)).rotateXDegrees(90.0f)).translate(0.0, 0.1, 0.0);
        }
    }
}
