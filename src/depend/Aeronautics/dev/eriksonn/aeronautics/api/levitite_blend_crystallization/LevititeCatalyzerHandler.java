/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.utility.RaycastHelper
 *  dev.simulated_team.simulated.util.SimDistUtil
 *  dev.simulated_team.simulated.util.click_interactions.InteractCallback
 *  dev.simulated_team.simulated.util.click_interactions.InteractCallback$Result
 *  foundry.veil.api.network.VeilPacketManager
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendHelper;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.eriksonn.aeronautics.network.packets.LevititeCatalystCrystallizationPacket;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LevititeCatalyzerHandler
implements InteractCallback {
    @NotNull
    private static ClipContext gatherContext(Player player) {
        Vec3 origin = player.getEyePosition();
        Vec3 target = RaycastHelper.getTraceTarget((Player)player, (double)player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), (Vec3)origin);
        return new ClipContext(origin, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, (Entity)player);
    }

    public static boolean isCatalyzer(ItemStack item) {
        return item.is(AeroTags.ItemTags.LEVITITE_CATALYZER) || item.is(AeroTags.ItemTags.LEVITITE_SOUL_CATALYZER);
    }

    public InteractCallback.Result onUse(int modifiers, int action, KeyMapping rightKey) {
        if (action == 1) {
            LocalPlayer player = (LocalPlayer)SimDistUtil.getClientPlayer();
            Level level = player.level();
            InteractionHand hand = InteractionHand.MAIN_HAND;
            ItemStack catalyzer = player.getItemInHand(hand);
            if (!LevititeCatalyzerHandler.isCatalyzer(catalyzer)) {
                return InteractCallback.Result.empty();
            }
            ClipContext context = LevititeCatalyzerHandler.gatherContext((Player)player);
            BlockHitResult ray = level.clip(context);
            if (ray.getType() != HitResult.Type.MISS && level.getFluidState(ray.getBlockPos()).getType() == LevititeBlendHelper.getFluid()) {
                VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new LevititeCatalystCrystallizationPacket(ray.getBlockPos(), hand)});
                player.swing(hand);
                return new InteractCallback.Result(true);
            }
        }
        return InteractCallback.Result.empty();
    }
}
