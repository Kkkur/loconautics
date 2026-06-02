/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.material.FluidState
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendDummyInterface;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendHelper;
import dev.eriksonn.aeronautics.index.AeroRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class LevititeBlendTicker {
    private final BlockPos pos;
    private final Level level;
    private final CrystalPropagationContext context;
    public int age;
    public int attempts = 0;
    public boolean isDormant;
    private boolean requiresCatalyst;

    public LevititeBlendTicker(CompoundTag toDeserialize, Level level) {
        this.level = level;
        this.pos = (BlockPos)NbtUtils.readBlockPos((CompoundTag)toDeserialize, (String)"pos").get();
        this.context = (CrystalPropagationContext)AeroRegistries.LEVITITE_CRYSTAL_PROPAGATION_CONTEXT.asVanillaRegistry().get(ResourceLocation.parse((String)toDeserialize.getString("context")));
        this.deserialize(toDeserialize);
    }

    public LevititeBlendTicker(int age, @NotNull BlockPos pos, @NotNull Level level, boolean requiresCatalyst, boolean isDormant, @NotNull CrystalPropagationContext context) {
        this.age = age;
        this.pos = pos;
        this.level = level;
        this.requiresCatalyst = requiresCatalyst;
        this.context = context;
        this.isDormant = isDormant;
    }

    public boolean tick() {
        if (this.age > 0) {
            if (this.checkSurroundingCatalyst()) {
                return true;
            }
            --this.age;
        } else {
            FluidState state = this.level.getBlockState(this.pos).getFluidState();
            if (state.getType() instanceof LevititeBlendDummyInterface) {
                if (this.context.shouldCrystallize(this.level, this.attempts, this.isDormant)) {
                    LevititeBlendHelper.crystallizeLevititeBlend(this.level, this.pos, this.context);
                    return true;
                }
                this.context.onCrystallizationFail(this.level, this.pos, this.attempts, this.isDormant);
                this.age = this.context.getNewAge(this.level, this.attempts, this.isDormant);
                ++this.attempts;
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean checkSurroundingCatalyst() {
        if (this.requiresCatalyst) {
            for (Direction dir : Direction.values()) {
                CrystalPropagationContext ctx = LevititeBlendHelper.getContextFromBlock(this.level, this.pos.relative(dir));
                if (ctx != this.context) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public LevelAccessor getLevel() {
        return this.level;
    }

    public CrystalPropagationContext getContext() {
        return this.context;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("age", this.age);
        tag.putInt("attempts", this.attempts);
        tag.putBoolean("requiresCatalyst", this.requiresCatalyst);
        tag.put("pos", NbtUtils.writeBlockPos((BlockPos)this.getPos()));
        ResourceLocation resourceLocation = AeroRegistries.LEVITITE_CRYSTAL_PROPAGATION_CONTEXT.asVanillaRegistry().getKey((Object)this.context);
        tag.putString("context", resourceLocation.toString());
        tag.putBoolean("isDormant", this.isDormant);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        this.age = tag.getInt("age");
        this.attempts = tag.getInt("attempts");
        this.requiresCatalyst = tag.getBoolean("requiresCatalyst");
        this.isDormant = tag.getBoolean("isDormant");
    }
}
