package com.iluha168.mc4d.mixin.net.minecraft.network.protocol.game;

import com.iluha168.mc4d.network.protocol.game.VecDeltaCodec4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VecDeltaCodec.class)
class VecDeltaCodecMixin implements VecDeltaCodec4 {
	@Shadow
	private Vec3 base;

	@Shadow
	static long encode(double input) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	static double decode(long v) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Overwrite
	@Deprecated
	public Vec3 decode(long xa, long ya, long za) {
		throw Err4.arguments3("VecDeltaCodec4#decode");
	}
	@Override
	public Vec4 decode(long xa, long ya, long za, long wa) {
		if (xa == 0L && ya == 0L && za == 0L && wa == 0L) {
			return (Vec4) this.base;
		}
		final double baseW = ((Vec4) this.base).w;
		final double x = xa == 0L ? this.base.x : decode(encode(this.base.x) + xa);
		final double y = ya == 0L ? this.base.y : decode(encode(this.base.y) + ya);
		final double z = za == 0L ? this.base.z : decode(encode(this.base.z) + za);
		@SuppressWarnings("ConstantValue") final double w = wa == 0L ? baseW : decode(encode(baseW) + wa);
		return new Vec4(x, y, z, w);
	}

	@Override
	public long encodeW(Vec4 pos) {
		return encode(pos.w) - encode(((Vec4) this.base).w);
	}
}
