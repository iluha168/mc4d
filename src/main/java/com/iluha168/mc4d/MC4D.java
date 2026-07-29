package com.iluha168.mc4d;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MC4D.MODID)
public class MC4D {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mc4d";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MC4D() {
    }

    /**
     * wRot of the 3D slice the client renders and aims within.
     * Movement keybinds and block/entity selection are taken relative to this orientation, and the server uses the same value when predicting them.
     */
    public static float getCameraSliceWRot() {
        return 0.0F;
    }
    /** vRot counterpart of {@link #getCameraSliceWRot}. */
    public static float getCameraSliceVRot() {
        return 0.0F;
    }
}
