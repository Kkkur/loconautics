/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.Minecart
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.PoweredRailBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.attachment.IAttachmentHolder
 *  net.neoforged.neoforge.attachment.IAttachmentSerializer
 *  net.neoforged.neoforge.common.util.INBTSerializable
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartControllerUpdatePacket;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinecartController
implements INBTSerializable<CompoundTag> {
    public static final MinecartController EMPTY = new Empty();
    public static final IAttachmentSerializer<CompoundTag, MinecartController> SERIALIZER = Type.SERIALIZER;
    private boolean needsEntryRefresh;
    private WeakReference<AbstractMinecart> weakRef;
    private Couple<Optional<StallData>> stallData;
    private Couple<Optional<CouplingData>> couplings;

    public MinecartController(AbstractMinecart minecart) {
        this.weakRef = new WeakReference<AbstractMinecart>(minecart);
        this.stallData = Couple.create(Optional::empty);
        this.couplings = Couple.create(Optional::empty);
        this.needsEntryRefresh = true;
    }

    public final boolean isEmpty() {
        return this.getType() == Type.EMPTY;
    }

    @NotNull
    protected Type getType() {
        return Type.NORMAL;
    }

    public void tick() {
        AbstractMinecart cart = this.cart();
        Level world = this.getWorld();
        if (cart == null || world == null) {
            return;
        }
        if (this.needsEntryRefresh) {
            ((List)CapabilityMinecartController.queuedAdditions.get((LevelAccessor)world)).add(cart);
            this.needsEntryRefresh = false;
        }
        this.stallData.forEach(opt -> opt.ifPresent(sd -> sd.tick(cart)));
        MutableBoolean internalStall = new MutableBoolean(false);
        this.couplings.forEachWithContext((opt, main) -> opt.ifPresent(cd -> {
            UUID idOfOther = cd.idOfCart(main == false);
            MinecartController otherCart = CapabilityMinecartController.getIfPresent(world, idOfOther);
            internalStall.setValue(internalStall.booleanValue() || otherCart == null || !otherCart.isPresent() || otherCart.isStalled(false));
        }));
        if (!world.isClientSide) {
            this.setStalled(internalStall.booleanValue(), true);
            this.disassemble(cart);
        }
    }

    private void disassemble(AbstractMinecart cart) {
        int k;
        int j;
        int i;
        if (cart instanceof Minecart) {
            return;
        }
        List passengers = cart.getPassengers();
        if (passengers.isEmpty() || !(passengers.getFirst() instanceof AbstractContraptionEntity)) {
            return;
        }
        Level world = cart.level();
        if (world.getBlockState(new BlockPos(i = Mth.floor((double)cart.getX()), (j = Mth.floor((double)cart.getY())) - 1, k = Mth.floor((double)cart.getZ()))).is(BlockTags.RAILS)) {
            --j;
        }
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState blockstate = world.getBlockState(blockpos);
        if (cart.canUseRail() && blockstate.is(BlockTags.RAILS) && blockstate.getBlock() instanceof PoweredRailBlock && ((PoweredRailBlock)blockstate.getBlock()).isActivatorRail()) {
            if (cart.isVehicle()) {
                cart.ejectPassengers();
            }
            if (cart.getHurtTime() == 0) {
                cart.setHurtDir(-cart.getHurtDir());
                cart.setHurtTime(10);
                cart.setDamage(50.0f);
                cart.hurtMarked = true;
            }
        }
    }

    public boolean isFullyCoupled() {
        return this.isLeadingCoupling() && this.isConnectedToCoupling();
    }

    public boolean isLeadingCoupling() {
        return ((Optional)this.couplings.get(true)).isPresent();
    }

    public boolean isConnectedToCoupling() {
        return ((Optional)this.couplings.get(false)).isPresent();
    }

    public boolean isCoupledThroughContraption() {
        for (boolean current : Iterate.trueAndFalse) {
            if (!this.hasContraptionCoupling(current)) continue;
            return true;
        }
        return false;
    }

    public boolean hasContraptionCoupling(boolean current) {
        Optional optional = (Optional)this.couplings.get(current);
        return optional.isPresent() && ((CouplingData)optional.get()).contraption;
    }

    public float getCouplingLength(boolean leading) {
        Optional optional = (Optional)this.couplings.get(leading);
        return optional.map(couplingData -> Float.valueOf(couplingData.length)).orElse(Float.valueOf(0.0f)).floatValue();
    }

    public void decouple() {
        this.couplings.forEachWithContext((opt, main) -> opt.ifPresent(cd -> {
            UUID idOfOther = cd.idOfCart(main == false);
            MinecartController otherCart = CapabilityMinecartController.getIfPresent(this.getWorld(), idOfOther);
            if (otherCart == null) {
                return;
            }
            this.removeConnection((boolean)main);
            otherCart.removeConnection(main == false);
        }));
    }

    public void removeConnection(boolean main) {
        Entity entity;
        List passengers;
        if (this.hasContraptionCoupling(main) && this.getWorld() != null && !this.getWorld().isClientSide && !(passengers = this.cart().getPassengers()).isEmpty() && (entity = (Entity)passengers.getFirst()) instanceof AbstractContraptionEntity) {
            ((AbstractContraptionEntity)entity).disassemble();
        }
        this.couplings.set(main, Optional.empty());
        this.needsEntryRefresh |= main;
        this.sendData();
    }

    public void prepareForCoupling(boolean isLeading) {
        if (isLeading && this.isLeadingCoupling() || !isLeading && this.isConnectedToCoupling()) {
            ArrayList<MinecartController> cartsToFlip = new ArrayList<MinecartController>();
            MinecartController current = this;
            boolean forward = current.isLeadingCoupling();
            int safetyCount = 1000;
            do {
                if (safetyCount-- <= 0) {
                    Create.LOGGER.warn("Infinite loop in coupling iteration");
                    return;
                }
                cartsToFlip.add(current);
            } while ((current = CouplingHandler.getNextInCouplingChain(this.getWorld(), current, forward)) != null && current != EMPTY);
            for (MinecartController minecartController : cartsToFlip) {
                minecartController.couplings.forEachWithContext((opt, leading) -> opt.ifPresent(cd -> {
                    cd.flip();
                    if (!cd.contraption) {
                        return;
                    }
                    List passengers = minecartController.cart().getPassengers();
                    if (passengers.isEmpty()) {
                        return;
                    }
                    Entity entity = (Entity)passengers.getFirst();
                    if (!(entity instanceof OrientedContraptionEntity)) {
                        return;
                    }
                    OrientedContraptionEntity contraption = (OrientedContraptionEntity)entity;
                    UUID couplingId = contraption.getCouplingId();
                    if (couplingId == cd.mainCartID) {
                        contraption.setCouplingId(cd.connectedCartID);
                        return;
                    }
                    if (couplingId == cd.connectedCartID) {
                        contraption.setCouplingId(cd.mainCartID);
                        return;
                    }
                }));
                minecartController.couplings = minecartController.couplings.swap();
                minecartController.needsEntryRefresh = true;
                if (minecartController == this) continue;
                minecartController.sendData();
            }
        }
    }

    public void coupleWith(boolean isLeading, UUID coupled, float length, boolean contraption) {
        UUID mainID = isLeading ? this.cart().getUUID() : coupled;
        UUID connectedID = isLeading ? coupled : this.cart().getUUID();
        this.couplings.set(isLeading, Optional.of(new CouplingData(mainID, connectedID, length, contraption)));
        this.needsEntryRefresh |= isLeading;
        this.sendData();
    }

    @Nullable
    public UUID getCoupledCart(boolean asMain) {
        Optional optional = (Optional)this.couplings.get(asMain);
        if (optional.isEmpty()) {
            return null;
        }
        CouplingData couplingData = (CouplingData)optional.get();
        return asMain ? couplingData.connectedCartID : couplingData.mainCartID;
    }

    public boolean isStalled() {
        return this.isStalled(true) || this.isStalled(false);
    }

    private boolean isStalled(boolean internal) {
        return ((Optional)this.stallData.get(internal)).isPresent();
    }

    public void setStalledExternally(boolean stall) {
        this.setStalled(stall, false);
    }

    private void setStalled(boolean stall, boolean internal) {
        if (this.isStalled(internal) == stall) {
            return;
        }
        @Nullable AbstractMinecart cart = this.cart();
        if (cart == null) {
            return;
        }
        if (stall && cart != null) {
            this.stallData.set(internal, Optional.of(new StallData(cart)));
            this.sendData();
            return;
        }
        if (!this.isStalled(!internal) && cart != null) {
            ((Optional)this.stallData.get(internal)).ifPresent(data -> data.release(cart));
        }
        this.stallData.set(internal, Optional.empty());
        this.sendData();
    }

    public void sendData() {
        this.sendData(null);
    }

    public void sendData(@Nullable AbstractMinecart cart) {
        if (cart != null) {
            this.weakRef = new WeakReference<AbstractMinecart>(cart);
            this.needsEntryRefresh = true;
        }
        if (this.getWorld() == null || this.getWorld().isClientSide) {
            return;
        }
        CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)this.cart(), (CustomPacketPayload)new MinecartControllerUpdatePacket(this, (HolderLookup.Provider)this.getWorld().registryAccess()));
    }

    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        CompoundTag compoundNBT = new CompoundTag();
        this.stallData.forEachWithContext((opt, internal) -> opt.ifPresent(sd -> compoundNBT.put(internal != false ? "InternalStallData" : "StallData", (Tag)sd.serialize())));
        this.couplings.forEachWithContext((opt, main) -> opt.ifPresent(cd -> compoundNBT.put(main != false ? "MainCoupling" : "ConnectedCoupling", (Tag)cd.serialize())));
        return compoundNBT;
    }

    public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag nbt) {
        Optional<Object> internalSD = Optional.empty();
        Optional<Object> externalSD = Optional.empty();
        Optional<Object> mainCD = Optional.empty();
        Optional<Object> connectedCD = Optional.empty();
        if (nbt.contains("InternalStallData")) {
            internalSD = Optional.of(StallData.read(nbt.getCompound("InternalStallData")));
        }
        if (nbt.contains("StallData")) {
            externalSD = Optional.of(StallData.read(nbt.getCompound("StallData")));
        }
        if (nbt.contains("MainCoupling")) {
            mainCD = Optional.of(CouplingData.read(nbt.getCompound("MainCoupling")));
        }
        if (nbt.contains("ConnectedCoupling")) {
            connectedCD = Optional.of(CouplingData.read(nbt.getCompound("ConnectedCoupling")));
        }
        this.stallData = Couple.create(internalSD, externalSD);
        this.couplings = Couple.create(mainCD, connectedCD);
        this.needsEntryRefresh = true;
    }

    public boolean isPresent() {
        return this.weakRef.get() != null && this.cart().isAlive();
    }

    public AbstractMinecart cart() {
        return (AbstractMinecart)this.weakRef.get();
    }

    @Nullable
    private Level getWorld() {
        if (this.cart() == null) {
            return null;
        }
        return this.cart().level();
    }

    protected static enum Type implements StringRepresentable
    {
        EMPTY(new IAttachmentSerializer<CompoundTag, MinecartController>(){

            @NotNull
            public MinecartController read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
                return MinecartController.EMPTY;
            }

            public CompoundTag write(@NotNull MinecartController attachment, @NotNull HolderLookup.Provider provider) {
                return attachment.serializeNBT(provider);
            }
        }),
        NORMAL(new IAttachmentSerializer<CompoundTag, MinecartController>(){

            @NotNull
            public MinecartController read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
                MinecartController controller = new MinecartController(null);
                controller.deserializeNBT(provider, tag);
                return controller;
            }

            @Nullable
            public CompoundTag write(@NotNull MinecartController attachment, @NotNull HolderLookup.Provider provider) {
                return attachment.serializeNBT(provider);
            }
        });

        public static final Codec<Type> CODEC;
        private final IAttachmentSerializer<CompoundTag, MinecartController> serializer;
        private static final IAttachmentSerializer<CompoundTag, MinecartController> SERIALIZER;

        private Type(IAttachmentSerializer<CompoundTag, MinecartController> serializer) {
            this.serializer = serializer;
        }

        public IAttachmentSerializer<CompoundTag, MinecartController> getSerializer() {
            return this.serializer;
        }

        @NotNull
        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }

        static {
            CODEC = StringRepresentable.fromValues(Type::values);
            SERIALIZER = new IAttachmentSerializer<CompoundTag, MinecartController>(){

                @NotNull
                public MinecartController read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
                    return (MinecartController)Type.valueOf(tag.getString("Type")).getSerializer().read(holder, (Tag)tag, provider);
                }

                @Nullable
                public CompoundTag write(MinecartController attachment, @NotNull HolderLookup.Provider provider) {
                    CompoundTag tag = attachment.serializeNBT(provider);
                    if (tag != null) {
                        tag.putString("Type", attachment.getType().name());
                    }
                    return tag;
                }
            };
        }
    }

    private static class CouplingData {
        private UUID mainCartID;
        private UUID connectedCartID;
        private float length;
        private boolean contraption;

        public CouplingData(UUID mainCartID, UUID connectedCartID, float length, boolean contraption) {
            this.mainCartID = mainCartID;
            this.connectedCartID = connectedCartID;
            this.length = length;
            this.contraption = contraption;
        }

        void flip() {
            UUID swap = this.mainCartID;
            this.mainCartID = this.connectedCartID;
            this.connectedCartID = swap;
        }

        CompoundTag serialize() {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Main", (Tag)NbtUtils.createUUID((UUID)this.mainCartID));
            nbt.put("Connected", (Tag)NbtUtils.createUUID((UUID)this.connectedCartID));
            nbt.putFloat("Length", this.length);
            nbt.putBoolean("Contraption", this.contraption);
            return nbt;
        }

        static CouplingData read(CompoundTag nbt) {
            UUID mainCartID = NbtUtils.loadUUID((Tag)NBTHelper.getINBT((CompoundTag)nbt, (String)"Main"));
            UUID connectedCartID = NbtUtils.loadUUID((Tag)NBTHelper.getINBT((CompoundTag)nbt, (String)"Connected"));
            float length = nbt.getFloat("Length");
            boolean contraption = nbt.getBoolean("Contraption");
            return new CouplingData(mainCartID, connectedCartID, length, contraption);
        }

        public UUID idOfCart(boolean main) {
            return main ? this.mainCartID : this.connectedCartID;
        }
    }

    private static class StallData {
        Vec3 position;
        Vec3 motion;
        float yaw;
        float pitch;

        private StallData() {
        }

        StallData(AbstractMinecart entity) {
            this.position = entity.position();
            this.motion = entity.getDeltaMovement();
            this.yaw = entity.getYRot();
            this.pitch = entity.getXRot();
            this.tick(entity);
        }

        void tick(AbstractMinecart entity) {
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setYRot(this.yaw);
            entity.setXRot(this.pitch);
        }

        void release(AbstractMinecart entity) {
            entity.setDeltaMovement(this.motion);
        }

        CompoundTag serialize() {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Pos", (Tag)VecHelper.writeNBT((Vec3)this.position));
            nbt.put("Motion", (Tag)VecHelper.writeNBT((Vec3)this.motion));
            nbt.putFloat("Yaw", this.yaw);
            nbt.putFloat("Pitch", this.pitch);
            return nbt;
        }

        static StallData read(CompoundTag nbt) {
            StallData stallData = new StallData();
            stallData.position = VecHelper.readNBT((ListTag)nbt.getList("Pos", 6));
            stallData.motion = VecHelper.readNBT((ListTag)nbt.getList("Motion", 6));
            stallData.yaw = nbt.getFloat("Yaw");
            stallData.pitch = nbt.getFloat("Pitch");
            return stallData;
        }
    }

    private static class Empty
    extends MinecartController {
        private Empty() {
            super(null);
        }

        public Empty(AbstractMinecart minecart) {
            super(minecart);
        }

        @Override
        @NotNull
        protected Type getType() {
            return Type.EMPTY;
        }

        private static void warn() {
            Create.LOGGER.warn("Method called on EMPTY MinecartController", (Throwable)new Exception());
        }

        @Override
        public void tick() {
            Empty.warn();
        }

        @Override
        public boolean isFullyCoupled() {
            Empty.warn();
            return false;
        }

        @Override
        public boolean isLeadingCoupling() {
            Empty.warn();
            return false;
        }

        @Override
        public boolean isConnectedToCoupling() {
            Empty.warn();
            return false;
        }

        @Override
        public boolean isCoupledThroughContraption() {
            Empty.warn();
            return false;
        }

        @Override
        public boolean hasContraptionCoupling(boolean current) {
            Empty.warn();
            return false;
        }

        @Override
        public float getCouplingLength(boolean leading) {
            Empty.warn();
            return 0.0f;
        }

        @Override
        public void decouple() {
            Empty.warn();
        }

        @Override
        public void removeConnection(boolean main) {
            Empty.warn();
        }

        @Override
        public void prepareForCoupling(boolean isLeading) {
            Empty.warn();
        }

        @Override
        public void coupleWith(boolean isLeading, UUID coupled, float length, boolean contraption) {
            Empty.warn();
        }

        @Override
        @Nullable
        public UUID getCoupledCart(boolean asMain) {
            Empty.warn();
            return null;
        }

        @Override
        public boolean isStalled() {
            Empty.warn();
            return false;
        }

        @Override
        public void setStalledExternally(boolean stall) {
            Empty.warn();
        }

        @Override
        public void sendData() {
            super.sendData();
        }

        @Override
        public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
            return super.serializeNBT(provider);
        }

        @Override
        public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag nbt) {
            super.deserializeNBT(provider, nbt);
        }

        @Override
        public boolean isPresent() {
            return super.isPresent();
        }

        @Override
        public AbstractMinecart cart() {
            return super.cart();
        }
    }
}
