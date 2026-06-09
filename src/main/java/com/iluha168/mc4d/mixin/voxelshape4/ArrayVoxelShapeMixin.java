package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.shapes.ArrayVoxelShape4;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrayVoxelShape.class)
public class ArrayVoxelShapeMixin {
	@Inject(method = "<init>(Lnet/minecraft/world/phys/shapes/DiscreteVoxelShape;Lit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;)V", at = @At("TAIL"))
	void banBaseClass(DiscreteVoxelShape shape, DoubleList xs, DoubleList ys, DoubleList zs, CallbackInfo ci) {
		if (!((ArrayVoxelShape) (Object) this instanceof ArrayVoxelShape4)) {
			throw Err4.arguments3("ArrayVoxelShape4::new");
		}
	}
}
