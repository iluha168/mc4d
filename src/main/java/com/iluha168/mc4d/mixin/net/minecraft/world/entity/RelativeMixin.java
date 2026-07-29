package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Relative4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Relative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("AddedEnumConstantsNamePattern")
@Mixin(Relative.class)
public enum RelativeMixin implements Relative4 {
	// In case of collisions, blame vanilla for not using `.ordinal()`!
	W(9),
	W_ROT(10),
	V_ROT(11),
	DELTA_W(12);

	@Shadow
	RelativeMixin(int bit) {}

	@Definition(id = "ROTATION", field = "Lnet/minecraft/world/entity/Relative;ROTATION:Ljava/util/Set;")
	@Expression("ROTATION = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Set<Relative> ROTATION(Set<Relative> original) {
		Set<Relative> set = new HashSet<>(original);
		set.add(Relative4.W_ROT);
		set.add(Relative4.V_ROT);
		return ImmutableSet.copyOf(set);
	}
	@Definition(id = "DELTA", field = "Lnet/minecraft/world/entity/Relative;DELTA:Ljava/util/Set;")
	@Expression("DELTA = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Set<Relative> DELTA(Set<Relative> original) {
		Set<Relative> set = new HashSet<>(original);
		set.add(Relative4.DELTA_W);
		return ImmutableSet.copyOf(set);
	}

	@Overwrite
	@Deprecated
	public static Set<Relative> rotation(boolean relativeYRot, boolean relativeXRot) {
		throw Err4.rotation("Relative4#rotation");
	}

	@Overwrite
	@Deprecated
	public static Set<Relative> position(boolean relativeX, boolean relativeY, boolean relativeZ) {
		throw Err4.arguments3("Relative4#position");
	}

	@Overwrite
	@Deprecated
	public static Set<Relative> direction(boolean relativeX, boolean relativeY, boolean relativeZ) {
		throw Err4.arguments3("Relative4#direction");
	}
}
