/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint$Mode
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.mechanical_arm;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import java.util.Optional;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record PointHolder(BlockPos pos, ArmInteractionPoint.Mode interactionMode, MutableBoolean covered) {
    public CompoundTag serialize(BlockPos anchor) {
        CompoundTag tag = new CompoundTag();
        Tag pos = NbtUtils.writeBlockPos((BlockPos)this.pos.subtract((Vec3i)anchor));
        tag.put("pos", pos);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"mode", (Enum)this.interactionMode);
        return tag;
    }

    public static PointHolder deserialize(CompoundTag tag, BlockPos anchor) {
        Optional pos = NbtUtils.readBlockPos((CompoundTag)tag, (String)"pos");
        return pos.map(blockPos -> new PointHolder(blockPos.offset((Vec3i)anchor), (ArmInteractionPoint.Mode)NBTHelper.readEnum((CompoundTag)tag, (String)"mode", ArmInteractionPoint.Mode.class), new MutableBoolean(false))).orElse(null);
    }
}
