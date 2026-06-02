/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.ContraptionType
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.bearing.BearingContraption
 *  dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper
 *  dev.simulated_team.simulated.service.SimInventoryService
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.ryanhcode.offroad.content.contraptions.borehead_contraption;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlock;
import dev.ryanhcode.offroad.data.OffroadLang;
import dev.ryanhcode.offroad.index.OffroadContraptionTypes;
import dev.ryanhcode.offroad.mixin.MountedStorageAccessor;
import dev.ryanhcode.offroad.service.OffroadMountedStorageService;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.service.SimInventoryService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

public class BoreheadBearingContraption
extends BearingContraption {
    public int rockCuttingWheelAmount = 0;
    private InventoryLoaderWrapper multiLoaderWrappedInventory;

    public BoreheadBearingContraption() {
        this.storage = OffroadMountedStorageService.INSTANCE.getSidedBoreheadContraptionMountedStorage();
    }

    public BoreheadBearingContraption(Direction direction) {
        super(false, direction);
        this.storage = OffroadMountedStorageService.INSTANCE.getSidedBoreheadContraptionMountedStorage();
    }

    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        boolean assemble = super.assemble(world, pos);
        if (this.rockCuttingWheelAmount < 1) {
            throw new AssemblyException((Component)OffroadLang.translate("exceptions.borehead_bearing.insuffecient_rockcutting_wheels", new Object[0]).component());
        }
        if (((MountedStorageAccessor)this.storage).getItemsBuilder().isEmpty()) {
            throw new AssemblyException((Component)OffroadLang.translate("exceptions.borehead_bearing.insuffecient_inventory_blocks", new Object[0]).component());
        }
        return assemble;
    }

    public ContraptionType getType() {
        return (ContraptionType)OffroadContraptionTypes.BOREHEAD_CONTRAPTION_TYPE.get();
    }

    public void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture) {
        super.addBlock(level, pos, capture);
        if (((StructureTemplate.StructureBlockInfo)capture.getKey()).state().getBlock() instanceof RockCuttingWheelBlock) {
            ++this.rockCuttingWheelAmount;
        }
    }

    public InventoryLoaderWrapper getSimWrappedStorage() {
        if (this.multiLoaderWrappedInventory == null) {
            this.multiLoaderWrappedInventory = SimInventoryService.INSTANCE.getWrappedAllItemsFromContraption(this.getStorage());
        }
        return this.multiLoaderWrappedInventory;
    }
}
