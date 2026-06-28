package com.iluha168.mc4d.mixin.com.mojang.blaze3d.audio;

import com.iluha168.mc4d.com.mojang.blaze3d.audio.SoundBuffer4;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.audio.OpenAlUtil;
import com.mojang.blaze3d.audio.SoundBuffer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.openal.AL10;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.OptionalInt;

@Mixin(SoundBuffer.class)
class SoundBufferMixin implements SoundBuffer4 {
	@Shadow private @Nullable ByteBuffer data;
	@Shadow @Final private AudioFormat format;

	@Shadow
	private boolean hasAlBuffer;
	@Unique private boolean hasAlBufferStereo;
	@Unique private int alBufferStereo;

	@WrapOperation(method = "getAlBuffer", at = @At(
		value = "FIELD",
		target = "Lcom/mojang/blaze3d/audio/SoundBuffer;data:Ljava/nio/ByteBuffer;",
		opcode = Opcodes.PUTFIELD
	))
	void getAlBuffer_keepData(SoundBuffer instance, ByteBuffer value, Operation<Void> original) {
		if (this.hasAlBufferStereo) {
			// Write `data = null` only when both buffers are built
			original.call(instance, value);
		}
	}
	@Override
	public OptionalInt getAlBufferStereo() {
		if (this.hasAlBufferStereo) {
			return OptionalInt.of(this.alBufferStereo);
		}
		if (this.data == null) {
			return OptionalInt.empty();
		}
		ByteBuffer stereoData = SoundBuffer4.monoToStereo16(
			this.data, this.format, SoundBuffer4.decorrelationSamples(this.format.getSampleRate()), null
		);
		if (stereoData == null) {
			return OptionalInt.empty();
		}

		int[] intBuffer = new int[1];
		AL10.alGenBuffers(intBuffer);
		if (OpenAlUtil.checkALError("Creating buffer")) {
			return OptionalInt.empty();
		}

		AL10.alBufferData(intBuffer[0], AL10.AL_FORMAT_STEREO16, stereoData, (int) this.format.getSampleRate());
		if (OpenAlUtil.checkALError("Assigning buffer data")) {
			return OptionalInt.empty();
		}
		this.alBufferStereo = intBuffer[0];
		this.hasAlBufferStereo = true;
		if (this.hasAlBuffer) {
			// Write `data = null` only when both buffers are built
			this.data = null;
		}

		return OptionalInt.of(this.alBufferStereo);
	}

	@WrapMethod(method = "discardAlBuffer")
	void discardAlBuffer(Operation<Void> original) {
		original.call();

		if (this.hasAlBufferStereo) {
			AL10.alDeleteBuffers(new int[]{this.alBufferStereo});
			if (OpenAlUtil.checkALError("Deleting stream buffers")) {
				return;
			}
		}

		this.hasAlBufferStereo = false;
	}

	@ModifyReturnValue(method = "isValid", at = @At("RETURN"))
	boolean isValid(boolean original) {
		return original || this.hasAlBufferStereo;
	}
}
