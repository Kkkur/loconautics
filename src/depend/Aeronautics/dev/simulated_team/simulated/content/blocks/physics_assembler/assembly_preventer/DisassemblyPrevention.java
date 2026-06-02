/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.physics_assembler.assembly_preventer;

import com.simibubi.create.content.contraptions.AssemblyException;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.mixin_interface.assembly_preventer.PrimaryAssemblerExtension;
import dev.simulated_team.simulated.service.SimConfigService;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DisassemblyPrevention {
    private static final LangBuilder ERR = SimLang.builder().translate("prevent_disassembly", new Object[0]);

    public static boolean checkSubLevelForPrimary(Level level, BlockPos toCheck) throws AssemblyException {
        BlockEntity blockEntity;
        ServerSubLevel ssl;
        BlockPos primary;
        if (!((Boolean)SimConfigService.INSTANCE.server().assembly.primaryDisassembly.get()).booleanValue() || level == null & toCheck == null) {
            return true;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)toCheck);
        if (subLevel instanceof ServerSubLevel && (primary = ((PrimaryAssemblerExtension)(ssl = (ServerSubLevel)subLevel)).simulated$getPrimaryAssembler()) != null && (blockEntity = level.getBlockEntity(primary)) instanceof PhysicsAssemblerBlockEntity) {
            PhysicsAssemblerBlockEntity psbe = (PhysicsAssemblerBlockEntity)blockEntity;
            if (!toCheck.equals((Object)primary) || !psbe.isPrimaryAssembler()) {
                throw new AssemblyException((Component)ERR.component());
            }
        }
        return true;
    }
}
