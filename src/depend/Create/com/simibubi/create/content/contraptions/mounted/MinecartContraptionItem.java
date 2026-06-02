/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.dispenser.BlockSource
 *  net.minecraft.core.dispenser.DefaultDispenseItemBehavior
 *  net.minecraft.core.dispenser.DispenseItemBehavior
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.AbstractMinecart$Type
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  org.apache.commons.lang3.tuple.MutablePair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.mounted;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.ContraptionMovementSetting;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.data.ContraptionPickupLimiting;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class MinecartContraptionItem
extends Item {
    private final AbstractMinecart.Type minecartType;
    private static final DispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){
        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        public ItemStack execute(BlockSource source, ItemStack stack) {
            double d3;
            RailShape railshape;
            Direction direction = (Direction)source.state().getValue((Property)DispenserBlock.FACING);
            ServerLevel world = source.level();
            Vec3 vec3 = source.center();
            double d0 = vec3.x() + (double)direction.getStepX() * 1.125;
            double d1 = Math.floor(vec3.y()) + (double)direction.getStepY();
            double d2 = vec3.z() + (double)direction.getStepZ() * 1.125;
            BlockPos blockpos = source.pos().relative(direction);
            BlockState blockstate = world.getBlockState(blockpos);
            RailShape railShape = railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, (BlockGetter)world, blockpos, null) : RailShape.NORTH_SOUTH;
            if (blockstate.is(BlockTags.RAILS)) {
                d3 = railshape.isAscending() ? 0.6 : 0.1;
            } else {
                if (!blockstate.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }
                BlockState blockstate1 = world.getBlockState(blockpos.below());
                RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate1.getBlock()).getRailDirection(blockstate1, (BlockGetter)world, blockpos.below(), null) : RailShape.NORTH_SOUTH;
                d3 = direction != Direction.DOWN && railshape1.isAscending() ? -0.4 : -0.9;
            }
            AbstractMinecart abstractminecartentity = AbstractMinecart.createMinecart((ServerLevel)world, (double)d0, (double)(d1 + d3), (double)d2, (AbstractMinecart.Type)((MinecartContraptionItem)stack.getItem()).minecartType, (ItemStack)stack, null);
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                abstractminecartentity.setCustomName(stack.getHoverName());
            }
            world.addFreshEntity((Entity)abstractminecartentity);
            MinecartContraptionItem.addContraptionToMinecart((Level)world, stack, abstractminecartentity, direction);
            stack.shrink(1);
            return stack;
        }

        protected void playSound(BlockSource source) {
            source.level().levelEvent(1000, source.pos(), 0);
        }
    };

    public static MinecartContraptionItem rideable(Item.Properties builder) {
        return new MinecartContraptionItem(AbstractMinecart.Type.RIDEABLE, builder);
    }

    public static MinecartContraptionItem furnace(Item.Properties builder) {
        return new MinecartContraptionItem(AbstractMinecart.Type.FURNACE, builder);
    }

    public static MinecartContraptionItem chest(Item.Properties builder) {
        return new MinecartContraptionItem(AbstractMinecart.Type.CHEST, builder);
    }

    public boolean canFitInsideContainerItems() {
        return (Boolean)AllConfigs.server().kinetics.minecartContraptionInContainers.get();
    }

    private MinecartContraptionItem(AbstractMinecart.Type minecartTypeIn, Item.Properties builder) {
        super(builder);
        this.minecartType = minecartTypeIn;
        DispenserBlock.registerBehavior((ItemLike)this, (DispenseItemBehavior)DISPENSER_BEHAVIOR);
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos;
        Level world = context.getLevel();
        BlockState blockstate = world.getBlockState(blockpos = context.getClickedPos());
        if (!blockstate.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        ItemStack itemstack = context.getItemInHand();
        if (world instanceof ServerLevel) {
            ServerLevel serverlevel = (ServerLevel)world;
            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, (BlockGetter)world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0;
            if (railshape.isAscending()) {
                d0 = 0.5;
            }
            AbstractMinecart abstractminecartentity = AbstractMinecart.createMinecart((ServerLevel)serverlevel, (double)((double)blockpos.getX() + 0.5), (double)((double)blockpos.getY() + 0.0625 + d0), (double)((double)blockpos.getZ() + 0.5), (AbstractMinecart.Type)this.minecartType, (ItemStack)itemstack, null);
            if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                abstractminecartentity.setCustomName(itemstack.getHoverName());
            }
            Player player = context.getPlayer();
            world.addFreshEntity((Entity)abstractminecartentity);
            MinecartContraptionItem.addContraptionToMinecart(world, itemstack, abstractminecartentity, player == null ? null : player.getDirection());
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    public static void addContraptionToMinecart(Level world, ItemStack itemstack, AbstractMinecart cart, @Nullable Direction newFacing) {
        if (itemstack.has(AllDataComponents.MINECRAFT_CONTRAPTION_DATA)) {
            CompoundTag contraptionTag = (CompoundTag)itemstack.get(AllDataComponents.MINECRAFT_CONTRAPTION_DATA);
            Direction intialOrientation = (Direction)NBTHelper.readEnum((CompoundTag)contraptionTag, (String)"InitialOrientation", Direction.class);
            Contraption mountedContraption = Contraption.fromNBT(world, contraptionTag, false);
            OrientedContraptionEntity contraptionEntity = newFacing == null ? OrientedContraptionEntity.create(world, mountedContraption, intialOrientation) : OrientedContraptionEntity.createAtYaw(world, mountedContraption, intialOrientation, newFacing.toYRot());
            contraptionEntity.startRiding((Entity)cart);
            contraptionEntity.setPos(cart.getX(), cart.getY(), cart.getZ());
            world.addFreshEntity((Entity)contraptionEntity);
        }
    }

    public String getDescriptionId(ItemStack stack) {
        return "item.create.minecart_contraption";
    }

    @SubscribeEvent
    public static void wrenchCanBeUsedToPickUpMinecartContraptions(PlayerInteractEvent.EntityInteract event) {
        Object e;
        AbstractMinecart.Type type;
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        if (player == null || entity == null) {
            return;
        }
        if (!((Boolean)AllConfigs.server().kinetics.survivalContraptionPickup.get()).booleanValue() && !player.isCreative()) {
            return;
        }
        ItemStack wrench = player.getItemInHand(event.getHand());
        if (!AllItems.WRENCH.isIn(wrench)) {
            return;
        }
        if (entity instanceof AbstractContraptionEntity) {
            entity = entity.getVehicle();
        }
        if (!(entity instanceof AbstractMinecart)) {
            return;
        }
        AbstractMinecart cart = (AbstractMinecart)entity;
        if (!entity.isAlive()) {
            return;
        }
        if (player instanceof DeployerFakePlayer) {
            DeployerFakePlayer dfp = (DeployerFakePlayer)player;
            if (dfp.onMinecartContraption) {
                return;
            }
        }
        if ((type = cart.getMinecartType()) != AbstractMinecart.Type.RIDEABLE && type != AbstractMinecart.Type.FURNACE && type != AbstractMinecart.Type.CHEST) {
            return;
        }
        List passengers = cart.getPassengers();
        if (passengers.isEmpty() || !((e = passengers.get(0)) instanceof OrientedContraptionEntity)) {
            return;
        }
        OrientedContraptionEntity oce = (OrientedContraptionEntity)((Object)e);
        Contraption contraption = oce.getContraption();
        if (ContraptionMovementSetting.isNoPickup(contraption.getBlocks().values())) {
            player.displayClientMessage((Component)CreateLang.translateDirect("contraption.minecart_contraption_illegal_pickup", new Object[0]).withStyle(ChatFormatting.RED), true);
            return;
        }
        if (event.getLevel().isClientSide) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        contraption.stop(event.getLevel());
        for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair : contraption.getActors()) {
            MovementBehaviour movementBehaviour = MovementBehaviour.REGISTRY.get((StateHolder<Block, ?>)((StructureTemplate.StructureBlockInfo)pair.left).state());
            if (!(movementBehaviour instanceof PortableStorageInterfaceMovement)) continue;
            PortableStorageInterfaceMovement psim = (PortableStorageInterfaceMovement)movementBehaviour;
            psim.reset((MovementContext)pair.right);
        }
        ItemStack generatedStack = MinecartContraptionItem.create(type, oce);
        generatedStack.set(DataComponents.CUSTOM_NAME, (Object)entity.getCustomName());
        if (ContraptionPickupLimiting.isTooLargeForPickup(generatedStack.saveOptional((HolderLookup.Provider)event.getLevel().registryAccess()))) {
            MutableComponent message = CreateLang.translateDirect("contraption.minecart_contraption_too_big", new Object[0]).withStyle(ChatFormatting.RED);
            player.displayClientMessage((Component)message, true);
            return;
        }
        if (contraption.getBlocks().size() > 200) {
            AllAdvancements.CART_PICKUP.awardTo(player);
        }
        player.getInventory().placeItemBackInInventory(generatedStack);
        oce.discard();
        entity.discard();
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    public static ItemStack create(AbstractMinecart.Type type, OrientedContraptionEntity entity) {
        ItemStack stack = ItemStack.EMPTY;
        switch (type) {
            case RIDEABLE: {
                stack = AllItems.MINECART_CONTRAPTION.asStack();
                break;
            }
            case FURNACE: {
                stack = AllItems.FURNACE_MINECART_CONTRAPTION.asStack();
                break;
            }
            case CHEST: {
                stack = AllItems.CHEST_MINECART_CONTRAPTION.asStack();
                break;
            }
        }
        if (stack.isEmpty()) {
            return stack;
        }
        CompoundTag tag = entity.getContraption().writeNBT((HolderLookup.Provider)entity.registryAccess(), false);
        tag.remove("UUID");
        tag.remove("Pos");
        tag.remove("Motion");
        NBTHelper.writeEnum((CompoundTag)tag, (String)"InitialOrientation", (Enum)entity.getInitialOrientation());
        stack.set(AllDataComponents.MINECRAFT_CONTRAPTION_DATA, (Object)tag);
        return stack;
    }
}
