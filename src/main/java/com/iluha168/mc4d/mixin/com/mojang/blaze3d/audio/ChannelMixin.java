package com.iluha168.mc4d.mixin.com.mojang.blaze3d.audio;

import com.iluha168.mc4d.com.mojang.blaze3d.audio.Channel4;
import com.iluha168.mc4d.com.mojang.blaze3d.audio.SoundBuffer4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTStereoAngles;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

@Mixin(Channel.class)
abstract class ChannelMixin implements Channel4 {
	@Shadow @Final private static Logger LOGGER;
	@Shadow @Final private static int QUEUED_BUFFER_COUNT;
	@Shadow @Final private int source;
	@Shadow private int streamingBufferSize;
	@Shadow private @Nullable AudioStream stream;
	@Shadow public abstract void play();

	@Shadow
	public abstract boolean playing();

	@Shadow
	public abstract void attachStaticBuffer(SoundBuffer buffer);

	@Shadow
	protected abstract void pumpBuffers(int size);

	@Unique private boolean isInStereoMode;
	/** The static buffer last attached, kept so the channel can re-attach its stereo/mono variant. */
	@Unique private @Nullable SoundBuffer lastStaticBuffer;
	/** Carry-over of the previous streamed chunk's tail, for delay continuity across stereo buffers. */
	@Unique private short @Nullable [] delayTail;

	@Override
	public boolean setStereoMode(boolean stereo) {
		if (this.isInStereoMode == stereo) {
			return false; // Has not changed
		}
		this.isInStereoMode = stereo;
		if (this.stream != null) {
			this.reformatStream();
		} else if (this.lastStaticBuffer != null) {
			this.reattachStaticBuffer();
		}
		return true; // Has changed
	}

	@Override
	public void setStereoAngle(float angleRadians) {
		AL10.alSourcefv(this.source, EXTStereoAngles.AL_STEREO_ANGLES, new float[]{ +angleRadians, -angleRadians });
	}

	@Unique
	private void reattachStaticBuffer() {
		final int oldSampleOffset = AL10.alGetSourcei(this.source, AL11.AL_SAMPLE_OFFSET);
		final boolean oldPlaying = this.playing();

		// This method runs in a context where a mode change has definitely happened, so we do not check for that and stop the old play immediately:
		AL10.alSourceStop(this.source);
		AL10.alSourcei(this.source, AL10.AL_BUFFER, 0);

		if (this.isInStereoMode) {
			this.attachStaticBufferStereo(this.lastStaticBuffer);
		} else {
			this.attachStaticBuffer(this.lastStaticBuffer);
		}

		// Restore old play settings
		AL10.alSourcei(this.source, AL11.AL_SAMPLE_OFFSET, oldSampleOffset);
		if (oldPlaying) this.play();
	}

	@Unique
	private void reformatStream() {
		// This method runs in a context where a mode change has definitely happened, so we do not check for that and stop the old play immediately:
		AL10.alSourceStop(this.source);
		int queued = AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_QUEUED);
		if (queued > 0) {
			int[] ids = new int[queued];
			AL10.alSourceUnqueueBuffers(this.source, ids);
			AL10.alDeleteBuffers(ids);
		}

		this.delayTail = null;
		if (this.isInStereoMode) {
			this.pumpBuffersStereo(QUEUED_BUFFER_COUNT);
		} else {
			this.pumpBuffers(QUEUED_BUFFER_COUNT);
		}
		this.play();
	}


	@Inject(method = "setSelfPosition", at = @At("HEAD"))
	void setSelfPosition(Vec3 newPosition, CallbackInfo ci) {
		if (!(newPosition instanceof Vec4)) throw Err4.container3();
		// This is just to ensure everything is patched, AL does not actually support 4th value :(
	}

	@WrapMethod(method = "attachStaticBuffer")
	void attachStaticBuffer(SoundBuffer buffer, Operation<Void> original) {
		this.lastStaticBuffer = buffer;
		if (this.isInStereoMode) {
			this.attachStaticBufferStereo(buffer);
		} else {
			original.call(buffer);
		}
	}
	@Unique
	private void attachStaticBufferStereo(SoundBuffer buffer) {
		((SoundBuffer4) buffer).getAlBufferStereo().ifPresent(id -> AL10.alSourcei(this.source, AL10.AL_BUFFER, id));
	}

	@WrapMethod(method = "pumpBuffers")
	private void pumpBuffers(int size, Operation<Void> original) {
		if (this.isInStereoMode) {
			this.pumpBuffersStereo(size);
		} else {
			original.call(size);
		}
	}
	@Unique
	private void pumpBuffersStereo(int size) {
		if (this.stream == null) {
			return;
		}
		AudioFormat monoFormat = this.stream.getFormat();
		int delay = SoundBuffer4.decorrelationSamples(monoFormat.getSampleRate());
		if (this.delayTail == null || this.delayTail.length != delay) {
			this.delayTail = new short[delay];
		}
		try {
			for (int i = 0; i < size; i++) {
				ByteBuffer buffer = this.stream.read(this.streamingBufferSize);
				//noinspection ConstantValue IDK vanilla does this so I will too
				if (buffer == null) {
					continue;
				}
				ByteBuffer stereoBuffer = SoundBuffer4.monoToStereo16(buffer, monoFormat, delay, this.delayTail);
				(stereoBuffer == null
					? new SoundBuffer(buffer, monoFormat)
					: new SoundBuffer(stereoBuffer, SoundBuffer4.stereoFormatOf(monoFormat)
				))
					.releaseAlBuffer()
					.ifPresent(id -> AL10.alSourceQueueBuffers(this.source, new int[]{id}));
			}
		} catch (IOException e) {
			LOGGER.error("Failed to read from audio stream", e);
		}
	}
}
