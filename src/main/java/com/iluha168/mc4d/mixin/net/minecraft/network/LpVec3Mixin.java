package com.iluha168.mc4d.mixin.net.minecraft.network;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.LpVec3;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LpVec3.class)
class LpVec3Mixin {
	@Shadow
	private static double sanitize(double value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Redirect(method = "read", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	private static Vec3 read_ZERO() {
		return Vec4.ZERO;
	}
	@Redirect(method = "read", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 read_vec(double x, double y, double z, @Local(argsOnly = true, name = "input") ByteBuf input) {
		return new Vec4(x, y, z, input.readDouble()); // Who needs compression when I got laziness
	}

	@Definition(id = "chessboardLength", local = @Local(type = double.class, name = "chessboardLength"))
	@Expression("@(chessboardLength) < ?")
	@ModifyExpressionValue(method = "write", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static double write_ZERO(
		double chessboardLength3,
		@Local(argsOnly = true, name = "value") Vec3 value,
		@Share("w") LocalDoubleRef w
	) {
		w.set(sanitize(((Vec4) value).w));
		return Math.max(chessboardLength3, Math.abs(w.get()));
	}
	@ModifyExpressionValue(method = "write", at = @At(
		value = "INVOKE",
		target = "Lio/netty/buffer/ByteBuf;writeInt(I)Lio/netty/buffer/ByteBuf;"
	))
	private static ByteBuf write_ifNotPartial(
		ByteBuf output,
		@Local(name = "isPartial") boolean isPartial,
		@Share("w") LocalDoubleRef w
	) {
		if (!isPartial) output.writeDouble(w.get());
		return output;
	}
	@ModifyExpressionValue(method = "write", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/network/VarInt;write(Lio/netty/buffer/ByteBuf;I)Lio/netty/buffer/ByteBuf;"
	))
	private static ByteBuf write_ifPartial(
		ByteBuf output,
		@Share("w") LocalDoubleRef w
	) {
		output.writeDouble(w.get());
		return output;
	}
}
