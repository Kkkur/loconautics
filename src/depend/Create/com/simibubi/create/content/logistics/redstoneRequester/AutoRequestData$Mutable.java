/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import net.minecraft.core.BlockPos;

public static class AutoRequestData.Mutable {
    public PackageOrderWithCrafts encodedRequest = PackageOrderWithCrafts.empty();
    public String encodedTargetAddress = "";
    public BlockPos targetOffset = BlockPos.ZERO;
    public String targetDim = "null";
    public boolean isValid = false;

    public AutoRequestData.Mutable() {
    }

    public AutoRequestData.Mutable(AutoRequestData data) {
        this.encodedRequest = data.encodedRequest;
        this.encodedTargetAddress = data.encodedTargetAddress;
        this.targetOffset = data.targetOffset;
        this.targetDim = data.targetDim;
        this.isValid = data.isValid;
    }

    public AutoRequestData toImmutable() {
        return new AutoRequestData(this.encodedRequest, this.encodedTargetAddress, this.targetOffset, this.targetDim, this.isValid);
    }
}
