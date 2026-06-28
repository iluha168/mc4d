package com.iluha168.mc4d.com.mojang.blaze3d.audio;

import com.mojang.blaze3d.audio.SoundBuffer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.OptionalInt;

/**
 * Implemented by {@link SoundBuffer}.
 */
public interface SoundBuffer4 {
	/** Ear decorrelation delay seconds for W sounds. */
	float DECORRELATION_MS = (float) (7.0 / 1000.0);

	OptionalInt getAlBufferStereo();

	/** Converts milliseconds to frames for the given per-second sample rate. */
	static int decorrelationSamples(float sampleRate) {
		return Math.round(DECORRELATION_MS * sampleRate);
	}

	/**
	 * Duplicates 16-bit mono into stereo, and also delays the right channel by {@code samplesDelayAmount} frames.
	 * @param tail where to put the rest {@code samplesDelayAmount} frames of right channel, that got shifted past duration.
	 *             It is also the same array that first {@code samplesDelayAmount} frames of the <i>resulting</i> right channel are read from.
	 *             It is assumed this method is called with the same `tail` reference over and over, so that ending of right channel from previous call becomes the beginning in the next call.
	 * @return {@code null} when not a 16-bit mono is input.
	 */
	static @Nullable ByteBuffer monoToStereo16(ByteBuffer mono, AudioFormat format, int samplesDelayAmount, short @Nullable [] tail) {
		if (format.getChannels() != 1 || format.getSampleSizeInBits() != Short.SIZE) {
			return null;
		}
		final ByteOrder order = format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		final ShortBuffer in = mono.duplicate().order(order).asShortBuffer();
		final int frames = in.remaining();
		final ByteBuffer outBytes = BufferUtils.createByteBuffer(frames * 2 * Short.BYTES);
		final ShortBuffer out = outBytes.order(order).asShortBuffer();
		for (int i = 0; i < frames; i++) {
			final short left = in.get(i);
			final short right = i >= samplesDelayAmount ? in.get(i - samplesDelayAmount)
				: tail != null && i < tail.length ? tail[i]
				: 0;
			out.put(left).put(right);
		}
		if (tail != null && samplesDelayAmount > 0 && frames >= samplesDelayAmount) {
			for (int k = 0; k < samplesDelayAmount; k++) {
				tail[k] = in.get(frames - samplesDelayAmount + k);
			}
		}
		return outBytes;
	}

	/** Format returned by {@link SoundBuffer4#monoToStereo16} for a given mono input format. */
	static AudioFormat stereoFormatOf(AudioFormat format) {
		return new AudioFormat(
			format.getEncoding(),
			format.getSampleRate(),
			format.getSampleSizeInBits(),
			2,
			format.getSampleSizeInBits() / 8 * 2,
			format.getFrameRate(),
			format.isBigEndian()
		);
	}
}
