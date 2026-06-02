/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Glob
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.box;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.item.ItemHelper;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Glob;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class PackageItem
extends Item {
    public static final int SLOTS = 9;
    public PackageStyles.PackageStyle style;

    public PackageItem(Item.Properties properties, PackageStyles.PackageStyle style) {
        super(properties);
        this.style = style;
        PackageStyles.ALL_BOXES.add(this);
        (style.rare() ? PackageStyles.RARE_BOXES : PackageStyles.STANDARD_BOXES).add(this);
    }

    public String getDescriptionId() {
        return "item.create" + (this.style.rare() ? ".rare_package" : ".package");
    }

    public static boolean isPackage(ItemStack stack) {
        return stack.getItem() instanceof PackageItem;
    }

    public boolean canFitInsideContainerItems() {
        return false;
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public Entity createEntity(Level world, Entity location, ItemStack itemstack) {
        return PackageEntity.fromDroppedItem(world, location, itemstack);
    }

    public static ItemStack containing(List<ItemStack> stacks) {
        ItemStackHandler newInv = new ItemStackHandler(9);
        stacks.forEach(s -> ItemHandlerHelper.insertItemStacked((IItemHandler)newInv, (ItemStack)s, (boolean)false));
        return PackageItem.containing(newInv);
    }

    public static ItemStack containing(ItemStackHandler stacks) {
        ItemStack box = PackageStyles.getRandomBox();
        box.set(AllDataComponents.PACKAGE_CONTENTS, (Object)ItemHelper.containerContentsFromHandler(stacks));
        return box;
    }

    public static void clearAddress(ItemStack box) {
        box.remove(AllDataComponents.PACKAGE_ADDRESS);
    }

    public static void addAddress(ItemStack box, String address) {
        box.set(AllDataComponents.PACKAGE_ADDRESS, (Object)address);
    }

    public static void setOrder(ItemStack box, int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex, boolean isFinal, @Nullable PackageOrderWithCrafts orderContext) {
        PackageOrderData order = new PackageOrderData(orderId, linkIndex, isFinalLink, fragmentIndex, isFinal, orderContext);
        box.set(AllDataComponents.PACKAGE_ORDER_DATA, (Object)order);
    }

    public static int getOrderId(ItemStack box) {
        if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
            return ((PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA)).orderId();
        }
        return -1;
    }

    public static boolean hasOrderData(ItemStack box) {
        return box.has(AllDataComponents.PACKAGE_ORDER_DATA);
    }

    public static int getIndex(ItemStack box) {
        if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
            return ((PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA)).fragmentIndex();
        }
        return -1;
    }

    public static boolean isFinal(ItemStack box) {
        return box.has(AllDataComponents.PACKAGE_ORDER_DATA) && ((PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA)).isFinal();
    }

    public static int getLinkIndex(ItemStack box) {
        if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
            return ((PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA)).linkIndex();
        }
        return -1;
    }

    public static boolean isFinalLink(ItemStack box) {
        return box.has(AllDataComponents.PACKAGE_ORDER_DATA) && ((PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA)).isFinalLink();
    }

    @Nullable
    public static PackageOrderWithCrafts getOrderContext(ItemStack box) {
        if (box.has(AllDataComponents.PACKAGE_ORDER_DATA)) {
            PackageOrderData data = (PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA);
            return data.orderContext();
        }
        if (box.has(AllDataComponents.PACKAGE_ORDER_CONTEXT)) {
            return (PackageOrderWithCrafts)box.get(AllDataComponents.PACKAGE_ORDER_CONTEXT);
        }
        return null;
    }

    public static void addOrderContext(ItemStack box, PackageOrderWithCrafts orderContext) {
        box.set(AllDataComponents.PACKAGE_ORDER_CONTEXT, (Object)orderContext);
    }

    public static boolean matchAddress(ItemStack box, String address) {
        return PackageItem.matchAddress(PackageItem.getAddress(box), address);
    }

    public static boolean matchAddress(String boxAddress, String address) {
        if (address.isBlank()) {
            return boxAddress.isBlank();
        }
        if (address.equals("*") || boxAddress.equals("*")) {
            return true;
        }
        if (address.equals(boxAddress)) {
            return true;
        }
        return address.matches(Glob.toRegexPattern((String)boxAddress, (String)"")) || boxAddress.matches(Glob.toRegexPattern((String)address, (String)""));
    }

    public static String getAddress(ItemStack box) {
        return (String)box.getOrDefault(AllDataComponents.PACKAGE_ADDRESS, (Object)"");
    }

    public static float getWidth(ItemStack box) {
        Item item = box.getItem();
        if (item instanceof PackageItem) {
            PackageItem pi = (PackageItem)item;
            return (float)pi.style.width() / 16.0f;
        }
        return 1.0f;
    }

    public static float getHeight(ItemStack box) {
        Item item = box.getItem();
        if (item instanceof PackageItem) {
            PackageItem pi = (PackageItem)item;
            return (float)pi.style.height() / 16.0f;
        }
        return 1.0f;
    }

    public static float getHookDistance(ItemStack box) {
        Item item = box.getItem();
        if (item instanceof PackageItem) {
            PackageItem pi = (PackageItem)item;
            return pi.style.riggingOffset() / 16.0f;
        }
        return 1.0f;
    }

    public static ItemStackHandler getContents(ItemStack box) {
        ItemStackHandler newInv = new ItemStackHandler(9);
        ItemContainerContents contents = (ItemContainerContents)box.getOrDefault(AllDataComponents.PACKAGE_CONTENTS, (Object)ItemContainerContents.EMPTY);
        ItemHelper.fillItemStackHandler(contents, newInv);
        return newInv;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);
        if (stack.has(AllDataComponents.PACKAGE_ADDRESS)) {
            tooltipComponents.add((Component)Component.literal((String)("\u2192 " + (String)stack.get(AllDataComponents.PACKAGE_ADDRESS))).withStyle(ChatFormatting.GOLD));
        }
        if (!stack.has(AllDataComponents.PACKAGE_CONTENTS)) {
            return;
        }
        int visibleNames = 0;
        int skippedNames = 0;
        ItemStackHandler contents = PackageItem.getContents(stack);
        for (int i = 0; i < contents.getSlots(); ++i) {
            ItemStack itemstack = contents.getStackInSlot(i);
            if (itemstack.isEmpty() || itemstack.getItem() instanceof SpawnEggItem) continue;
            if (visibleNames > 2) {
                ++skippedNames;
                continue;
            }
            ++visibleNames;
            tooltipComponents.add((Component)itemstack.getHoverName().copy().append(" x").append(String.valueOf(itemstack.getCount())).withStyle(ChatFormatting.GRAY));
        }
        if (skippedNames > 0) {
            tooltipComponents.add((Component)Component.translatable((String)"container.shulkerBox.more", (Object[])new Object[]{skippedNames}).withStyle(ChatFormatting.ITALIC));
        }
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> open(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack box = playerIn.getItemInHand(handIn);
        ItemStackHandler contents = PackageItem.getContents(box);
        ItemStack particle = box.copy();
        playerIn.setItemInHand(handIn, box.getCount() <= 1 ? ItemStack.EMPTY : box.copyWithCount(box.getCount() - 1));
        if (!worldIn.isClientSide()) {
            for (int i = 0; i < contents.getSlots(); ++i) {
                ItemStack itemstack = contents.getStackInSlot(i);
                if (itemstack.isEmpty()) continue;
                Item item = itemstack.getItem();
                if (item instanceof SpawnEggItem) {
                    SpawnEggItem sei = (SpawnEggItem)item;
                    if (worldIn instanceof ServerLevel) {
                        ServerLevel sl = (ServerLevel)worldIn;
                        EntityType entitytype = sei.getType(itemstack);
                        Entity entity = entitytype.spawn(sl, itemstack, null, BlockPos.containing((Position)playerIn.position().add(playerIn.getLookAngle().multiply(1.0, 0.0, 1.0).normalize())), MobSpawnType.SPAWN_EGG, false, false);
                        if (entity != null) {
                            itemstack.shrink(1);
                        }
                    }
                }
                playerIn.getInventory().placeItemBackInInventory(itemstack.copy());
            }
        }
        Vec3 position = playerIn.position();
        AllSoundEvents.PACKAGE_POP.playOnServer(worldIn, (Vec3i)playerIn.blockPosition());
        if (worldIn.isClientSide()) {
            for (int i = 0; i < 10; ++i) {
                Vec3 motion = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)worldIn.getRandom(), (float)0.125f);
                Vec3 pos = position.add(0.0, 0.5, 0.0).add(playerIn.getLookAngle().scale(0.5)).add(motion.scale(4.0));
                worldIn.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, particle), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
            }
        }
        return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)box);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer().isShiftKeyDown()) {
            return this.open(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
        }
        Vec3 point = context.getClickLocation();
        float h = (float)this.style.height() / 16.0f;
        float r = (float)this.style.width() / 2.0f / 16.0f;
        if (context.getClickedFace() == Direction.DOWN) {
            point = point.subtract(0.0, (double)(h + 0.25f), 0.0);
        } else if (context.getClickedFace().getAxis().isHorizontal()) {
            point = point.add(Vec3.atLowerCornerOf((Vec3i)context.getClickedFace().getNormal()).scale((double)r));
        }
        AABB scanBB = new AABB(point, point).inflate((double)r, 0.0, (double)r).expandTowards(0.0, (double)h, 0.0);
        Level world = context.getLevel();
        if (!world.getEntities((EntityTypeTest)AllEntityTypes.PACKAGE.get(), scanBB, e -> true).isEmpty()) {
            return super.useOn(context);
        }
        PackageEntity packageEntity = new PackageEntity(world, point.x, point.y, point.z);
        ItemStack itemInHand = context.getItemInHand();
        packageEntity.setBox(itemInHand.copy());
        world.addFreshEntity((Entity)packageEntity);
        itemInHand.shrink(1);
        return InteractionResult.SUCCESS;
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            return this.open(world, player, hand);
        }
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.success((Object)itemstack);
    }

    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int ticks) {
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        int i = this.getUseDuration(stack, entity) - ticks;
        if (i < 0) {
            return;
        }
        float f = PackageItem.getPackageVelocity(i);
        if ((double)f < 0.1) {
            return;
        }
        if (world.isClientSide) {
            return;
        }
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5f, 0.5f);
        ItemStack copy = stack.copy();
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        Vec3 vec = new Vec3(entity.getX(), entity.getY() + entity.getBoundingBox().getYsize() / 2.0, entity.getZ());
        Vec3 motion = entity.getLookAngle().scale((double)(f * 2.0f));
        vec = vec.add(motion);
        PackageEntity packageEntity = new PackageEntity(world, vec.x, vec.y, vec.z);
        packageEntity.setBox(copy);
        packageEntity.setDeltaMovement(motion);
        packageEntity.tossedBy = new WeakReference<Player>(player);
        world.addFreshEntity((Entity)packageEntity);
    }

    public static float getPackageVelocity(int p_185059_0_) {
        float f = (float)p_185059_0_ / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    public record PackageOrderData(int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex, boolean isFinal, @Nullable PackageOrderWithCrafts orderContext) {
        public static final Codec<PackageOrderData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("order_id").forGetter(PackageOrderData::orderId), (App)Codec.INT.fieldOf("link_index").forGetter(PackageOrderData::linkIndex), (App)Codec.BOOL.fieldOf("is_final_link").forGetter(PackageOrderData::isFinalLink), (App)Codec.INT.fieldOf("fragment_index").forGetter(PackageOrderData::fragmentIndex), (App)Codec.BOOL.fieldOf("is_final").forGetter(PackageOrderData::isFinal), (App)PackageOrderWithCrafts.CODEC.optionalFieldOf("order_context").forGetter(i -> Optional.ofNullable(i.orderContext))).apply((Applicative)instance, PackageOrderData::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, PackageOrderData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, PackageOrderData::orderId, (StreamCodec)ByteBufCodecs.INT, PackageOrderData::linkIndex, (StreamCodec)ByteBufCodecs.BOOL, PackageOrderData::isFinalLink, (StreamCodec)ByteBufCodecs.INT, PackageOrderData::fragmentIndex, (StreamCodec)ByteBufCodecs.BOOL, PackageOrderData::isFinal, (StreamCodec)CatnipStreamCodecBuilders.nullable(PackageOrderWithCrafts.STREAM_CODEC), PackageOrderData::orderContext, PackageOrderData::new);

        public PackageOrderData(int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex, boolean isFinal, Optional<PackageOrderWithCrafts> orderContext) {
            this(orderId, linkIndex, isFinalLink, fragmentIndex, isFinal, (PackageOrderWithCrafts)orderContext.orElse(null));
        }
    }
}
