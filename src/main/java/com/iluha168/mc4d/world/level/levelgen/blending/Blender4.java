package com.iluha168.mc4d.world.level.levelgen.blending;

import net.minecraft.world.level.levelgen.blending.Blender;

/**
 * Implemented by {@link Blender}.
 */
public interface Blender4 {
	Blender.BlendingOutput blendOffsetAndFactor(int blockX, int blockZ, int blockW);
}
