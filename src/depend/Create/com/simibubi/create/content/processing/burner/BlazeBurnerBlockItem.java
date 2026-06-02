/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.random.WeightedEntry$Wrapper
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BaseSpawner
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.SpawnData
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.SpawnerBlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.processing.burner;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlazeBurnerBlockItem
extends BlockItem {
    private final boolean capturedBlaze;

    public static BlazeBurnerBlockItem empty(Item.Properties properties) {
        return new BlazeBurnerBlockItem((Block)AllBlocks.BLAZE_BURNER.get(), properties, false);
    }

    public static BlazeBurnerBlockItem withBlaze(Block block, Item.Properties properties) {
        return new BlazeBurnerBlockItem(block, properties, true);
    }

    public void registerBlocks(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
        if (!this.hasCapturedBlaze()) {
            return;
        }
        super.registerBlocks(p_195946_1_, p_195946_2_);
    }

    private BlazeBurnerBlockItem(Block block, Item.Properties properties, boolean capturedBlaze) {
        super(block, properties);
        this.capturedBlaze = capturedBlaze;
    }

    public String getDescriptionId() {
        return this.hasCapturedBlaze() ? super.getDescriptionId() : "item.create." + RegisteredObjectsHelper.getKeyOrThrow((Item)this).getPath();
    }

    public InteractionResult useOn(UseOnContext context) {
        if (this.hasCapturedBlaze()) {
            return super.useOn(context);
        }
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = world.getBlockEntity(pos);
        Player player = context.getPlayer();
        if (!(be instanceof SpawnerBlockEntity)) {
            return super.useOn(context);
        }
        BaseSpawner spawner = ((SpawnerBlockEntity)be).getSpawner();
        List<SpawnData> possibleSpawns = spawner.spawnPotentials.unwrap().stream().map(WeightedEntry.Wrapper::data).toList();
        if (possibleSpawns.isEmpty()) {
            possibleSpawns = new ArrayList<SpawnData>();
            possibleSpawns.add(spawner.nextSpawnData);
        }
        for (SpawnData e : possibleSpawns) {
            Optional optionalEntity = EntityType.by((CompoundTag)e.entityToSpawn());
            if (optionalEntity.isEmpty() || !AllTags.AllEntityTags.BLAZE_BURNER_CAPTURABLE.matches((EntityType)optionalEntity.get())) continue;
            this.spawnCaptureEffects(world, VecHelper.getCenterOf((Vec3i)pos));
            if (world.isClientSide || player == null) {
                return InteractionResult.SUCCESS;
            }
            this.giveBurnerItemTo(player, context.getItemInHand(), context.getHand());
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    public InteractionResult interactLivingEntity(ItemStack heldItem, Player player, LivingEntity entity, InteractionHand hand) {
        if (this.hasCapturedBlaze()) {
            return InteractionResult.PASS;
        }
        if (!AllTags.AllEntityTags.BLAZE_BURNER_CAPTURABLE.matches((Entity)entity)) {
            return InteractionResult.PASS;
        }
        Level world = player.level();
        this.spawnCaptureEffects(world, entity.position());
        if (world.isClientSide) {
            return InteractionResult.FAIL;
        }
        this.giveBurnerItemTo(player, heldItem, hand);
        entity.discard();
        return InteractionResult.FAIL;
    }

    protected void giveBurnerItemTo(Player player, ItemStack heldItem, InteractionHand hand) {
        ItemStack filled = AllBlocks.BLAZE_BURNER.asStack();
        if (!player.isCreative()) {
            heldItem.shrink(1);
        }
        if (heldItem.isEmpty()) {
            player.setItemInHand(hand, filled);
            return;
        }
        player.getInventory().placeItemBackInInventory(filled);
    }

    private void spawnCaptureEffects(Level world, Vec3 vec) {
        if (world.isClientSide) {
            for (int i = 0; i < 40; ++i) {
                Vec3 motion = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)world.random, (float)0.125f);
                world.addParticle((ParticleOptions)ParticleTypes.FLAME, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                Vec3 circle = motion.multiply(1.0, 0.0, 1.0).normalize().scale(0.5);
                world.addParticle((ParticleOptions)ParticleTypes.SMOKE, circle.x, vec.y, circle.z, 0.0, -0.125, 0.0);
            }
            return;
        }
        BlockPos soundPos = BlockPos.containing((Position)vec);
        world.playSound(null, soundPos, SoundEvents.BLAZE_HURT, SoundSource.HOSTILE, 0.25f, 0.75f);
        world.playSound(null, soundPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.5f, 0.75f);
    }

    public boolean hasCapturedBlaze() {
        return this.capturedBlaze;
    }
}
