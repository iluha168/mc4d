package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundSetStructureBlockPacket.class)
class ServerboundSetStructureBlockPacketMixin {
	@Shadow @Final private BlockPos offset;
	@Shadow @Final private Vec3i size;

	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos init_offset(
		BlockPos original,
		@Local(argsOnly = true, name = "input") FriendlyByteBuf input,
		@Local(name = "maxOffset") int maxOffset
	) {
		Vec4i.setW(original, Mth.clamp(input.readByte(), -maxOffset, maxOffset));
		return original;
	}
	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i init_size(
		Vec3i original,
		@Local(argsOnly = true, name = "input") FriendlyByteBuf input,
		@Local(name = "maxOffset") int maxOffset
	) {
		Vec4i.setW(original, Mth.clamp(input.readByte(), 0, maxOffset));
		return original;
	}

	@Definition(id = "writeByte", method = "Lnet/minecraft/network/FriendlyByteBuf;writeByte(I)Lnet/minecraft/network/FriendlyByteBuf;")
	@Definition(id = "getZ", method = "Lnet/minecraft/core/BlockPos;getZ()I")
	@Expression("?.writeByte(?.getZ())")
	@Inject(method = "write", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
	void write_offsetW(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeByte(Vec4i.getW(this.offset));
	}
	@Definition(id = "writeByte", method = "Lnet/minecraft/network/FriendlyByteBuf;writeByte(I)Lnet/minecraft/network/FriendlyByteBuf;")
	@Definition(id = "getZ", method = "Lnet/minecraft/core/Vec3i;getZ()I")
	@Expression("?.writeByte(?.getZ())")
	@Inject(method = "write", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
	void write_sizeW(FriendlyByteBuf output, CallbackInfo ci) {
		output.writeByte(Vec4i.getW(this.size));
	}
}
