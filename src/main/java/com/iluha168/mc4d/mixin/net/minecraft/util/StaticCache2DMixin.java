package com.iluha168.mc4d.mixin.net.minecraft.util;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.util.StaticCache3D;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.StaticCache2D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StaticCache2D.class)
class StaticCache2DMixin {
	@Definition(id = "Object", type = Object.class)
	@Expression("new Object[@(?)]")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	int size3D(int size2D) {
		if (!((StaticCache2D<?>) (Object) this instanceof StaticCache3D<?>)) {
			// It is fair to prohibit the 2D class because it is used only in chunk-generation-stuff, and not as a general purpose "util" it claims to be.
			throw Err4.arguments2("StaticCache3D::new");
		}
		return 0; // Cache is created in subclass
	}

	@Definition(id = "x", local = @Local(type = int.class, name = "x"))
	@Expression("x < ?")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean stopTheBaseClassInitLoop(boolean original) {
		return false;
	}

	@Overwrite
	private int getIndex(int x, int z) {
		throw Err4.arguments2("StaticCache3D#getIndex");
	}
}
