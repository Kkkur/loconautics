/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.armor.BacktankUtil
 *  com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods
 *  com.simibubi.create.foundation.item.CustomArmPoseItem
 *  com.simibubi.create.foundation.utility.RaycastHelper
 *  dev.ryanhcode.sable.Sable
 *  foundry.veil.api.network.VeilPacketManager
 *  net.minecraft.client.model.HumanoidModel$ArmPose
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.items.plunger_launcher;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerEntity;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerServerHandler;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimEntityTypes;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.mixin_interface.PlayerLaunchedPlungerExtension;
import dev.simulated_team.simulated.network.packets.PlungerLauncherShootPacket;
import dev.simulated_team.simulated.service.SimConfigService;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlungerLauncherItem
extends Item
implements CustomArmPoseItem {
    public static boolean reloadCooldown = false;

    public PlungerLauncherItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack heldStack = player.getItemInHand(interactionHand);
        if (ShootableGadgetItemMethods.shouldSwap((Player)player, (ItemStack)heldStack, (InteractionHand)interactionHand, s -> s.getItem() instanceof PlungerLauncherItem)) {
            return InteractionResultHolder.fail((Object)heldStack);
        }
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                LaunchedPlungerServerHandler.removePlayerPlungers(player);
                player.displayClientMessage((Component)SimLang.translate("plunger_launcher.clear_plungers", new Object[0]).color(0xAAAAAA).component(), true);
                return InteractionResultHolder.success((Object)heldStack);
            }
            BarrelAndCorrectionInfo info = this.getCorrectionInfo(player, interactionHand);
            Vec3 barrelPos = info.barrelPos();
            level.playSound(null, barrelPos.x, barrelPos.y, barrelPos.z, SimSoundEvents.PLUNGER_LAUNCH.event(), SoundSource.PLAYERS, 1.0f, 1.0f);
            LaunchedPlungerEntity newPlunger = (LaunchedPlungerEntity)SimEntityTypes.PLUNGER.create(level);
            newPlunger.setPos(barrelPos);
            newPlunger.shootFromRotation((Entity)player, player.getXRot(), player.getYRot(), 0.0f, 0.5f, 0.0f);
            newPlunger.setOldPosAndRot();
            newPlunger.setDeltaMovement(info.motion());
            newPlunger.setOwner((Entity)player);
            level.addFreshEntity((Entity)newPlunger);
            PlayerLaunchedPlungerExtension duck = (PlayerLaunchedPlungerExtension)player;
            LaunchedPlungerEntity plunger = duck.simulated$getLaunchedPlunger();
            if (plunger == null || plunger.isRemoved()) {
                newPlunger.setData(LaunchedPlungerEntity.IS_FIRST, true);
                duck.simulated$setLaunchedPlunger(newPlunger);
                ShootableGadgetItemMethods.applyCooldown((Player)player, (ItemStack)heldStack, (InteractionHand)interactionHand, b -> b.getItem() instanceof PlungerLauncherItem, (int)4);
                reloadCooldown = false;
            } else {
                duck.simulated$setLaunchedPlunger(null);
                newPlunger.setOther(plunger);
                plunger.setOther(newPlunger);
                ShootableGadgetItemMethods.applyCooldown((Player)player, (ItemStack)heldStack, (InteractionHand)interactionHand, b -> b.getItem() instanceof PlungerLauncherItem, (int)16);
                reloadCooldown = true;
            }
            VeilPacketManager.player((ServerPlayer)((ServerPlayer)player)).sendPacket(new CustomPacketPayload[]{new PlungerLauncherShootPacket(interactionHand)});
            if (!BacktankUtil.canAbsorbDamage((LivingEntity)player, (int)PlungerLauncherItem.maxUses())) {
                heldStack.hurtAndBreak(1, (LivingEntity)player, LivingEntity.getSlotForHand((InteractionHand)interactionHand));
            }
        } else {
            SimulatedClient.PLUNGER_LAUNCHER_RENDER_HANDLER.dontAnimateItem(interactionHand);
        }
        return InteractionResultHolder.success((Object)heldStack);
    }

    public boolean isBarVisible(ItemStack stack) {
        return BacktankUtil.isBarVisible((ItemStack)stack, (int)PlungerLauncherItem.maxUses());
    }

    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth((ItemStack)stack, (int)PlungerLauncherItem.maxUses());
    }

    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor((ItemStack)stack, (int)PlungerLauncherItem.maxUses());
    }

    private static int maxUses() {
        return (Integer)SimConfigService.INSTANCE.server().equipment.maxPlungerLauncherShots.get();
    }

    @NotNull
    public BarrelAndCorrectionInfo getCorrectionInfo(Player player, InteractionHand interactionHand) {
        Vec3 barrelPos = ShootableGadgetItemMethods.getGunBarrelVec((Player)player, (interactionHand == InteractionHand.MAIN_HAND ? 1 : 0) != 0, (Vec3)new Vec3((double)0.825f, (double)-0.3f, 1.5));
        Level level = player.level();
        barrelPos = level.clip(new ClipContext(player.getEyePosition(), barrelPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty())).getLocation();
        barrelPos = Sable.HELPER.projectOutOfSubLevel(level, barrelPos);
        Vec3 motion = player.getLookAngle();
        BlockHitResult hit = RaycastHelper.rayTraceRange((Level)level, (Player)player, (double)48.0);
        if (hit != null) {
            Vec3 projectedHit = Sable.HELPER.projectOutOfSubLevel(level, hit.getLocation());
            motion = projectedHit.subtract(barrelPos).normalize().scale((double)1.35f);
        }
        return new BarrelAndCorrectionInfo(barrelPos, motion);
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable HumanoidModel.ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        return HumanoidModel.ArmPose.CROSSBOW_HOLD;
    }

    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    public int getEnchantmentValue() {
        return 1;
    }

    public record BarrelAndCorrectionInfo(Vec3 barrelPos, Vec3 motion) {
    }
}
