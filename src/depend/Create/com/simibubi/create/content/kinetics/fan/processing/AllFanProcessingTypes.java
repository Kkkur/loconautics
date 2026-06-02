/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.horse.Horse
 *  net.minecraft.world.entity.animal.horse.SkeletonHorse
 *  net.minecraft.world.entity.monster.EnderMan
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.item.crafting.SmokingRecipe
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class AllFanProcessingTypes {
    public static final BlastingType BLASTING = AllFanProcessingTypes.register("blasting", new BlastingType());
    public static final HauntingType HAUNTING = AllFanProcessingTypes.register("haunting", new HauntingType());
    public static final SmokingType SMOKING = AllFanProcessingTypes.register("smoking", new SmokingType());
    public static final SplashingType SPLASHING = AllFanProcessingTypes.register("splashing", new SplashingType());
    private static final Map<String, FanProcessingType> LEGACY_NAME_MAP;

    private static <T extends FanProcessingType> T register(String name, T type) {
        return (T)((FanProcessingType)Registry.register(CreateBuiltInRegistries.FAN_PROCESSING_TYPE, (ResourceLocation)Create.asResource(name), type));
    }

    @ApiStatus.Internal
    public static void init() {
    }

    @Nullable
    public static FanProcessingType ofLegacyName(String name) {
        return LEGACY_NAME_MAP.get(name);
    }

    @Nullable
    public static FanProcessingType parseLegacy(String str) {
        FanProcessingType type = AllFanProcessingTypes.ofLegacyName(str);
        if (type != null) {
            return type;
        }
        return FanProcessingType.parse(str);
    }

    static {
        Object2ReferenceOpenHashMap map = new Object2ReferenceOpenHashMap();
        map.put((Object)"BLASTING", (Object)BLASTING);
        map.put((Object)"HAUNTING", (Object)HAUNTING);
        map.put((Object)"SMOKING", (Object)SMOKING);
        map.put((Object)"SPLASHING", (Object)SPLASHING);
        map.trim();
        LEGACY_NAME_MAP = map;
    }

    public static class BlastingType
    implements FanProcessingType {
        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            FluidState fluidState = level.getFluidState(pos);
            if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_BLASTING.matches(fluidState)) {
                return true;
            }
            BlockState blockState = level.getBlockState(pos);
            if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.matches(blockState)) {
                return !blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL) || ((BlazeBurnerBlock.HeatLevel)((Object)blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL))).isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
            }
            return false;
        }

        @Override
        public int getPriority() {
            return 100;
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            Optional<RecipeHolder<?>> smeltingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
            if (smeltingRecipe.isPresent()) {
                return true;
            }
            Optional<RecipeHolder<?>> blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
            if (blastingRecipe.isPresent()) {
                return true;
            }
            return !stack.has(DataComponents.FIRE_RESISTANT);
        }

        @Override
        @Nullable
        public List<ItemStack> process(ItemStack stack, Level level) {
            Optional<RecipeHolder<?>> smokingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
            Optional<RecipeHolder<?>> smeltingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
            if (smeltingRecipe.isEmpty()) {
                smeltingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
            }
            if (smeltingRecipe.isPresent()) {
                RegistryAccess registryAccess = level.registryAccess();
                if (smokingRecipe.isEmpty() || !ItemStack.isSameItem((ItemStack)((SmokingRecipe)smokingRecipe.get().value()).getResultItem((HolderLookup.Provider)registryAccess), (ItemStack)((AbstractCookingRecipe)smeltingRecipe.get().value()).getResultItem((HolderLookup.Provider)registryAccess))) {
                    return RecipeApplier.applyRecipeOn(level, stack, smeltingRecipe.get().value(), false);
                }
            }
            return Collections.emptyList();
        }

        @Override
        public void spawnProcessingParticles(Level level, Vec3 pos) {
            if (level.random.nextInt(8) != 0) {
                return;
            }
            level.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.25, pos.z, 0.0, 0.0625, 0.0);
        }

        @Override
        public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
            particleAccess.setColor(Color.mixColors((int)0xFF4400, (int)0xFF8855, (float)random.nextFloat()));
            particleAccess.setAlpha(0.5f);
            if (random.nextFloat() < 0.03125f) {
                particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.FLAME, 0.25f);
            }
            if (random.nextFloat() < 0.0625f) {
                particleAccess.spawnExtraParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, Blocks.LAVA.defaultBlockState()), 0.25f);
            }
        }

        @Override
        public void affectEntity(Entity entity, Level level) {
            if (level.isClientSide) {
                return;
            }
            if (!entity.fireImmune()) {
                entity.igniteForSeconds(10.0f);
                entity.hurt(CreateDamageSources.fanLava(level), 4.0f);
            }
        }
    }

    public static class HauntingType
    implements FanProcessingType {
        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            FluidState fluidState = level.getFluidState(pos);
            if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_HAUNTING.matches(fluidState)) {
                return true;
            }
            BlockState blockState = level.getBlockState(pos);
            if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_HAUNTING.matches(blockState)) {
                if (blockState.is(BlockTags.CAMPFIRES) && blockState.hasProperty((Property)CampfireBlock.LIT) && !((Boolean)blockState.getValue((Property)CampfireBlock.LIT)).booleanValue()) {
                    return false;
                }
                return !blockState.hasProperty(LitBlazeBurnerBlock.FLAME_TYPE) || blockState.getValue(LitBlazeBurnerBlock.FLAME_TYPE) == LitBlazeBurnerBlock.FlameType.SOUL;
            }
            return false;
        }

        @Override
        public int getPriority() {
            return 300;
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            return AllRecipeTypes.HAUNTING.find(new SingleRecipeInput(stack), level).isPresent();
        }

        @Override
        @Nullable
        public List<ItemStack> process(ItemStack stack, Level level) {
            return AllRecipeTypes.HAUNTING.find(new SingleRecipeInput(stack), level).map(RecipeHolder::value).map(r -> RecipeApplier.applyRecipeOn(level, stack, r, true)).orElse(null);
        }

        @Override
        public void spawnProcessingParticles(Level level, Vec3 pos) {
            if (level.random.nextInt(8) != 0) {
                return;
            }
            pos = pos.add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)level.random, (float)1.0f).multiply(1.0, (double)0.05f, 1.0).normalize().scale((double)0.15f));
            level.addParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y + (double)0.45f, pos.z, 0.0, 0.0, 0.0);
            if (level.random.nextInt(2) == 0) {
                level.addParticle((ParticleOptions)ParticleTypes.SMOKE, pos.x, pos.y + 0.25, pos.z, 0.0, 0.0, 0.0);
            }
        }

        @Override
        public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
            particleAccess.setColor(Color.mixColors((int)0, (int)1205608, (float)random.nextFloat()));
            particleAccess.setAlpha(1.0f);
            if (random.nextFloat() < 0.0078125f) {
                particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, 0.125f);
            }
            if (random.nextFloat() < 0.03125f) {
                particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.SMOKE, 0.125f);
            }
        }

        @Override
        public void affectEntity(Entity entity, Level level) {
            if (level.isClientSide) {
                if (entity instanceof Horse) {
                    Vec3 p = entity.getPosition(0.0f);
                    Vec3 v = p.add(0.0, 0.5, 0.0).add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)level.random, (float)1.0f).multiply(1.0, (double)0.2f, 1.0).normalize().scale(1.0));
                    level.addParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, v.x, v.y, v.z, 0.0, (double)0.1f, 0.0);
                    if (level.random.nextInt(3) == 0) {
                        level.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, p.x, p.y + 0.5, p.z, (double)((level.random.nextFloat() - 0.5f) * 0.5f), (double)0.1f, (double)((level.random.nextFloat() - 0.5f) * 0.5f));
                    }
                }
                return;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, false));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
            }
            if (entity instanceof Horse) {
                Horse horse = (Horse)entity;
                int progress = horse.getPersistentData().getInt("CreateHaunting");
                if (progress < 100) {
                    if (progress % 10 == 0) {
                        level.playSound(null, entity.blockPosition(), (SoundEvent)SoundEvents.SOUL_ESCAPE.value(), SoundSource.NEUTRAL, 1.0f, 1.5f * (float)progress / 100.0f);
                    }
                    horse.getPersistentData().putInt("CreateHaunting", progress + 1);
                    return;
                }
                level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 1.25f, 0.65f);
                SkeletonHorse skeletonHorse = (SkeletonHorse)EntityType.SKELETON_HORSE.create(level);
                CompoundTag serializeNBT = horse.saveWithoutId(new CompoundTag());
                serializeNBT.remove("UUID");
                if (!horse.getBodyArmorItem().isEmpty()) {
                    horse.spawnAtLocation(horse.getBodyArmorItem());
                }
                skeletonHorse.deserializeNBT((HolderLookup.Provider)entity.registryAccess(), serializeNBT);
                skeletonHorse.setPos(horse.getPosition(0.0f));
                level.addFreshEntity((Entity)skeletonHorse);
                horse.discard();
            }
        }
    }

    public static class SmokingType
    implements FanProcessingType {
        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            FluidState fluidState = level.getFluidState(pos);
            if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_SMOKING.matches(fluidState)) {
                return true;
            }
            BlockState blockState = level.getBlockState(pos);
            if (AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.matches(blockState)) {
                if (blockState.is(BlockTags.CAMPFIRES) && blockState.hasProperty((Property)CampfireBlock.LIT) && !((Boolean)blockState.getValue((Property)CampfireBlock.LIT)).booleanValue()) {
                    return false;
                }
                if (blockState.hasProperty(LitBlazeBurnerBlock.FLAME_TYPE) && blockState.getValue(LitBlazeBurnerBlock.FLAME_TYPE) != LitBlazeBurnerBlock.FlameType.REGULAR) {
                    return false;
                }
                return !blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL) || blockState.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.SMOULDERING;
            }
            return false;
        }

        @Override
        public int getPriority() {
            return 200;
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            return level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED).isPresent();
        }

        @Override
        @Nullable
        public List<ItemStack> process(ItemStack stack, Level level) {
            return level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, (RecipeInput)new SingleRecipeInput(stack), level).filter(AllRecipeTypes.CAN_BE_AUTOMATED).map(RecipeHolder::value).map(r -> RecipeApplier.applyRecipeOn(level, stack, r, false)).orElse(null);
        }

        @Override
        public void spawnProcessingParticles(Level level, Vec3 pos) {
            if (level.random.nextInt(8) != 0) {
                return;
            }
            level.addParticle((ParticleOptions)ParticleTypes.POOF, pos.x, pos.y + 0.25, pos.z, 0.0, 0.0625, 0.0);
        }

        @Override
        public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
            particleAccess.setColor(Color.mixColors((int)0, (int)0x555555, (float)random.nextFloat()));
            particleAccess.setAlpha(1.0f);
            if (random.nextFloat() < 0.03125f) {
                particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.SMOKE, 0.125f);
            }
            if (random.nextFloat() < 0.03125f) {
                particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, 0.125f);
            }
        }

        @Override
        public void affectEntity(Entity entity, Level level) {
            if (level.isClientSide) {
                return;
            }
            if (!entity.fireImmune()) {
                entity.igniteForSeconds(2.0f);
                entity.hurt(CreateDamageSources.fanFire(level), 2.0f);
            }
        }
    }

    public static class SplashingType
    implements FanProcessingType {
        @Override
        public boolean isValidAt(Level level, BlockPos pos) {
            FluidState fluidState = level.getFluidState(pos);
            if (AllTags.AllFluidTags.FAN_PROCESSING_CATALYSTS_SPLASHING.matches(fluidState)) {
                return true;
            }
            BlockState blockState = level.getBlockState(pos);
            return AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SPLASHING.matches(blockState);
        }

        @Override
        public int getPriority() {
            return 400;
        }

        @Override
        public boolean canProcess(ItemStack stack, Level level) {
            return AllRecipeTypes.SPLASHING.find(new SingleRecipeInput(stack), level).isPresent();
        }

        @Override
        @Nullable
        public List<ItemStack> process(ItemStack stack, Level level) {
            Optional recipe = AllRecipeTypes.SPLASHING.find(new SingleRecipeInput(stack), level);
            return AllRecipeTypes.SPLASHING.find(new SingleRecipeInput(stack), level).map(RecipeHolder::value).map(r -> RecipeApplier.applyRecipeOn(level, stack, r, true)).orElse(null);
        }

        @Override
        public void spawnProcessingParticles(Level level, Vec3 pos) {
            if (level.random.nextInt(8) != 0) {
                return;
            }
            Vector3f color = new Color(22015).asVectorF();
            level.addParticle((ParticleOptions)new DustParticleOptions(color, 1.0f), pos.x + (double)((level.random.nextFloat() - 0.5f) * 0.5f), pos.y + 0.5, pos.z + (double)((level.random.nextFloat() - 0.5f) * 0.5f), 0.0, 0.125, 0.0);
            level.addParticle((ParticleOptions)ParticleTypes.SPIT, pos.x + (double)((level.random.nextFloat() - 0.5f) * 0.5f), pos.y + 0.5, pos.z + (double)((level.random.nextFloat() - 0.5f) * 0.5f), 0.0, 0.125, 0.0);
        }

        @Override
        public void morphAirFlow(FanProcessingType.AirFlowParticleAccess particleAccess, RandomSource random) {
            particleAccess.setColor(Color.mixColors((int)0x4499FF, (int)0x2277FF, (float)random.nextFloat()));
            particleAccess.setAlpha(1.0f);
            if (random.nextFloat() < 0.03125f) {
                particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.BUBBLE, 0.125f);
            }
            if (random.nextFloat() < 0.03125f) {
                particleAccess.spawnExtraParticle((ParticleOptions)ParticleTypes.BUBBLE_POP, 0.125f);
            }
        }

        @Override
        public void affectEntity(Entity entity, Level level) {
            if (level.isClientSide) {
                return;
            }
            if (entity instanceof EnderMan || entity.getType() == EntityType.SNOW_GOLEM || entity.getType() == EntityType.BLAZE) {
                entity.hurt(entity.damageSources().drown(), 2.0f);
            }
            if (entity.isOnFire()) {
                entity.clearFire();
                level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 0.7f, 1.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.4f);
            }
        }
    }
}
