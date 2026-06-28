package com.iluha168.mc4d.mixin.com.mojang.blaze3d.audio;

import com.iluha168.mc4d.com.mojang.blaze3d.audio.Library4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.audio.Library;
import org.lwjgl.openal.ALCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Library.class)
class LibraryMixin implements Library4 {
	@Unique volatile boolean supportsStereoAngles;

	@ModifyExpressionValue(method = "init", at = @At(
		value = "INVOKE",
		target = "Lorg/lwjgl/openal/AL;createCapabilities(Lorg/lwjgl/openal/ALCCapabilities;)Lorg/lwjgl/openal/ALCapabilities;"
	))
	ALCapabilities init(ALCapabilities capabilities) {
		this.supportsStereoAngles = capabilities.AL_EXT_STEREO_ANGLES;
		return capabilities;
	}

	@Override
	public boolean supportsStereoAngles() {
		return this.supportsStereoAngles;
	}
}
