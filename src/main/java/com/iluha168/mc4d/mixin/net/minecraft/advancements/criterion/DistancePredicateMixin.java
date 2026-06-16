package com.iluha168.mc4d.mixin.net.minecraft.advancements.criterion;

import com.iluha168.mc4d.advancements.criterion.DistancePredicate4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DistancePredicate.class)
class DistancePredicateMixin implements DistancePredicate4 {
	@Shadow
	@Final
	private MinMaxBounds.Doubles x;

	@Shadow
	@Final
	private MinMaxBounds.Doubles y;

	@Shadow
	@Final
	private MinMaxBounds.Doubles z;

	@Shadow
	@Final
	private MinMaxBounds.Doubles horizontal;

	@Shadow
	@Final
	private MinMaxBounds.Doubles absolute;

	@Unique private MinMaxBounds.Doubles w;

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(CallbackInfo ci) {
		this.w = MinMaxBounds.Doubles.ANY;
	}

	@Override
	public MinMaxBounds.Doubles w() {
		return this.w;
	}
	@Override
	public void setW(MinMaxBounds.Doubles w) {
		this.w = w;
	}

	@Definition(id = "CODEC", field = "Lnet/minecraft/advancements/criterion/DistancePredicate;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("CODEC = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Codec<DistancePredicate> CODEC(Codec<DistancePredicate> original) {
		return RecordCodecBuilder.create(
			i -> i.group(
					MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::x),
					MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::y),
					MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::z),
					MinMaxBounds.Doubles.CODEC.optionalFieldOf("w", MinMaxBounds.Doubles.ANY).forGetter(predicate -> DistancePredicate4.as(predicate).w()),
					MinMaxBounds.Doubles.CODEC.optionalFieldOf("horizontal", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::horizontal),
					MinMaxBounds.Doubles.CODEC.optionalFieldOf("absolute", MinMaxBounds.Doubles.ANY).forGetter(DistancePredicate::absolute)
				)
				.apply(i, DistancePredicate4::from)
		);
	}

	@Overwrite
	@Deprecated
	public boolean matches(double x0, double y0, double z0, double x1, double y1, double z1) {
		throw Err4.arguments3("DistancePredicate4#matches");
	}
	@Override
	public boolean matches(double x0, double y0, double z0, double w0, double x1, double y1, double z1, double w1) {
		float xd = (float) (x0 - x1);
		float yd = (float) (y0 - y1);
		float zd = (float) (z0 - z1);
		float wd = (float) (w0 - w1);
		return this.x.matches(Mth.abs(xd)) && this.y.matches(Mth.abs(yd)) && this.z.matches(Mth.abs(zd)) && this.w.matches(Mth.abs(wd))
			&& this.horizontal.matchesSqr(xd * xd + zd * zd + wd * wd) && this.absolute.matchesSqr(xd * xd + yd * yd + zd * zd + wd * wd);
	}
}
