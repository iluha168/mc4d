package com.iluha168.mc4d.mixin.net.minecraft.client.gui.components;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.resources.sounds.SoundInstance4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SubtitleOverlay.class)
class SubtitleOverlayMixin {
	@ModifyExpressionValue(method = "extractRenderState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
	))
	private Vec3 extractRenderState_delta(Vec3 delta, @Share("deltaW") LocalDoubleRef deltaW) {
		// The vanilla < > arrows should work in the camera 3D slice coordinate space.
		final Vec4 delta4 = (Vec4) delta;
		deltaW.set(delta4.w);
		return new Vec4(delta4.x, delta4.y, delta4.z, 0.0);
	}
	@ModifyExpressionValue(method = "extractRenderState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/ARGB;color(IIII)I"
	))
	private int extractRenderState_textColor(int textColor, @Share("deltaW") LocalDoubleRef deltaW) {
		return ARGB.alphaBlend(textColor, MC4DClient.getTintColor(deltaW.get()));
	}

	@Redirect(method = "onPlaySound", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private Vec3 onPlaySound(double x, double y, double z, @Local(argsOnly = true, name = "sound") SoundInstance sound) {
		return new Vec4(x, y, z, ((SoundInstance4) sound).getW());
	}
}
