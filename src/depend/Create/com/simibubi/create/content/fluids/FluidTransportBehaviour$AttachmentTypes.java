/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.fluids;

public static enum FluidTransportBehaviour.AttachmentTypes {
    NONE(new ComponentPartials[0]),
    CONNECTION(ComponentPartials.CONNECTION),
    DETAILED_CONNECTION(ComponentPartials.RIM_CONNECTOR),
    RIM(ComponentPartials.RIM_CONNECTOR, ComponentPartials.RIM),
    PARTIAL_RIM(ComponentPartials.RIM),
    DRAIN(ComponentPartials.RIM_CONNECTOR, ComponentPartials.DRAIN),
    PARTIAL_DRAIN(ComponentPartials.DRAIN);

    public final ComponentPartials[] partials;

    private FluidTransportBehaviour.AttachmentTypes(ComponentPartials ... partials) {
        this.partials = partials;
    }

    public FluidTransportBehaviour.AttachmentTypes withoutConnector() {
        if (this == RIM) {
            return PARTIAL_RIM;
        }
        if (this == DRAIN) {
            return PARTIAL_DRAIN;
        }
        return this;
    }

    public static enum ComponentPartials {
        CONNECTION,
        RIM_CONNECTOR,
        RIM,
        DRAIN;

    }
}
