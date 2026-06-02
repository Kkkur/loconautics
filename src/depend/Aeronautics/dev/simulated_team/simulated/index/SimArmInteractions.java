/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.registry.CreateBuiltInRegistries
 *  com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes$DepotPoint
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
package dev.simulated_team.simulated.index;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SimArmInteractions {
    private static <T extends ArmInteractionPointType> void register(String name, T type) {
        Registry.register((Registry)CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, (ResourceLocation)Simulated.path(name), type);
    }

    public static void init() {
    }

    static {
        SimArmInteractions.register("portable_engine", new PortableEngineType());
        SimArmInteractions.register("navigation_table", new NavTableType());
    }

    public static class PortableEngineType
    extends ArmInteractionPointType {
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return SimBlocks.PORTABLE_ENGINES.contains(state.getBlock());
        }

        @Nullable
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new PortableEngineInteractionPoint(this, level, pos, state);
        }
    }

    public static class NavTableType
    extends ArmInteractionPointType {
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return SimBlocks.NAVIGATION_TABLE.has(state);
        }

        @Nullable
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new NavTablePoint(this, level, pos, state);
        }
    }

    public static class NavTablePoint
    extends AllArmInteractionPointTypes.DepotPoint {
        public NavTablePoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }
    }

    public static class PortableEngineInteractionPoint
    extends AllArmInteractionPointTypes.DepotPoint {
        public PortableEngineInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            BlockEntity be;
            if (this.cachedState.hasBlockEntity() && (be = this.level.getBlockEntity(this.pos)) instanceof PortableEngineBlockEntity) {
                PortableEngineBlockEntity sbe = (PortableEngineBlockEntity)be;
                return sbe.inventory.insertSlot(stack, 0, simulate);
            }
            return super.insert(armBlockEntity, stack, simulate);
        }
    }
}
