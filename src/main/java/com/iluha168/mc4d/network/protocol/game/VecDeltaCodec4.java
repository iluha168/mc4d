package com.iluha168.mc4d.network.protocol.game;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.network.protocol.game.VecDeltaCodec;

/**
 * Implemented by {@link VecDeltaCodec}.
 */
public interface VecDeltaCodec4 {
	Vec4 decode(long xa, long ya, long za, long wa);

	long encodeW(Vec4 pos);
}
