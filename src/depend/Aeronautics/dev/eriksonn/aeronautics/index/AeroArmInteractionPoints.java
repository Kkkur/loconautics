/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.registry.CreateBuiltInRegistries
 *  com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes$DepositOnlyArmInteractionPoint
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AeroArmInteractionPoints {
    private static <T extends ArmInteractionPointType> void register(String name, T type) {
        Registry.register((Registry)CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, (ResourceLocation)Aeronautics.path(name), type);
    }

    public static void init() {
    }

    static {
        AeroArmInteractionPoints.register("mounted_potato_cannon_point", new MountedPotatoCannonType());
    }

    public static class MountedPotatoCannonType
    extends ArmInteractionPointType {
        public boolean canCreatePoint(Level var1, BlockPos var2, BlockState var3) {
            return AeroBlocks.MOUNTED_POTATO_CANNON.has(var1.getBlockState(var2));
        }

        @Nullable
        public ArmInteractionPoint createPoint(Level var1, BlockPos var2, BlockState var3) {
            return new MountedPotatoCannonPoint(this, var1, var2, var3);
        }
    }

    public static class MountedPotatoCannonPoint
    extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
        public MountedPotatoCannonPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            BlockEntity be;
            if (this.cachedState.hasBlockEntity() && (be = this.level.getBlockEntity(this.pos)) instanceof MountedPotatoCannonBlockEntity) {
                MountedPotatoCannonBlockEntity sbe = (MountedPotatoCannonBlockEntity)be;
                return sbe.getInventory().insertSlot(stack, 0, simulate);
            }
            return super.insert(armBlockEntity, stack, simulate);
        }
    }
}
