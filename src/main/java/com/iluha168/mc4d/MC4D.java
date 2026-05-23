package com.iluha168.mc4d;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Relative;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MC4D.MODID)
public class MC4D {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mc4d";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Relative RelativeW = Relative.valueOf("W");

    public MC4D() {
    }
}
