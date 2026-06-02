/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.offroad.content.blocks.borehead_bearing;

import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;

public interface BoreheadAttachedStorage {
    public void attachBlockEntity(BoreheadBearingBlockEntity var1);

    public void setInsertAllowed(boolean var1);

    public void invokeUnstall();
}
