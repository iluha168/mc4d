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
	DELTA_W(10);

	@Shadow
	RelativeMixin(int bit) {}

	@Definition(id = "DELTA", field = "Lnet/minecraft/world/entity/Relative;DELTA:Ljava/util/Set;")
	@Expression("DELTA = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Set<Relative> DELTA(Set<Relative> original) {
		Set<Relative> set = new HashSet<>(original);
		set.add(Relative4.DELTA_W);
		return ImmutableSet.copyOf(set);
	}

	@Overwrite
	public static Set<Relative> position(boolean relativeX, boolean relativeY, boolean relativeZ) {
		throw Err4.arguments3("Relative4#position");
	}

	@Overwrite
	public static Set<Relative> direction(boolean relativeX, boolean relativeY, boolean relativeZ) {
		throw Err4.arguments3("Relative4#direction");
	}
}
