/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CarriageBogey {
    public static final StreamCodec<RegistryFriendlyByteBuf, CarriageBogey> STREAM_CODEC = StreamCodec.composite(AbstractBogeyBlock.STREAM_CODEC, bogey -> bogey.type, (StreamCodec)ByteBufCodecs.BOOL, bogey -> bogey.upsideDown, (StreamCodec)ByteBufCodecs.COMPOUND_TAG, bogey -> bogey.bogeyData, CarriageBogey::new);
    public static final String UPSIDE_DOWN_KEY = "UpsideDown";
    public Carriage carriage;
    boolean isLeading;
    public CompoundTag bogeyData;
    AbstractBogeyBlock<?> type;
    boolean upsideDown;
    Couple<TravellingPoint> points;
    LerpedFloat wheelAngle;
    LerpedFloat yaw;
    LerpedFloat pitch;
    public Couple<Vec3> couplingAnchors;
    int derailAngle;

    public CarriageBogey(AbstractBogeyBlock<?> type, boolean upsideDown, CompoundTag bogeyData) {
        this(type, upsideDown, bogeyData, new TravellingPoint(), new TravellingPoint());
    }

    public CarriageBogey(AbstractBogeyBlock<?> type, boolean upsideDown, CompoundTag bogeyData, TravellingPoint point, TravellingPoint point2) {
        this.type = type;
        point.upsideDown = this.upsideDown = type.canBeUpsideDown() && upsideDown;
        point2.upsideDown = this.upsideDown;
        if (bogeyData == null || bogeyData.isEmpty()) {
            bogeyData = this.createBogeyData();
        }
        bogeyData.putBoolean(UPSIDE_DOWN_KEY, upsideDown);
        this.bogeyData = bogeyData;
        this.points = Couple.create((Object)point, (Object)point2);
        this.wheelAngle = LerpedFloat.angular();
        this.yaw = LerpedFloat.angular();
        this.pitch = LerpedFloat.angular();
        this.derailAngle = Create.RANDOM.nextInt(60) - 30;
        this.couplingAnchors = Couple.create(null, null);
    }

    public ResourceKey<Level> getDimension() {
        TravellingPoint leading = this.leading();
        TravellingPoint trailing = this.trailing();
        if (leading.edge == null || trailing.edge == null) {
            return null;
        }
        if (leading.edge.isInterDimensional() || trailing.edge.isInterDimensional()) {
            return null;
        }
        ResourceKey<Level> dimension1 = leading.node1.getLocation().dimension;
        ResourceKey<Level> dimension2 = trailing.node1.getLocation().dimension;
        if (dimension1.equals(dimension2)) {
            return dimension1;
        }
        return null;
    }

    public void updateAngles(CarriageContraptionEntity entity, double distanceMoved) {
        double angleDiff = 360.0 * distanceMoved / (Math.PI * 2 * this.type.getWheelRadius());
        float xRot = 0.0f;
        float yRot = 0.0f;
        if (this.leading().edge == null || this.carriage.train.derailed) {
            yRot = -90.0f + entity.yaw - (float)this.derailAngle;
        } else if (!entity.level().dimension().equals(this.getDimension())) {
            yRot = -90.0f + entity.yaw;
            xRot = 0.0f;
        } else {
            Vec3 positionVec = this.leading().getPosition(this.carriage.train.graph);
            Vec3 coupledVec = this.trailing().getPosition(this.carriage.train.graph);
            double diffX = positionVec.x - coupledVec.x;
            double diffY = positionVec.y - coupledVec.y;
            double diffZ = positionVec.z - coupledVec.z;
            yRot = AngleHelper.deg((double)Mth.atan2((double)diffZ, (double)diffX)) + 90.0f;
            xRot = AngleHelper.deg((double)Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)));
        }
        double newWheelAngle = ((double)this.wheelAngle.getValue() - angleDiff) % 360.0;
        for (boolean twice : Iterate.trueAndFalse) {
            if (twice && !entity.firstPositionUpdate) continue;
            this.wheelAngle.setValue(newWheelAngle);
            this.pitch.setValue((double)xRot);
            this.yaw.setValue((double)(-yRot));
        }
    }

    public TravellingPoint leading() {
        TravellingPoint point = (TravellingPoint)this.points.getFirst();
        point.upsideDown = this.isUpsideDown();
        return point;
    }

    public TravellingPoint trailing() {
        TravellingPoint point = (TravellingPoint)this.points.getSecond();
        point.upsideDown = this.isUpsideDown();
        return point;
    }

    public double getStress() {
        if (this.getDimension() == null) {
            return 0.0;
        }
        if (this.carriage.train.derailed) {
            return 0.0;
        }
        return this.type.getWheelPointSpacing() - this.leading().getPosition(this.carriage.train.graph).distanceTo(this.trailing().getPosition(this.carriage.train.graph));
    }

    @Nullable
    public Vec3 getAnchorPosition() {
        return this.getAnchorPosition(false);
    }

    @Nullable
    public Vec3 getAnchorPosition(boolean flipUpsideDown) {
        if (this.leading().edge == null) {
            return null;
        }
        return ((TravellingPoint)this.points.getFirst()).getPosition(this.carriage.train.graph, flipUpsideDown).add(((TravellingPoint)this.points.getSecond()).getPosition(this.carriage.train.graph, flipUpsideDown)).scale(0.5);
    }

    public void updateCouplingAnchor(Vec3 entityPos, float entityXRot, float entityYRot, int bogeySpacing, float partialTicks, boolean leading) {
        boolean selfUpsideDown = this.isUpsideDown();
        boolean leadingUpsideDown = this.carriage.leadingBogey().isUpsideDown();
        Vec3 thisOffset = this.type.getConnectorAnchorOffset(selfUpsideDown);
        thisOffset = thisOffset.multiply(1.0, 1.0, leading ? -1.0 : 1.0);
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)this.pitch.getValue(partialTicks), (Direction.Axis)Direction.Axis.X);
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)this.yaw.getValue(partialTicks), (Direction.Axis)Direction.Axis.Y);
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)(-entityYRot - 90.0f), (Direction.Axis)Direction.Axis.Y);
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)entityXRot, (Direction.Axis)Direction.Axis.X);
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)-180.0, (Direction.Axis)Direction.Axis.Y);
        thisOffset = thisOffset.add(0.0, 0.0, leading ? 0.0 : (double)(-bogeySpacing));
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)180.0, (Direction.Axis)Direction.Axis.Y);
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)(-entityXRot), (Direction.Axis)Direction.Axis.X);
        thisOffset = VecHelper.rotate((Vec3)thisOffset, (double)(entityYRot + 90.0f), (Direction.Axis)Direction.Axis.Y);
        if (selfUpsideDown != leadingUpsideDown) {
            thisOffset = thisOffset.add(0.0, selfUpsideDown ? -2.0 : 2.0, 0.0);
        }
        this.couplingAnchors.set(leading, (Object)entityPos.add(thisOffset));
    }

    public CompoundTag write(DimensionPalette dimensions) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", RegisteredObjectsHelper.getKeyOrThrow(this.type).toString());
        tag.put("Points", (Tag)this.points.serializeEach(tp -> tp.write(dimensions)));
        tag.putBoolean(UPSIDE_DOWN_KEY, this.upsideDown);
        this.bogeyData.putBoolean(UPSIDE_DOWN_KEY, this.upsideDown);
        NBTHelper.writeResourceLocation((CompoundTag)this.bogeyData, (String)"BogeyStyle", (ResourceLocation)this.getStyle().id);
        tag.put("BogeyData", (Tag)this.bogeyData);
        return tag;
    }

    public static CarriageBogey read(CompoundTag tag, TrackGraph graph, DimensionPalette dimensions) {
        ResourceLocation location = ResourceLocation.parse((String)tag.getString("Type"));
        AbstractBogeyBlock type = (AbstractBogeyBlock)BuiltInRegistries.BLOCK.get(location);
        boolean upsideDown = tag.getBoolean(UPSIDE_DOWN_KEY);
        Couple points = Couple.deserializeEach((ListTag)tag.getList("Points", 10), c -> TravellingPoint.read(c, graph, dimensions));
        CompoundTag data = tag.getCompound("BogeyData");
        return new CarriageBogey(type, upsideDown, data, (TravellingPoint)points.getFirst(), (TravellingPoint)points.getSecond());
    }

    public BogeyStyle getStyle() {
        ResourceLocation location = NBTHelper.readResourceLocation((CompoundTag)this.bogeyData, (String)"BogeyStyle");
        BogeyStyle style = AllBogeyStyles.BOGEY_STYLES.get(location);
        return style != null ? style : AllBogeyStyles.STANDARD;
    }

    public BogeySizes.BogeySize getSize() {
        return this.type.getSize();
    }

    private CompoundTag createBogeyData() {
        BogeyStyle style = this.type != null ? this.type.getDefaultStyle() : AllBogeyStyles.STANDARD;
        CompoundTag nbt = style.defaultData != null ? style.defaultData : new CompoundTag();
        NBTHelper.writeResourceLocation((CompoundTag)nbt, (String)"BogeyStyle", (ResourceLocation)style.id);
        nbt.putBoolean(UPSIDE_DOWN_KEY, this.isUpsideDown());
        return nbt;
    }

    void setLeading() {
        this.isLeading = true;
    }

    public boolean isUpsideDown() {
        return this.type.canBeUpsideDown() && this.upsideDown;
    }
}
