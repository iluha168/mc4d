package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import net.minecraft.util.Util;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BitSetDiscreteVoxelShape.class)
public class BitSetDiscreteVoxelShapeMixin {
	@Inject(method = "<init>*", at = @At("TAIL"))
	void banBaseClass(int xSize, int ySize, int zSize, CallbackInfo ci) {
		if (!DiscreteVoxelShape4.UNSAFE_DISABLE_3D_ERRORS) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use BitSetDiscreteVoxelShape4::new instead."));
		}
	}
}
