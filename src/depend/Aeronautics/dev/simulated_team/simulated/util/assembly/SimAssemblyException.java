/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  net.minecraft.network.chat.Component
 */
package dev.simulated_team.simulated.util.assembly;

import com.simibubi.create.content.contraptions.AssemblyException;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.service.SimConfigService;
import net.minecraft.network.chat.Component;

public class SimAssemblyException {
    public static AssemblyException structureTooLarge() {
        return new AssemblyException("structureTooLarge", new Object[]{SimConfigService.INSTANCE.server().assembly.maxBlocksMoved.get()});
    }

    public static AssemblyException couldNotAlign() {
        return new AssemblyException((Component)SimLang.translate("gui.assembly.exception.couldNotAlign", new Object[0]).component());
    }

    public static AssemblyException outOfWorld() {
        return new AssemblyException((Component)SimLang.translate("gui.assembly.exception.outOfWorld", new Object[0]).component());
    }

    public static AssemblyException tooFarFromGround() {
        return new AssemblyException((Component)SimLang.translate("gui.assembly.exception.tooFarFromGround", new Object[0]).component());
    }

    public static AssemblyException tooFast() {
        return new AssemblyException((Component)SimLang.translate("gui.assembly.exception.tooFast", new Object[0]).component());
    }
}
