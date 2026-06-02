/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.item.PrimedTnt
 *  net.minecraft.world.entity.projectile.FireworkRocketEntity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.projectile.SmallFireball
 *  net.minecraft.world.entity.projectile.ThrownPotion
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.BeehiveBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BucketPickup
 *  net.minecraft.world.level.block.entity.BeehiveBlockEntity$BeeReleaseStatus
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.MountedDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.MountedProjectileDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.OptionalMountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AllMountedDispenseItemBehaviors {
    private static final MountedDispenseBehavior SPAWN_EGG = new DefaultMountedDispenseBehavior(){

        @Override
        protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
            Item item = stack.getItem();
            if (!(item instanceof SpawnEggItem)) {
                return super.execute(stack, context, pos, facing);
            }
            SpawnEggItem egg = (SpawnEggItem)item;
            Level level = context.world;
            if (level instanceof ServerLevel) {
                BlockPos offset;
                ServerLevel serverLevel = (ServerLevel)level;
                EntityType type = egg.getType(stack);
                Entity entity = type.spawn(serverLevel, stack, null, pos.offset((Vec3i)(offset = BlockPos.containing((double)(facing.x + 0.7), (double)(facing.y + 0.7), (double)(facing.z + 0.7)))), MobSpawnType.DISPENSER, facing.y < 0.5, false);
                if (entity != null) {
                    entity.setDeltaMovement(context.motion.scale(2.0));
                }
            }
            stack.shrink(1);
            return stack;
        }
    };
    private static final MountedDispenseBehavior TNT = new DefaultMountedDispenseBehavior(){

        @Override
        protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
            double x = (double)pos.getX() + facing.x * 0.7 + 0.5;
            double y = (double)pos.getY() + facing.y * 0.7 + 0.5;
            double z = (double)pos.getZ() + facing.z * 0.7 + 0.5;
            PrimedTnt tnt = new PrimedTnt(context.world, x, y, z, null);
            tnt.push(context.motion.x, context.motion.y, context.motion.z);
            context.world.addFreshEntity((Entity)tnt);
            context.world.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
            stack.shrink(1);
            return stack;
        }
    };
    private static final MountedDispenseBehavior FIREWORK = new DefaultMountedDispenseBehavior(){

        @Override
        protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
            double x = (double)pos.getX() + facing.x * 0.7 + 0.5;
            double y = (double)pos.getY() + facing.y * 0.7 + 0.5;
            double z = (double)pos.getZ() + facing.z * 0.7 + 0.5;
            FireworkRocketEntity firework = new FireworkRocketEntity(context.world, stack, x, y, z, true);
            firework.shoot(facing.x, facing.y, facing.z, 0.5f, 1.0f);
            context.world.addFreshEntity((Entity)firework);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(LevelAccessor level, BlockPos pos) {
            level.levelEvent(1004, pos, 0);
        }
    };
    private static final MountedDispenseBehavior FIRE_CHARGE = new DefaultMountedDispenseBehavior(){

        @Override
        protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
            RandomSource random = context.world.random;
            double x = (double)pos.getX() + facing.x * 0.7 + 0.5;
            double y = (double)pos.getY() + facing.y * 0.7 + 0.5;
            double z = (double)pos.getZ() + facing.z * 0.7 + 0.5;
            SmallFireball fireball = new SmallFireball(context.world, x, y, z, new Vec3(random.nextGaussian() * 0.05 + facing.x + context.motion.x, random.nextGaussian() * 0.05 + facing.y + context.motion.y, random.nextGaussian() * 0.05 + facing.z + context.motion.z).normalize());
            fireball.setItem(stack);
            context.world.addFreshEntity((Entity)fireball);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(LevelAccessor level, BlockPos pos) {
            level.levelEvent(1018, pos, 0);
        }
    };
    private static final MountedDispenseBehavior BUCKET = new DefaultMountedDispenseBehavior(){

        @Override
        protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
            BlockPos interactionPos = pos.relative(MountedDispenseBehavior.getClosestFacingDirection(facing));
            BlockState state = context.world.getBlockState(interactionPos);
            Block block = state.getBlock();
            if (!(block instanceof BucketPickup)) {
                return super.execute(stack, context, pos, facing);
            }
            BucketPickup bucketPickup = (BucketPickup)block;
            ItemStack bucket = bucketPickup.pickupBlock(null, (LevelAccessor)context.world, interactionPos, state);
            MountedDispenseBehavior.placeItemInInventory(bucket, context, pos);
            stack.shrink(1);
            return stack;
        }
    };
    private static final MountedDispenseBehavior POTIONS = new MountedProjectileDispenseBehavior(){

        @Override
        protected Projectile getProjectile(Level level, double x, double y, double z, ItemStack stack, Direction facing) {
            ThrownPotion potion = new ThrownPotion(level, x, y, z);
            potion.setItem(stack);
            return potion;
        }

        @Override
        protected float getUncertainty() {
            return super.getUncertainty() * 0.5f;
        }

        @Override
        protected float getPower() {
            return super.getPower() * 1.25f;
        }
    };
    private static final MountedDispenseBehavior BOTTLE = new OptionalMountedDispenseBehavior(){

        @Override
        @Nullable
        protected ItemStack doExecute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
            BlockPos interactionPos = pos.relative(MountedDispenseBehavior.getClosestFacingDirection(facing));
            BlockState state = context.world.getBlockState(interactionPos);
            Block block = state.getBlock();
            if (block instanceof BeehiveBlock) {
                BeehiveBlock hive = (BeehiveBlock)block;
                if (state.is(BlockTags.BEEHIVES) && (Integer)state.getValue((Property)BeehiveBlock.HONEY_LEVEL) >= 5) {
                    hive.releaseBeesAndResetHoneyLevel(context.world, state, interactionPos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                    MountedDispenseBehavior.placeItemInInventory(new ItemStack((ItemLike)Items.HONEY_BOTTLE), context, pos);
                    stack.shrink(1);
                    return stack;
                }
            }
            if (context.world.getFluidState(interactionPos).is(FluidTags.WATER)) {
                ItemStack waterBottle = PotionContents.createItemStack((Item)Items.POTION, (Holder)Potions.WATER);
                MountedDispenseBehavior.placeItemInInventory(waterBottle, context, pos);
                stack.shrink(1);
                return stack;
            }
            return null;
        }
    };

    public static void registerDefaults() {
        MountedDispenseBehavior.REGISTRY.registerProvider(item -> item instanceof SpawnEggItem ? SPAWN_EGG : null);
        MountedDispenseBehavior.REGISTRY.register(Items.TNT, TNT);
        MountedDispenseBehavior.REGISTRY.register(Items.FIREWORK_ROCKET, FIREWORK);
        MountedDispenseBehavior.REGISTRY.register(Items.FIRE_CHARGE, FIRE_CHARGE);
        MountedDispenseBehavior.REGISTRY.register(Items.BUCKET, BUCKET);
        MountedDispenseBehavior.REGISTRY.register(Items.GLASS_BOTTLE, BOTTLE);
        MountedDispenseBehavior.REGISTRY.register(Items.SPLASH_POTION, POTIONS);
        MountedDispenseBehavior.REGISTRY.register(Items.LINGERING_POTION, POTIONS);
    }
}
