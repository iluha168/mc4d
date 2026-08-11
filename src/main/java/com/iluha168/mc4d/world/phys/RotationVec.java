package com.iluha168.mc4d.world.phys;

import com.iluha168.mc4d.util.Err4;
import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Vanilla uses {@link Vec2} for representing rotation and horizontal dimensions.
 * This class can be used to extend the rotation case usages.
 * <p>
 * Vanilla's rotation {@link Vec2} stores {@link Vec2#x} = xRot (pitch), {@link Vec2#y} = yRot (yaw)
 * This class adds {@link RotationVec#w} = wRot and {@link RotationVec#v} = vRot, more about them in README.md.
 * wRot is clamped to [-90, 90] like pitch; vRot wraps mod 360 like yaw.
 */
public class RotationVec extends Vec2 {
	public static final RotationVec ZERO = new RotationVec(0.0F, 0.0F, 0.0F, 0.0F);

	public static final Codec<RotationVec> CODEC = Codec.FLOAT
		.listOf()
		.comapFlatMap(input -> Util
			.fixedSize(input, 4)
			.map(floats -> new RotationVec(floats.getFirst(), floats.get(1), floats.get(2), floats.get(3))),
			vec -> List.of(vec.x, vec.y, vec.w, vec.v)
		);

	public final float w;
	public final float v;

	public RotationVec(float x, float y, float w, float v) {
		super(x, y);
		this.w = w;
		this.v = v;
	}

	private <T> T throw4() {
		throw Err4.math("Does not make sense for a rotation container. This is not a dimension vector.");
	}

	@Override
	@Deprecated
	public @NonNull RotationVec scale(float s) {
		return this.throw4();
	}

	@Override
	@Deprecated
	public float dot(@NonNull Vec2 v) {
		return this.throw4();
	}

	@Override
	public @NonNull RotationVec add(@NonNull Vec2 rhs) {
		if (!(rhs instanceof RotationVec rhs3)) throw Err4.container3();
		return new RotationVec(this.x + rhs.x, this.y + rhs.y, this.w + rhs3.w, this.v + rhs3.v);
	}

	@Override
	@Deprecated
	public @NonNull RotationVec add(float v) {
		return this.throw4();
	}

	@Override
	public boolean equals(@NonNull Vec2 rhs) {
		if (!(rhs instanceof RotationVec o)) throw Err4.container3();
		return this.x == o.x && this.y == o.y && this.w == o.w && this.v == o.v;
	}

	@Override
	@Deprecated
	public @NonNull RotationVec normalized() {
		return this.throw4();
	}

	@Override
	@Deprecated
	public float length() {
		return this.throw4();
	}

	@Override
	@Deprecated
	public float lengthSquared() {
		return this.throw4();
	}

	@Override
	@Deprecated
	public float distanceToSqr(@NonNull Vec2 p) {
		return this.throw4();
	}

	@Override
	public @NonNull RotationVec negated() {
		return new RotationVec(-this.x, -this.y, -this.w, -this.v);
	}

	/**
	 * An analogy to `random.nextFloat() * 360` used by yRot, for wRot.
	 * <p>
	 * {@return a random wRot in degrees}
	 */
	public static float randomWRotDeg(@NonNull RandomSource random) {
		return (float) Math.toDegrees(randomWRotRad(random));
	}

	/**
	 * An analogy to `random.nextFloat() * Math.PI * 2` used by yRot, for wRot.
	 * <p>
	 * {@return a random wRot in radians}
	 */
	public static double randomWRotRad(@NonNull RandomSource random) {
		// asin([-1, 1]) spreads evenly over the sphere instead of near the ana/kata poles.
		return Math.asin(random.nextFloat() * 2.0F - 1.0F);
	}
}
