/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Container
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.decoration.HangingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DiodeBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.common.CommonHooks
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.entity.IEntityWithComplexSpawn
 *  net.neoforged.neoforge.event.EventHooks
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.wrapper.InvWrapper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.blueprint;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.schematic.requirement.SpecialEntityItemRequirement;
import com.simibubi.create.content.equipment.blueprint.BlueprintItem;
import com.simibubi.create.content.equipment.blueprint.BlueprintMenu;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.networking.ISyncPersistentData;
import com.simibubi.create.foundation.utility.IInteractionChecker;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class BlueprintEntity
extends HangingEntity
implements IEntityWithComplexSpawn,
SpecialEntityItemRequirement,
ISyncPersistentData,
IInteractionChecker {
    protected int size;
    protected Direction verticalOrientation;
    private Map<Integer, BlueprintSection> sectionCache = new HashMap<Integer, BlueprintSection>();

    public BlueprintEntity(EntityType<?> p_i50221_1_, Level p_i50221_2_) {
        super(p_i50221_1_, p_i50221_2_);
        this.size = 1;
    }

    public BlueprintEntity(Level world, BlockPos pos, Direction facing, Direction verticalOrientation) {
        super((EntityType)AllEntityTypes.CRAFTING_BLUEPRINT.get(), world, pos);
        int size = 3;
        while (size > 0) {
            this.size = size--;
            this.updateFacingWithBoundingBox(facing, verticalOrientation);
            if (this.survives()) break;
        }
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<?> entityBuilder = builder;
        return entityBuilder;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void addAdditionalSaveData(CompoundTag p_213281_1_) {
        p_213281_1_.putByte("Facing", (byte)this.direction.get3DDataValue());
        p_213281_1_.putByte("Orientation", (byte)this.verticalOrientation.get3DDataValue());
        p_213281_1_.putInt("Size", this.size);
        super.addAdditionalSaveData(p_213281_1_);
    }

    public void readAdditionalSaveData(CompoundTag p_70037_1_) {
        if (p_70037_1_.contains("Facing", 99)) {
            this.direction = Direction.from3DDataValue((int)p_70037_1_.getByte("Facing"));
            this.verticalOrientation = Direction.from3DDataValue((int)p_70037_1_.getByte("Orientation"));
            this.size = p_70037_1_.getInt("Size");
        } else {
            this.direction = Direction.SOUTH;
            this.verticalOrientation = Direction.DOWN;
            this.size = 1;
        }
        super.readAdditionalSaveData(p_70037_1_);
        this.updateFacingWithBoundingBox(this.direction, this.verticalOrientation);
    }

    protected void updateFacingWithBoundingBox(Direction facing, Direction verticalOrientation) {
        Objects.requireNonNull(facing);
        this.direction = facing;
        this.verticalOrientation = verticalOrientation;
        if (facing.getAxis().isHorizontal()) {
            this.setXRot(0.0f);
            this.setYRot(this.direction.get2DDataValue() * 90);
        } else {
            this.setXRot(-90 * facing.getAxisDirection().getStep());
            this.setYRot(verticalOrientation.getAxis().isHorizontal() ? 180.0f + verticalOrientation.toYRot() : 0.0f);
        }
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).withEyeHeight(0.0f);
    }

    protected AABB calculateBoundingBox(BlockPos blockPos, Direction direction) {
        Vec3 pos = Vec3.atLowerCornerOf((Vec3i)this.getPos()).add(0.5, 0.5, 0.5).subtract(Vec3.atLowerCornerOf((Vec3i)direction.getNormal()).scale(0.46875));
        double d1 = pos.x;
        double d2 = pos.y;
        double d3 = pos.z;
        this.setPosRaw(d1, d2, d3);
        Direction.Axis axis = direction.getAxis();
        if (this.size == 2) {
            pos = pos.add(Vec3.atLowerCornerOf((Vec3i)(axis.isHorizontal() ? direction.getCounterClockWise().getNormal() : this.verticalOrientation.getClockWise().getNormal())).scale(0.5)).add(Vec3.atLowerCornerOf((Vec3i)(axis.isHorizontal() ? Direction.UP.getNormal() : (direction == Direction.UP ? this.verticalOrientation.getNormal() : this.verticalOrientation.getOpposite().getNormal()))).scale(0.5));
        }
        d1 = pos.x;
        d2 = pos.y;
        d3 = pos.z;
        double d4 = this.getWidth();
        double d5 = this.getHeight();
        double d6 = this.getWidth();
        Direction.Axis direction$axis = this.direction.getAxis();
        switch (direction$axis) {
            case X: {
                d4 = 1.0;
                break;
            }
            case Y: {
                d5 = 1.0;
                break;
            }
            case Z: {
                d6 = 1.0;
            }
        }
        return new AABB(d1 - (d4 /= 32.0), d2 - (d5 /= 32.0), d3 - (d6 /= 32.0), d1 + d4, d2 + d5, d3 + d6);
    }

    protected void recalculateBoundingBox() {
        if (this.direction != null && this.verticalOrientation != null) {
            this.setBoundingBox(this.calculateBoundingBox(this.pos, this.direction));
        }
    }

    public void setPos(double pX, double pY, double pZ) {
        this.setPosRaw(pX, pY, pZ);
        super.setPos(pX, pY, pZ);
    }

    public boolean survives() {
        if (!this.level().noCollision((Entity)this)) {
            return false;
        }
        int i = Math.max(1, this.getWidth() / 16);
        int j = Math.max(1, this.getHeight() / 16);
        BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
        Direction upDirection = this.direction.getAxis().isHorizontal() ? Direction.UP : (this.direction == Direction.UP ? this.verticalOrientation : this.verticalOrientation.getOpposite());
        Direction newDirection = this.direction.getAxis().isVertical() ? this.verticalOrientation.getClockWise() : this.direction.getCounterClockWise();
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        for (int k = 0; k < i; ++k) {
            for (int l = 0; l < j; ++l) {
                int i1 = (i - 1) / -2;
                int j1 = (j - 1) / -2;
                blockpos$mutable.set((Vec3i)blockpos).move(newDirection, k + i1).move(upDirection, l + j1);
                BlockState blockstate = this.level().getBlockState((BlockPos)blockpos$mutable);
                if (Block.canSupportCenter((LevelReader)this.level(), (BlockPos)blockpos$mutable, (Direction)this.direction) || blockstate.isSolid() || DiodeBlock.isDiode((BlockState)blockstate)) continue;
                return false;
            }
        }
        return this.level().getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
    }

    public int getWidth() {
        return 16 * this.size;
    }

    public int getHeight() {
        return 16 * this.size;
    }

    public boolean skipAttackInteraction(Entity source) {
        Player player;
        block7: {
            block6: {
                if (!(source instanceof Player)) break block6;
                player = (Player)source;
                if (!this.level().isClientSide) break block7;
            }
            return super.skipAttackInteraction(source);
        }
        double attrib = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + (double)(player.isCreative() ? 0.0f : -0.5f);
        Vec3 eyePos = source.getEyePosition(1.0f);
        Vec3 look = source.getViewVector(1.0f);
        Vec3 target = eyePos.add(look.scale(attrib));
        Optional rayTrace = this.getBoundingBox().clip(eyePos, target);
        if (!rayTrace.isPresent()) {
            return super.skipAttackInteraction(source);
        }
        Vec3 hitVec = (Vec3)rayTrace.get();
        BlueprintSection sectionAt = this.getSectionAt(hitVec.subtract(this.position()));
        ItemStackHandler items = sectionAt.getItems();
        if (items.getStackInSlot(9).isEmpty()) {
            return super.skipAttackInteraction(source);
        }
        for (int i = 0; i < items.getSlots(); ++i) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }
        sectionAt.save(items);
        return true;
    }

    public void dropItem(@Nullable Entity p_110128_1_) {
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        this.playSound(SoundEvents.PAINTING_BREAK, 1.0f, 1.0f);
        if (p_110128_1_ instanceof Player) {
            Player playerentity = (Player)p_110128_1_;
            if (playerentity.getAbilities().instabuild) {
                return;
            }
        }
        this.spawnAtLocation(AllItems.CRAFTING_BLUEPRINT.asStack());
    }

    public ItemStack getPickedResult(HitResult target) {
        return AllItems.CRAFTING_BLUEPRINT.asStack();
    }

    @Override
    public ItemRequirement getRequiredItems() {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, (Item)AllItems.CRAFTING_BLUEPRINT.get());
    }

    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0f, 1.0f);
    }

    public void moveTo(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
        this.setPos(p_70012_1_, p_70012_3_, p_70012_5_);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
        BlockPos blockpos = this.pos.offset((Vec3i)BlockPos.containing((double)(pX - this.getX()), (double)(pY - this.getY()), (double)(pZ - this.getZ())));
        this.setPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    public void writeSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        CompoundTag compound = new CompoundTag();
        this.addAdditionalSaveData(compound);
        registryFriendlyByteBuf.writeNbt((Tag)compound);
        registryFriendlyByteBuf.writeNbt((Tag)this.getPersistentData());
    }

    public void readSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        this.readAdditionalSaveData(registryFriendlyByteBuf.readNbt());
        this.getPersistentData().merge(registryFriendlyByteBuf.readNbt());
    }

    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (player instanceof FakePlayer) {
            return InteractionResult.PASS;
        }
        boolean holdingWrench = AllItems.WRENCH.isIn(player.getItemInHand(hand));
        BlueprintSection section = this.getSectionAt(vec);
        ItemStackHandler items = section.getItems();
        if (!(holdingWrench || this.level().isClientSide || items.getStackInSlot(9).isEmpty())) {
            InvWrapper playerInv = new InvWrapper((Container)player.getInventory());
            boolean firstPass = true;
            int amountCrafted = 0;
            CommonHooks.setCraftingPlayer((Player)player);
            Optional recipe = Optional.empty();
            do {
                HashMap<Integer, ItemStack> stacksTaken = new HashMap<Integer, ItemStack>();
                HashMap<Integer, ItemStack> craftingGrid = new HashMap<Integer, ItemStack>();
                boolean success = true;
                block1: for (int i = 0; i < 9; ++i) {
                    FilterItemStack requestedItem = FilterItemStack.of(items.getStackInSlot(i));
                    if (requestedItem.isEmpty()) {
                        craftingGrid.put(i, ItemStack.EMPTY);
                        continue;
                    }
                    for (int slot = 0; slot < playerInv.getSlots(); ++slot) {
                        if (!requestedItem.test(this.level(), playerInv.getStackInSlot(slot))) continue;
                        ItemStack currentItem = playerInv.extractItem(slot, 1, false);
                        if (stacksTaken.containsKey(slot)) {
                            ((ItemStack)stacksTaken.get(slot)).grow(1);
                        } else {
                            stacksTaken.put(slot, currentItem.copy());
                        }
                        craftingGrid.put(i, currentItem);
                        continue block1;
                    }
                    success = false;
                    break;
                }
                if (success) {
                    ItemStack result;
                    BlueprintCraftingInventory craftingInventory = new BlueprintCraftingInventory(craftingGrid);
                    if (!recipe.isPresent()) {
                        recipe = this.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, (RecipeInput)craftingInventory.asCraftInput(), this.level());
                    }
                    if ((result = recipe.filter(arg_0 -> this.lambda$interactAt$0((CraftingContainer)craftingInventory, arg_0)).map(arg_0 -> this.lambda$interactAt$1((CraftingContainer)craftingInventory, arg_0)).orElse(ItemStack.EMPTY)).isEmpty()) {
                        success = false;
                    } else if (result.getCount() + amountCrafted > 64) {
                        success = false;
                    } else {
                        amountCrafted += result.getCount();
                        result.onCraftedBy(player.level(), player, 1);
                        EventHooks.firePlayerCraftingEvent((Player)player, (ItemStack)result, (Container)craftingInventory);
                        NonNullList nonnulllist = this.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, (RecipeInput)craftingInventory.asCraftInput(), this.level());
                        if (firstPass) {
                            this.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 1.0f + this.level().getRandom().nextFloat());
                        }
                        player.getInventory().placeItemBackInInventory(result);
                        for (ItemStack itemStack : nonnulllist) {
                            player.getInventory().placeItemBackInInventory(itemStack);
                        }
                        firstPass = false;
                    }
                }
                if (success) continue;
                for (Map.Entry entry : stacksTaken.entrySet()) {
                    playerInv.insertItem(((Integer)entry.getKey()).intValue(), (ItemStack)entry.getValue(), false);
                }
                break;
            } while (player.isShiftKeyDown());
            CommonHooks.setCraftingPlayer(null);
            return InteractionResult.SUCCESS;
        }
        int i = section.index;
        if (!this.level().isClientSide && player instanceof ServerPlayer) {
            player.openMenu((MenuProvider)section, buf -> {
                buf.writeVarInt(this.getId());
                buf.writeVarInt(i);
            });
        }
        return InteractionResult.SUCCESS;
    }

    public BlueprintSection getSectionAt(Vec3 vec) {
        int index = 0;
        if (this.size > 1) {
            vec = VecHelper.rotate((Vec3)vec, (double)this.getYRot(), (Direction.Axis)Direction.Axis.Y);
            vec = VecHelper.rotate((Vec3)vec, (double)(-this.getXRot()), (Direction.Axis)Direction.Axis.X);
            vec = vec.add(0.5, 0.5, 0.0);
            if (this.size == 3) {
                vec = vec.add(1.0, 1.0, 0.0);
            }
            int x = Mth.clamp((int)Mth.floor((double)vec.x), (int)0, (int)(this.size - 1));
            int y = Mth.clamp((int)Mth.floor((double)vec.y), (int)0, (int)(this.size - 1));
            index = x + y * this.size;
        }
        BlueprintSection section = this.getSection(index);
        return section;
    }

    public CompoundTag getOrCreateRecipeCompound() {
        CompoundTag persistentData = this.getPersistentData();
        if (!persistentData.contains("Recipes")) {
            persistentData.put("Recipes", (Tag)new CompoundTag());
        }
        return persistentData.getCompound("Recipes");
    }

    public BlueprintSection getSection(int index) {
        return this.sectionCache.computeIfAbsent(index, i -> new BlueprintSection((int)i));
    }

    @Override
    public void onPersistentDataUpdated() {
        this.sectionCache.clear();
    }

    @Override
    public boolean canPlayerUse(Player player) {
        AABB box = this.getBoundingBox();
        double dx = 0.0;
        if (box.minX > player.getX()) {
            dx = box.minX - player.getX();
        } else if (player.getX() > box.maxX) {
            dx = player.getX() - box.maxX;
        }
        double dy = 0.0;
        if (box.minY > player.getY()) {
            dy = box.minY - player.getY();
        } else if (player.getY() > box.maxY) {
            dy = player.getY() - box.maxY;
        }
        double dz = 0.0;
        if (box.minZ > player.getZ()) {
            dz = box.minZ - player.getZ();
        } else if (player.getZ() > box.maxZ) {
            dz = player.getZ() - box.maxZ;
        }
        return dx * dx + dy * dy + dz * dz <= 64.0;
    }

    private /* synthetic */ ItemStack lambda$interactAt$1(CraftingContainer craftingInventory, RecipeHolder r) {
        return ((CraftingRecipe)r.value()).assemble((RecipeInput)craftingInventory.asCraftInput(), (HolderLookup.Provider)this.registryAccess());
    }

    private /* synthetic */ boolean lambda$interactAt$0(CraftingContainer craftingInventory, RecipeHolder r) {
        return ((CraftingRecipe)r.value()).matches((RecipeInput)craftingInventory.asCraftInput(), this.level());
    }

    class BlueprintSection
    implements MenuProvider,
    IInteractionChecker {
        int index;
        Couple<ItemStack> cachedDisplayItems;
        public boolean inferredIcon = false;

        public BlueprintSection(int index) {
            this.index = index;
        }

        public Couple<ItemStack> getDisplayItems() {
            if (this.cachedDisplayItems != null) {
                return this.cachedDisplayItems;
            }
            ItemStackHandler items = this.getItems();
            this.cachedDisplayItems = Couple.create((Object)items.getStackInSlot(9), (Object)items.getStackInSlot(10));
            return this.cachedDisplayItems;
        }

        public ItemStackHandler getItems() {
            ItemStackHandler newInv = new ItemStackHandler(11);
            CompoundTag list = BlueprintEntity.this.getOrCreateRecipeCompound();
            CompoundTag invNBT = list.getCompound("" + this.index);
            this.inferredIcon = list.getBoolean("InferredIcon");
            if (!invNBT.isEmpty()) {
                newInv.deserializeNBT((HolderLookup.Provider)BlueprintEntity.this.registryAccess(), invNBT);
            }
            return newInv;
        }

        public void save(ItemStackHandler inventory) {
            CompoundTag list = BlueprintEntity.this.getOrCreateRecipeCompound();
            list.put("" + this.index, (Tag)inventory.serializeNBT((HolderLookup.Provider)BlueprintEntity.this.registryAccess()));
            list.putBoolean("InferredIcon", this.inferredIcon);
            this.cachedDisplayItems = null;
            if (!BlueprintEntity.this.level().isClientSide) {
                BlueprintEntity.this.syncPersistentDataWithTracking((Entity)BlueprintEntity.this);
            }
        }

        public boolean isEntityAlive() {
            return BlueprintEntity.this.isAlive();
        }

        public Level getBlueprintWorld() {
            return BlueprintEntity.this.level();
        }

        public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
            return BlueprintMenu.create(id, inv, this);
        }

        public Component getDisplayName() {
            return ((BlueprintItem)((Object)AllItems.CRAFTING_BLUEPRINT.get())).getDescription();
        }

        @Override
        public boolean canPlayerUse(Player player) {
            return BlueprintEntity.this.canPlayerUse(player);
        }
    }

    static class BlueprintCraftingInventory
    extends TransientCraftingContainer {
        private static final AbstractContainerMenu dummyContainer = new AbstractContainerMenu(null, -1){

            public boolean stillValid(Player playerIn) {
                return false;
            }

            public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
                return ItemStack.EMPTY;
            }
        };

        public BlueprintCraftingInventory(Map<Integer, ItemStack> items) {
            super(dummyContainer, 3, 3);
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 3; ++x) {
                    ItemStack stack = items.get(y * 3 + x);
                    this.setItem(y * 3 + x, stack == null ? ItemStack.EMPTY : stack.copy());
                }
            }
        }
    }
}
