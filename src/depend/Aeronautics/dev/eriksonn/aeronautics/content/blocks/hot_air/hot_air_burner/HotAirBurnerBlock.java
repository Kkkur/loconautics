/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.foundation.block.IBE
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.math.LevelReusedVectors
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.LevelExtension
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.eriksonn.aeronautics.index.AeroBlockShapes;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.LevelExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class HotAirBurnerBlock
extends Block
implements IBE<HotAirBurnerBlockEntity>,
IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Variant> VARIANT = EnumProperty.create((String)"variant", Variant.class);

    public HotAirBurnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static int getLightPower(BlockState state) {
        return (Boolean)state.getValue((Property)POWERED) != false ? 15 : 0;
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HotAirBurnerBlockEntity) {
            HotAirBurnerBlockEntity be = (HotAirBurnerBlockEntity)blockEntity;
            if (!entity.fireImmune() && ((Boolean)state.getValue((Property)POWERED)).booleanValue() && entity instanceof LivingEntity) {
                Vector3d sideLengths;
                AABB entityAABB;
                Vector3d entityCenter;
                OrientedBoundingBox3d entityBounds;
                OrientedBoundingBox3d burnerBounds;
                LevelReusedVectors jomlSink = ((LevelExtension)level).sable$getJOMLSink();
                SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)pos);
                Vector3d burnerCubePos = JOMLConversion.atCenterOf((Vec3i)pos).add(0.0, 0.25, 0.0);
                if (subLevel != null) {
                    subLevel.logicalPose().transformPosition(burnerCubePos);
                }
                if (OrientedBoundingBox3d.sat((OrientedBoundingBox3d)(burnerBounds = new OrientedBoundingBox3d((Vector3dc)burnerCubePos, (Vector3dc)new Vector3d(0.625), (Quaterniondc)(subLevel != null ? subLevel.logicalPose().orientation() : JOMLConversion.QUAT_IDENTITY), jomlSink)), (OrientedBoundingBox3d)(entityBounds = new OrientedBoundingBox3d((Vector3dc)(entityCenter = JOMLConversion.toJOML((Position)(entityAABB = entity.getBoundingBox()).getCenter())), (Vector3dc)(sideLengths = new Vector3d(entityAABB.getXsize(), entityAABB.getYsize(), entityAABB.getZsize())), JOMLConversion.QUAT_IDENTITY, jomlSink))).lengthSquared() > 0.0) {
                    entity.hurt(level.damageSources().inFire(), (float)be.getSignalStrength() / 7.5f);
                }
            }
            super.entityInside(state, level, pos, entity);
        }
    }

    public Class<HotAirBurnerBlockEntity> getBlockEntityClass() {
        return HotAirBurnerBlockEntity.class;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED, VARIANT});
        super.createBlockStateDefinition(builder);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Variant current;
        Variant conversion = Variant.getConversionFromItem(stack.getItem());
        if (conversion != null && conversion != (current = (Variant)((Object)state.getValue(VARIANT)))) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(VARIANT, (Comparable)((Object)conversion)));
            level.playLocalSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), conversion.sound, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, HotAirBurnerBlockEntity::updateSignal);
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != level.hasNeighborSignal(pos)) {
            level.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
    }

    public BlockEntityType<? extends HotAirBurnerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.HOT_AIR_BURNER.get();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext == CollisionContext.empty()) {
            return AeroBlockShapes.HOT_AIR_BURNER_SMOKE_CLIP;
        }
        return AeroBlockShapes.HOT_AIR_BURNER;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AeroBlockShapes.HOT_AIR_BURNER_PLAYER_COLLISION;
    }

    public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AeroBlockShapes.HOT_AIR_BURNER;
    }

    public static enum Variant implements StringRepresentable
    {
        FIRE("fire", SoundEvents.NETHERRACK_PLACE),
        SOUL_FIRE("soulful", SoundEvents.SOUL_SAND_PLACE);

        public final String name;
        public final SoundEvent sound;

        private Variant(String name, SoundEvent sound) {
            this.name = name;
            this.sound = sound;
        }

        public static Variant getConversionFromItem(Item item) {
            if (item.builtInRegistryHolder().is(AeroTags.ItemTags.BURNER_FIRE)) {
                return FIRE;
            }
            if (item.builtInRegistryHolder().is(ItemTags.SOUL_FIRE_BASE_BLOCKS)) {
                return SOUL_FIRE;
            }
            return null;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
