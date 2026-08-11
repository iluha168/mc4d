package com.iluha168.mc4d.mixin.net.minecraft.core.particles;

import com.iluha168.mc4d.world.phys.Vec4;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.network.codec.StreamCodec;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TrailParticleOption.class)
class TrailParticleOptionMixin {
	@Redirect(method = "lambda$static$0", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;",
		opcode = Opcodes.GETSTATIC
	))
	private static Codec<Vec4> CODEC() {
		return Vec4.CODEC;
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;STREAM_CODEC:Lnet/minecraft/network/codec/StreamCodec;",
		opcode = Opcodes.GETSTATIC
	))
	private static StreamCodec<ByteBuf, Vec4> STREAM_CODEC() {
		return Vec4.STREAM_CODEC;
	}
}
