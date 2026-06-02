/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;

static class FilterItemStack.1 {
    static final /* synthetic */ int[] $SwitchMap$com$simibubi$create$content$logistics$filter$AttributeFilterWhitelistMode;

    static {
        $SwitchMap$com$simibubi$create$content$logistics$filter$AttributeFilterWhitelistMode = new int[AttributeFilterWhitelistMode.values().length];
        try {
            FilterItemStack.1.$SwitchMap$com$simibubi$create$content$logistics$filter$AttributeFilterWhitelistMode[AttributeFilterWhitelistMode.BLACKLIST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FilterItemStack.1.$SwitchMap$com$simibubi$create$content$logistics$filter$AttributeFilterWhitelistMode[AttributeFilterWhitelistMode.WHITELIST_CONJ.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FilterItemStack.1.$SwitchMap$com$simibubi$create$content$logistics$filter$AttributeFilterWhitelistMode[AttributeFilterWhitelistMode.WHITELIST_DISJ.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
