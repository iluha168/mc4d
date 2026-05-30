package com.iluha168.mc4d.mixin.voxelshape4;

import net.minecraft.util.Util;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SubShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubShape.class)
public class SubShapeMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	void banBaseClass(DiscreteVoxelShape parent, int startX, int startY, int startZ, int endX, int endY, int endZ, CallbackInfo ci) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use SubShape4::new instead."));
	}
}
