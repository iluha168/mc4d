package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiscreteVoxelShape.class)
public class DiscreteVoxelShapeMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	void banBaseClass(int xSize, int ySize, int zSize, CallbackInfo ci) {
		if (!((DiscreteVoxelShape) (Object) this instanceof DiscreteVoxelShape4 || DiscreteVoxelShape4.UNSAFE_DISABLE_3D_ERRORS)) {
			throw Err4.arguments3("DiscreteVoxelShape4::new");
		}
	}
}
