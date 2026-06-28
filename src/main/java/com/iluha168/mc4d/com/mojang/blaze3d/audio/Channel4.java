package com.iluha168.mc4d.com.mojang.blaze3d.audio;

import com.mojang.blaze3d.audio.Channel;

/**
 * Implemented by {@link Channel}.
 */
public interface Channel4 {
	/** How far (at least) in W the sound has to be for decorrelation mode. */
	double W_NEAR = 0.5;

	/** @return has the mode changed */
	boolean setStereoMode(boolean stereo);

	void setStereoAngle(float angleRadians);
}
