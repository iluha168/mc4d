package com.iluha168.mc4d.mixin.net.minecraft.core;

import com.iluha168.mc4d.core.Direction18;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("AddedEnumConstantsNamePattern")
@Mixin(Direction8.class)
public enum Direction8Mixin implements Direction18 {
	// Each horizontal direction has 4 neighbors now.
	KATA(Direction4.KATA),
	KATA_NORTH(Direction4.KATA, Direction.NORTH),
	KATA_EAST (Direction4.KATA, Direction.EAST ),
	KATA_SOUTH(Direction4.KATA, Direction.SOUTH),
	KATA_WEST (Direction4.KATA, Direction.WEST ),
	ANA(Direction4.ANA),
	ANA_NORTH(Direction4.ANA, Direction.NORTH),
	ANA_EAST (Direction4.ANA, Direction.EAST ),
	ANA_SOUTH(Direction4.ANA, Direction.SOUTH),
	ANA_WEST (Direction4.ANA, Direction.WEST );

	@Shadow
	Direction8Mixin(Direction... directions) {}

	@Shadow
	@Final
	private Vec3i step;

	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i init(Vec3i original) {
		Vec4i.setW(original, 0);
		return original;
	}

	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Vec3i;setZ(I)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i init(Vec3i original, @Local(name = "direction") Direction direction) {
		return ((Vec4i) original).setW(Vec4i.getW(this.step) + Direction4.as(direction).getStepW());
	}

	@Override
	public int getStepW() {
		return Vec4i.getW(this.step);
	}
}
