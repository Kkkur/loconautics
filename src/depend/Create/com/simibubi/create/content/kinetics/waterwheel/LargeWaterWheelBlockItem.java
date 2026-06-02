/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class LargeWaterWheelBlockItem
extends BlockItem {
    public LargeWaterWheelBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult result = super.place(ctx);
        if (result != InteractionResult.FAIL) {
            return result;
        }
        Direction clickedFace = ctx.getClickedFace();
        if (clickedFace.getAxis() != ((LargeWaterWheelBlock)this.getBlock()).getAxisForPlacement(ctx)) {
            result = super.place(BlockPlaceContext.at((BlockPlaceContext)ctx, (BlockPos)ctx.getClickedPos().relative(clickedFace), (Direction)clickedFace));
        }
        if (result == InteractionResult.FAIL && ctx.getLevel().isClientSide()) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.showBounds(ctx));
        }
        return result;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void showBounds(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction.Axis axis = ((LargeWaterWheelBlock)this.getBlock()).getAxisForPlacement(context);
        Vec3 contract = Vec3.atLowerCornerOf((Vec3i)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis).getNormal());
        Player player = context.getPlayer();
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer localPlayer = (LocalPlayer)player;
        Outliner.getInstance().showAABB((Object)Pair.of((Object)"waterwheel", (Object)pos), new AABB(pos).inflate(1.0).deflate(contract.x, contract.y, contract.z)).colored(-41620);
        CreateLang.translate("large_water_wheel.not_enough_space", new Object[0]).color(-41620).sendStatus((Player)localPlayer);
    }
}
