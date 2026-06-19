package com.iluha168.mc4d.mixin.net.minecraft.network;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FriendlyByteBuf.class)
abstract
class   FriendlyByteBufMixin {
	@Shadow
	public abstract float readFloat();

	@Shadow
	public abstract FriendlyByteBuf writeFloat(float value);

	// TODO everything

	@Redirect(method = "readBlockHitResult", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 readBlockHitResult(double x, double y, double z, @Local(name = "pos") BlockPos pos) {
		final float clickW = this.readFloat();
		return new Vec4(x, y, z, Vec4i.getW(pos) + clickW);
	}

	@Inject(method = "writeBlockHitResult", at = @At("TAIL"))
	void writeBlockHitResult(BlockHitResult blockHit, CallbackInfo ci, @Local(name = "blockPos") BlockPos blockPos, @Local(name = "location") Vec3 location) {
		this.writeFloat((float)(((Vec4) location).w - Vec4i.getW(blockPos)));
	}

	// TODO everything
}
