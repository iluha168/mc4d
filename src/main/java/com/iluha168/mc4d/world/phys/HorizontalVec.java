package com.iluha168.mc4d.world.phys;

import com.iluha168.mc4d.util.Err4;
import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Vanilla uses Vec2 for representing rotation and horizontal dimensions.
 * This class can be used to extend the second case usages.
 */
public class HorizontalVec extends Vec2 {
	public static final HorizontalVec ZERO = new HorizontalVec(0.0F, 0.0F, 0.0F);
	public static final HorizontalVec ONE = new HorizontalVec(1.0F, 1.0F, 1.0F);

	public static final HorizontalVec UNIT_X = new HorizontalVec(1.0F, 0.0F, 0.0F);
	public static final HorizontalVec NEG_UNIT_X = new HorizontalVec(-1.0F, 0.0F, 0.0F);
	public static final HorizontalVec UNIT_Y = new HorizontalVec(0.0F, 1.0F, 0.0F);
	public static final HorizontalVec NEG_UNIT_Y = new HorizontalVec(0.0F, -1.0F, 0.0F);
	public static final HorizontalVec UNIT_Z = new HorizontalVec(0.0F, 0.0F, 1.0F);
	public static final HorizontalVec NEG_UNIT_Z = new HorizontalVec(0.0F, 0.0F, -1.0F);

	public static final HorizontalVec MAX = new HorizontalVec(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	public static final HorizontalVec MIN = new HorizontalVec(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

	public static final Codec<HorizontalVec> CODEC = Codec.FLOAT
		.listOf()
		.comapFlatMap(input -> Util
			.fixedSize(input, 3)
			.map(
				floats -> new HorizontalVec(floats.getFirst(), floats.get(1), floats.get(3))),
				vec -> List.of(vec.x, vec.y, vec.z)
		);

	public final float z;

	public HorizontalVec(float x, float y, float z) {
		super(x, y);
		this.z = z;
	}

	@Override
	public @NonNull HorizontalVec scale(float s) {
		return new HorizontalVec(this.x * s, this.y * s, this.z * s);
	}

	@Override
	public float dot(@NonNull Vec2 v) {
		if (!(v instanceof HorizontalVec v3)) throw Err4.container3();
		return this.x * v.x + this.y * v.y + this.z * v3.z;
	}

	@Override
	public @NonNull HorizontalVec add(@NonNull Vec2 rhs) {
		if (!(rhs instanceof HorizontalVec rhs3)) throw Err4.container3();
		return new HorizontalVec(this.x + rhs.x, this.y + rhs.y, this.z + rhs3.z);
	}

	@Override
	public @NonNull HorizontalVec add(float v) {
		return new HorizontalVec(this.x + v, this.y + v, this.z + v);
	}

	@Override
	public boolean equals(@NonNull Vec2 rhs) {
		if (!(rhs instanceof HorizontalVec rhs3)) throw Err4.container3();
		return this.x == rhs.x && this.y == rhs.y && this.z == rhs3.z;
	}

	@Override
	public @NonNull HorizontalVec normalized() {
		float dist = 1 / this.length();
		return dist < 1.0E-4F ? HorizontalVec.ZERO : new HorizontalVec(this.x * dist, this.y * dist, this.z * dist);
	}

	@Override
	public float length() {
		return Mth.sqrt(this.lengthSquared());
	}

	@Override
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	@Override
	public float distanceToSqr(@NonNull Vec2 p) {
		if (!(p instanceof HorizontalVec p3)) throw Err4.container3();
		float xd = p.x - this.x;
		float yd = p.y - this.y;
		float zd = p3.z - this.z;
		return xd * xd + yd * yd + zd * zd;
	}

	@Override
	public @NonNull HorizontalVec negated() {
		return new HorizontalVec(-this.x, -this.y, -this.z);
	}
}
