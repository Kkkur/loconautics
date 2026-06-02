/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  joptsimple.internal.Strings
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlockEntity;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.SoundPool;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import joptsimple.internal.Strings;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class BoilerData {
    static final int SAMPLE_RATE = 5;
    private static final int waterSupplyPerLevel = 10;
    private static final float passiveEngineEfficiency = 0.125f;
    int gatheredSupply;
    float[] supplyOverTime = new float[10];
    int ticksUntilNextSample;
    int currentIndex;
    public boolean needsHeatLevelUpdate;
    public boolean passiveHeat;
    public int activeHeat;
    public float waterSupply;
    public int attachedEngines;
    public int attachedWhistles;
    private int maxHeatForSize = 0;
    private int maxHeatForWater = 0;
    private int minValue = 0;
    private int maxValue = 0;
    public boolean[] occludedDirections = new boolean[]{true, true, true, true};
    public LerpedFloat gauge = LerpedFloat.linear();
    private final SoundPool.Sound sound = (level, pos) -> {
        float volume = 3.0f / (float)Math.max(2, this.attachedEngines / 6);
        float pitch = 1.18f - level.random.nextFloat() * 0.25f;
        level.playLocalSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, volume, pitch, false);
        AllSoundEvents.STEAM.playAt(level, pos, volume / 16.0f, 0.8f, false);
    };
    private final EnumMap<Direction, SoundPool> pools = new EnumMap(Direction.class);

    public void tick(FluidTankBlockEntity controller) {
        if (!this.isActive()) {
            return;
        }
        Level level = controller.getLevel();
        if (level.isClientSide) {
            this.pools.values().forEach(p -> p.play(level));
            this.gauge.tickChaser();
            float current = this.gauge.getValue(1.0f);
            if (current > 1.0f && level.random.nextFloat() < 0.5f) {
                this.gauge.setValueNoUpdate((double)(current + Math.min(-(current - 1.0f) * level.random.nextFloat(), 0.0f)));
            }
            return;
        }
        if (this.needsHeatLevelUpdate && this.updateTemperature(controller)) {
            controller.notifyUpdate();
        }
        --this.ticksUntilNextSample;
        if (this.ticksUntilNextSample > 0) {
            return;
        }
        int capacity = controller.tankInventory.getCapacity();
        if (capacity == 0) {
            return;
        }
        this.ticksUntilNextSample = 5;
        this.supplyOverTime[this.currentIndex] = (float)this.gatheredSupply / 5.0f;
        this.waterSupply = Math.max(this.waterSupply, this.supplyOverTime[this.currentIndex]);
        this.currentIndex = (this.currentIndex + 1) % this.supplyOverTime.length;
        this.gatheredSupply = 0;
        if (this.currentIndex == 0) {
            this.waterSupply = 0.0f;
            for (float i : this.supplyOverTime) {
                this.waterSupply = Math.max(i, this.waterSupply);
            }
        }
        if (controller instanceof CreativeFluidTankBlockEntity) {
            this.waterSupply = 200.0f;
        }
        if (this.getActualHeat(controller.getTotalTankSize()) == 18) {
            controller.award(AllAdvancements.STEAM_ENGINE_MAXED);
        }
        controller.notifyUpdate();
    }

    public void updateOcclusion(FluidTankBlockEntity controller) {
        if (!controller.getLevel().isClientSide) {
            return;
        }
        if (this.attachedEngines + this.attachedWhistles == 0) {
            return;
        }
        for (Direction d : Iterate.horizontalDirections) {
            AABB aabb = new AABB(controller.getBlockPos()).move((double)((float)controller.width / 2.0f - 0.5f), 0.0, (double)((float)controller.width / 2.0f - 0.5f)).deflate(0.625);
            aabb = aabb.move((double)((float)d.getStepX() * ((float)controller.width / 2.0f + 0.25f)), 0.0, (double)((float)d.getStepZ() * ((float)controller.width / 2.0f + 0.25f)));
            aabb = aabb.inflate((double)((float)Math.abs(d.getStepZ()) / 2.0f), 0.25, (double)((float)Math.abs(d.getStepX()) / 2.0f));
            this.occludedDirections[d.get2DDataValue()] = !controller.getLevel().noCollision(aabb);
        }
    }

    public void queueSoundOnSide(BlockPos pos, Direction side) {
        SoundPool pool = this.pools.get(side);
        if (pool == null) {
            pool = new SoundPool(4, 2, this.sound);
            this.pools.put(side, pool);
        }
        pool.queueAt(pos);
    }

    public int getTheoreticalHeatLevel() {
        return this.activeHeat;
    }

    public int getMaxHeatLevelForBoilerSize(int boilerSize) {
        return Math.min(18, boilerSize / 4);
    }

    public int getMaxHeatLevelForWaterSupply() {
        return Math.min(18, Mth.ceil((float)this.waterSupply) / 10);
    }

    public boolean isPassive() {
        return this.passiveHeat && this.maxHeatForSize > 0 && this.maxHeatForWater > 0;
    }

    public boolean isPassive(int boilerSize) {
        this.calcMinMaxForSize(boilerSize);
        return this.isPassive();
    }

    public float getEngineEfficiency(int boilerSize) {
        if (this.isPassive(boilerSize)) {
            return 0.125f / (float)this.attachedEngines;
        }
        if (this.activeHeat == 0) {
            return 0.0f;
        }
        int actualHeat = this.getActualHeat(boilerSize);
        return this.attachedEngines <= actualHeat ? 1.0f : (float)actualHeat / (float)this.attachedEngines;
    }

    private int getActualHeat(int boilerSize) {
        int forBoilerSize = this.getMaxHeatLevelForBoilerSize(boilerSize);
        int forWaterSupply = this.getMaxHeatLevelForWaterSupply();
        int actualHeat = Math.min(this.activeHeat, Math.min(forWaterSupply, forBoilerSize));
        return actualHeat;
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, int boilerSize) {
        if (!this.isActive()) {
            return false;
        }
        this.calcMinMaxForSize(boilerSize);
        CreateLang.translate("boiler.status", this.getHeatLevelTextComponent().withStyle(ChatFormatting.GREEN)).forGoggles(tooltip);
        CreateLang.builder().add(this.getSizeComponent(true, false, new ChatFormatting[0])).forGoggles(tooltip, 1);
        CreateLang.builder().add(this.getWaterComponent(true, false, new ChatFormatting[0])).forGoggles(tooltip, 1);
        CreateLang.builder().add(this.getHeatComponent(true, false, new ChatFormatting[0])).forGoggles(tooltip, 1);
        if (this.attachedEngines == 0) {
            return true;
        }
        int boilerLevel = Math.min(this.activeHeat, Math.min(this.maxHeatForWater, this.maxHeatForSize));
        double totalSU = (double)(this.getEngineEfficiency(boilerSize) * 16.0f * (float)Math.max(boilerLevel, this.attachedEngines)) * BlockStressValues.getCapacity((Block)AllBlocks.STEAM_ENGINE.get());
        tooltip.add(CommonComponents.EMPTY);
        if (this.attachedEngines > 0 && this.maxHeatForSize > 0 && this.maxHeatForWater == 0 && (this.passiveHeat ? 1 : this.activeHeat) > 0) {
            CreateLang.translate("boiler.water_input_rate", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.number(this.waterSupply).style(ChatFormatting.BLUE).add(CreateLang.translate("generic.unit.millibuckets", new Object[0])).add(CreateLang.text(" / ").style(ChatFormatting.GRAY)).add(CreateLang.translate("boiler.per_tick", CreateLang.number(10.0).add(CreateLang.translate("generic.unit.millibuckets", new Object[0]))).style(ChatFormatting.DARK_GRAY)).forGoggles(tooltip, 1);
            return true;
        }
        CreateLang.translate("tooltip.capacityProvided", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.number(totalSU).translate("generic.unit.stress", new Object[0]).style(ChatFormatting.AQUA).space().add((this.attachedEngines == 1 ? CreateLang.translate("boiler.via_one_engine", new Object[0]) : CreateLang.translate("boiler.via_engines", this.attachedEngines)).style(ChatFormatting.DARK_GRAY)).forGoggles(tooltip, 1);
        return true;
    }

    public void calcMinMaxForSize(int boilerSize) {
        this.maxHeatForSize = this.getMaxHeatLevelForBoilerSize(boilerSize);
        this.maxHeatForWater = this.getMaxHeatLevelForWaterSupply();
        this.minValue = Math.min(this.passiveHeat ? 1 : this.activeHeat, Math.min(this.maxHeatForWater, this.maxHeatForSize));
        this.maxValue = Math.max(this.passiveHeat ? 1 : this.activeHeat, Math.max(this.maxHeatForWater, this.maxHeatForSize));
    }

    @NotNull
    public MutableComponent getHeatLevelTextComponent() {
        int boilerLevel = Math.min(this.activeHeat, Math.min(this.maxHeatForWater, this.maxHeatForSize));
        return this.isPassive() ? CreateLang.translateDirect("boiler.passive", new Object[0]) : (boilerLevel == 0 ? CreateLang.translateDirect("boiler.idle", new Object[0]) : (boilerLevel == 18 ? CreateLang.translateDirect("boiler.max_lvl", new Object[0]) : CreateLang.translateDirect("boiler.lvl", String.valueOf(boilerLevel))));
    }

    public MutableComponent getSizeComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting ... styles) {
        return this.componentHelper("size", this.maxHeatForSize, forGoggles, useBlocksAsBars, styles);
    }

    public MutableComponent getWaterComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting ... styles) {
        return this.componentHelper("water", this.maxHeatForWater, forGoggles, useBlocksAsBars, styles);
    }

    public MutableComponent getHeatComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting ... styles) {
        return this.componentHelper("heat", this.passiveHeat ? 1 : this.activeHeat, forGoggles, useBlocksAsBars, styles);
    }

    private MutableComponent componentHelper(String label, int level, boolean forGoggles, boolean useBlocksAsBars, ChatFormatting ... styles) {
        MutableComponent base;
        MutableComponent mutableComponent = base = useBlocksAsBars ? this.blockComponent(level) : this.barComponent(level);
        if (!forGoggles) {
            return base;
        }
        ChatFormatting style1 = styles.length >= 1 ? styles[0] : ChatFormatting.GRAY;
        ChatFormatting style2 = styles.length >= 2 ? styles[1] : ChatFormatting.DARK_GRAY;
        return CreateLang.translateDirect("boiler." + label, new Object[0]).withStyle(style1).append((Component)CreateLang.translateDirect("boiler." + label + "_dots", new Object[0]).withStyle(style2)).append((Component)base);
    }

    private MutableComponent blockComponent(int level) {
        return Component.literal((String)("\u2588".repeat(this.minValue) + "\u2592".repeat(level - this.minValue) + "\u2591".repeat(this.maxValue - level)));
    }

    private MutableComponent barComponent(int level) {
        return Component.empty().append((Component)this.bars(Math.max(0, this.minValue - 1), ChatFormatting.DARK_GREEN)).append((Component)this.bars(this.minValue > 0 ? 1 : 0, ChatFormatting.GREEN)).append((Component)this.bars(Math.max(0, level - this.minValue), ChatFormatting.DARK_GREEN)).append((Component)this.bars(Math.max(0, this.maxValue - level), ChatFormatting.DARK_RED)).append((Component)this.bars(Math.max(0, Math.min(18 - this.maxValue, (this.maxValue / 5 + 1) * 5 - this.maxValue)), ChatFormatting.DARK_GRAY));
    }

    private MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal((String)Strings.repeat((char)'|', (int)level)).withStyle(format);
    }

    public boolean evaluate(FluidTankBlockEntity controller) {
        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        int prevEngines = this.attachedEngines;
        int prevWhistles = this.attachedWhistles;
        this.attachedEngines = 0;
        this.attachedWhistles = 0;
        for (int yOffset = 0; yOffset < controller.height; ++yOffset) {
            for (int xOffset = 0; xOffset < controller.width; ++xOffset) {
                for (int zOffset = 0; zOffset < controller.width; ++zOffset) {
                    BlockPos pos = controllerPos.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidTankBlock.isTank(blockState)) continue;
                    for (Direction d : Iterate.directions) {
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        if (AllBlocks.STEAM_ENGINE.has(attachedState) && SteamEngineBlock.getFacing(attachedState) == d) {
                            ++this.attachedEngines;
                        }
                        if (!AllBlocks.STEAM_WHISTLE.has(attachedState) || WhistleBlock.getAttachedDirection(attachedState).getOpposite() != d) continue;
                        ++this.attachedWhistles;
                    }
                }
            }
        }
        this.needsHeatLevelUpdate = true;
        return prevEngines != this.attachedEngines || prevWhistles != this.attachedWhistles;
    }

    public void checkPipeOrganAdvancement(FluidTankBlockEntity controller) {
        if (!controller.getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent()) {
            return;
        }
        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        HashSet<Integer> whistlePitches = new HashSet<Integer>();
        for (int yOffset = 0; yOffset < controller.height; ++yOffset) {
            for (int xOffset = 0; xOffset < controller.width; ++xOffset) {
                for (int zOffset = 0; zOffset < controller.width; ++zOffset) {
                    BlockPos pos = controllerPos.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!FluidTankBlock.isTank(blockState)) continue;
                    for (Direction d : Iterate.directions) {
                        BlockEntity blockEntity;
                        BlockPos attachedPos = pos.relative(d);
                        BlockState attachedState = level.getBlockState(attachedPos);
                        if (!AllBlocks.STEAM_WHISTLE.has(attachedState) || WhistleBlock.getAttachedDirection(attachedState).getOpposite() != d || !((blockEntity = level.getBlockEntity(attachedPos)) instanceof WhistleBlockEntity)) continue;
                        WhistleBlockEntity wbe = (WhistleBlockEntity)blockEntity;
                        whistlePitches.add(wbe.getPitchId());
                    }
                }
            }
        }
        if (whistlePitches.size() >= 12) {
            controller.award(AllAdvancements.PIPE_ORGAN);
        }
    }

    public boolean updateTemperature(FluidTankBlockEntity controller) {
        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        this.needsHeatLevelUpdate = false;
        boolean prevPassive = this.passiveHeat;
        int prevActive = this.activeHeat;
        this.passiveHeat = false;
        this.activeHeat = 0;
        for (int xOffset = 0; xOffset < controller.width; ++xOffset) {
            for (int zOffset = 0; zOffset < controller.width; ++zOffset) {
                BlockState blockState;
                BlockPos pos = controllerPos.offset(xOffset, -1, zOffset);
                float heat = BoilerHeater.findHeat(level, pos, blockState = level.getBlockState(pos));
                if (heat == 0.0f) {
                    this.passiveHeat = true;
                    continue;
                }
                if (!(heat > 0.0f)) continue;
                this.activeHeat = (int)((float)this.activeHeat + heat);
            }
        }
        this.passiveHeat &= this.activeHeat == 0;
        return prevActive != this.activeHeat || prevPassive != this.passiveHeat;
    }

    public boolean isActive() {
        return this.attachedEngines > 0 || this.attachedWhistles > 0;
    }

    public void clear() {
        this.waterSupply = 0.0f;
        this.activeHeat = 0;
        this.passiveHeat = false;
        this.attachedEngines = 0;
        Arrays.fill(this.supplyOverTime, 0.0f);
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Supply", this.waterSupply);
        nbt.putInt("ActiveHeat", this.activeHeat);
        nbt.putBoolean("PassiveHeat", this.passiveHeat);
        nbt.putInt("Engines", this.attachedEngines);
        nbt.putInt("Whistles", this.attachedWhistles);
        nbt.putBoolean("Update", this.needsHeatLevelUpdate);
        return nbt;
    }

    public void read(CompoundTag nbt, int boilerSize) {
        this.waterSupply = nbt.getFloat("Supply");
        this.activeHeat = nbt.getInt("ActiveHeat");
        this.passiveHeat = nbt.getBoolean("PassiveHeat");
        this.attachedEngines = nbt.getInt("Engines");
        this.attachedWhistles = nbt.getInt("Whistles");
        this.needsHeatLevelUpdate = nbt.getBoolean("Update");
        Arrays.fill(this.supplyOverTime, (float)((int)this.waterSupply));
        int forBoilerSize = this.getMaxHeatLevelForBoilerSize(boilerSize);
        int forWaterSupply = this.getMaxHeatLevelForWaterSupply();
        int actualHeat = Math.min(this.activeHeat, Math.min(forWaterSupply, forBoilerSize));
        float target = this.isPassive(boilerSize) ? 0.125f : (forBoilerSize == 0 ? 0.0f : (float)actualHeat / ((float)forBoilerSize * 1.0f));
        this.gauge.chase((double)target, 0.125, LerpedFloat.Chaser.EXP);
    }

    public BoilerFluidHandler createHandler() {
        return new BoilerFluidHandler();
    }

    public class BoilerFluidHandler
    implements IFluidHandler {
        public int getTanks() {
            return 1;
        }

        public FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }

        public int getTankCapacity(int tank) {
            return 10000;
        }

        public boolean isFluidValid(int tank, FluidStack stack) {
            return FluidHelper.isWater(stack.getFluid());
        }

        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!this.isFluidValid(0, resource)) {
                return 0;
            }
            int amount = resource.getAmount();
            if (action.execute()) {
                BoilerData.this.gatheredSupply += amount;
            }
            return amount;
        }

        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }

        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}
