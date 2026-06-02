/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.AllDataComponents
 *  com.simibubi.create.content.contraptions.StructureTransform
 *  com.simibubi.create.content.schematics.SchematicItem
 *  com.simibubi.create.content.schematics.SchematicPrinter
 *  com.simibubi.create.content.schematics.packet.SchematicPlacePacket
 *  com.simibubi.create.foundation.utility.BlockHelper
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  it.unimi.dsi.fastutil.Function
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import com.simibubi.create.foundation.utility.BlockHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics.SchematicLevelExtension;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics.SchematicPrinterExtension;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SchematicPlacePacket.class})
public class SchematicPlacePacketMixin {
    @Shadow
    @Final
    private ItemStack stack;

    @Inject(method={"handle"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/schematics/SchematicPrinter;loadSchematic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Z)V", shift=At.Shift.AFTER)}, cancellable=true)
    private void sable$preHandle(ServerPlayer player, CallbackInfo ci, @Local SchematicPrinter printer) {
        SchematicLevel schematicLevel;
        Mirror mirror = (Mirror)this.stack.get(AllDataComponents.SCHEMATIC_MIRROR);
        if (mirror != null && mirror != Mirror.NONE && !((SchematicLevelExtension)(schematicLevel = ((SchematicPrinterExtension)printer).sable$getSchematicLevel())).sable$getSubLevels().isEmpty()) {
            player.sendSystemMessage((Component)Component.translatable((String)"schematic.sable.mirror_not_supported").withStyle(ChatFormatting.RED));
            ci.cancel();
        }
    }

    @Inject(method={"handle"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/infrastructure/config/AllConfigs;server()Lcom/simibubi/create/infrastructure/config/CServer;")})
    private void sable$handle(ServerPlayer player, CallbackInfo ci, @Local Level level, @Local SchematicPrinter printer) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        SubLevelPhysicsSystem physicsSystem = ((ServerSubLevelContainer)container).physicsSystem();
        SchematicLevel schematicLevel = ((SchematicPrinterExtension)printer).sable$getSchematicLevel();
        List<SchematicLevelExtension.SchematicSubLevel> subLevels = ((SchematicLevelExtension)schematicLevel).sable$getSubLevels();
        BlockPos minPos = printer.getAnchor();
        SubLevelSchematicSerializationContext context = new SubLevelSchematicSerializationContext(SubLevelSchematicSerializationContext.Type.PLACE, null);
        StructurePlaceSettings settings = SchematicItem.getSettings((ItemStack)this.stack, (!player.canUseGameMasterBlocks() ? 1 : 0) != 0);
        StructureTransform transform = new StructureTransform(settings.getRotationPivot(), Direction.Axis.Y, settings.getRotation(), settings.getMirror());
        context.setSetupTransform((Function<BlockPos, BlockPos>)((Function)block -> transform.apply((BlockPos)block)));
        context.setPlaceTransform((Function<BlockPos, BlockPos>)((Function)block -> ((BlockPos)block).offset((Vec3i)minPos)));
        Object2ObjectOpenHashMap spawnedSubLevels = new Object2ObjectOpenHashMap();
        for (SchematicLevelExtension.SchematicSubLevel schematicSubLevel : subLevels) {
            Pose3d pose = new Pose3d();
            pose.orientation().set((Quaterniondc)schematicSubLevel.orientation());
            pose.position().set((Vector3dc)schematicSubLevel.position());
            SubLevel subLevel = container.allocateNewSubLevel(pose);
            Function blockFunction = block -> ((BlockPos)block).offset((Vec3i)subLevel.getPlot().getCenterBlock());
            SubLevelSchematicSerializationContext.SchematicMapping mapping = new SubLevelSchematicSerializationContext.SchematicMapping(null, null, subLevel.getUniqueId(), (Function<BlockPos, BlockPos>)blockFunction);
            context.getMappings().put(schematicSubLevel.uuid(), mapping);
            spawnedSubLevels.put(schematicSubLevel.uuid(), subLevel);
        }
        SubLevelSchematicSerializationContext.setCurrentContext(context);
        for (SchematicLevelExtension.SchematicSubLevel schematicSubLevel : subLevels) {
            SubLevel subLevel = (SubLevel)spawnedSubLevels.get(schematicSubLevel.uuid());
            schematicSubLevel.position().add((double)minPos.getX(), (double)minPos.getY(), (double)minPos.getZ());
            SchematicLevel subSchematicLevel = schematicSubLevel.level();
            BoundingBox schematicBounds = subSchematicLevel.getBounds();
            LevelPlot plot = subLevel.getPlot();
            BlockPos centerBlock = plot.getCenterBlock();
            int minChunkX = centerBlock.getX() + schematicBounds.minX() >> 4;
            int minChunkZ = centerBlock.getZ() + schematicBounds.minZ() >> 4;
            int maxChunkX = centerBlock.getX() + schematicBounds.maxX() >> 4;
            int maxChunkZ = centerBlock.getZ() + schematicBounds.maxZ() >> 4;
            for (int x = minChunkX; x <= maxChunkX; ++x) {
                for (int z = minChunkZ; z <= maxChunkZ; ++z) {
                    plot.newEmptyChunk(new ChunkPos(x, z));
                }
            }
            BlockPos.betweenClosedStream((BoundingBox)schematicBounds).forEach(block -> {
                BlockState state = subSchematicLevel.getBlockState(block);
                BlockEntity blockEntity = subSchematicLevel.getBlockEntity(block);
                CompoundTag data = BlockHelper.prepareBlockEntityData((Level)level, (BlockState)state, (BlockEntity)blockEntity);
                BlockHelper.placeSchematicBlock((Level)level, (BlockState)state, (BlockPos)centerBlock.offset((Vec3i)block), null, (CompoundTag)data);
            });
            subLevel.logicalPose().position().add((Vector3dc)schematicSubLevel.position().sub((Vector3dc)subLevel.logicalPose().transformPosition(new Vector3d((double)centerBlock.getX(), (double)centerBlock.getY(), (double)centerBlock.getZ()))));
            SubLevel containingSubLevel = Sable.HELPER.getContaining(level, (Vector3dc)subLevel.logicalPose().position());
            PhysicsPipeline pipeline = physicsSystem.getPipeline();
            if (containingSubLevel != null && level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                SubLevelAssemblyHelper.kickFromContainingSubLevel(serverLevel, physicsSystem, pipeline, (ServerSubLevel)subLevel, containingSubLevel);
                subLevel.logicalPose().orientation().premul((Quaterniondc)containingSubLevel.logicalPose().orientation());
            }
            pipeline.teleport((ServerSubLevel)subLevel, (Vector3dc)subLevel.logicalPose().position(), (Quaterniondc)subLevel.logicalPose().orientation());
            subLevel.updateLastPose();
            for (Entity entity : subSchematicLevel.getEntityList()) {
                entity.setPos(entity.position().add((double)centerBlock.getX(), (double)centerBlock.getY(), (double)centerBlock.getZ()));
                level.addFreshEntity(entity);
            }
        }
    }

    @Inject(method={"handle"}, at={@At(value="TAIL")})
    private void sable$postHandle(ServerPlayer player, CallbackInfo ci) {
        SubLevelSchematicSerializationContext.setCurrentContext(null);
    }
}
