/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity
 *  com.simibubi.create.content.equipment.toolbox.ToolboxHandler
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import dev.ryanhcode.sable.Sable;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ToolboxHandler.class})
public abstract class ToolboxHandlerMixin {
    @Shadow
    @Final
    public static WorldAttached<WeakHashMap<BlockPos, ToolboxBlockEntity>> toolboxes;

    @Inject(method={"withinRange"}, at={@At(value="HEAD")}, remap=false, cancellable=true)
    private static void sable$withinRangeToolBoxRedirect(Player player, ToolboxBlockEntity box, CallbackInfoReturnable<Boolean> cir) {
        if (player.level() != box.getLevel()) {
            cir.setReturnValue((Object)false);
        }
        double maxRange = ToolboxHandler.getMaxRange((Player)player);
        cir.setReturnValue((Object)(ToolboxHandlerMixin.sable$getDistance((LevelAccessor)player.level(), player.position(), box.getBlockPos()) < maxRange * maxRange ? 1 : 0));
    }

    @Inject(method={"getNearest"}, at={@At(value="HEAD")}, remap=false, cancellable=true)
    private static void sable$getNearestToolBoxRedirect(LevelAccessor world, Player player, int maxAmount, CallbackInfoReturnable<List<ToolboxBlockEntity>> cir) {
        Vec3 location = player.position();
        double maxRange = ToolboxHandler.getMaxRange((Player)player);
        cir.setReturnValue(((WeakHashMap)toolboxes.get(world)).keySet().stream().filter(p -> ToolboxHandlerMixin.sable$getDistance(world, location, p) < maxRange * maxRange).sorted(Comparator.comparingDouble(p -> ToolboxHandlerMixin.sable$getDistance(world, location, p))).limit(maxAmount).map(((WeakHashMap)toolboxes.get(world))::get).filter(ToolboxBlockEntity::isFullyInitialized).collect(Collectors.toList()));
    }

    @Unique
    private static double sable$getDistance(LevelAccessor level, Vec3 pos, BlockPos bPos) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)level, (Position)pos, (double)bPos.getX() + 0.5, (double)bPos.getY(), (double)bPos.getZ() + 0.5);
    }
}
