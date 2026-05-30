package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.phys.shapes.DiscreteVoxelShape4;
import com.iluha168.mc4d.world.phys.shapes.SubShape4;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SliceShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SliceShape.class)
public class SliceShapeMixin {
	/**
	 * @author iluha168
	 * @reason a @Redirect would have required the return type to be SubShape. SubShape4 is not a subclass of it.
	 */
	@Overwrite
	private static DiscreteVoxelShape makeSlice(DiscreteVoxelShape delegate, Direction.Axis axis, int point) {
		DiscreteVoxelShape4 delegate4 = (DiscreteVoxelShape4) delegate;
		Direction4.Axis axis4 = Direction4.Axis.as(axis);
		//noinspection SuspiciousNameCombination
		return new SubShape4(
			delegate4,
			axis4.choose(point, 0, 0, 0),
			axis4.choose(0, point, 0, 0),
			axis4.choose(0, 0, point, 0),
			axis4.choose(0, 0, 0, point),
			axis4.choose(point + 1, delegate4.xSize, delegate4.xSize, delegate4.xSize),
			axis4.choose(delegate4.ySize, point + 1, delegate4.ySize, delegate4.ySize),
			axis4.choose(delegate4.zSize, delegate4.zSize, point + 1, delegate4.zSize),
			axis4.choose(delegate4.wSize, delegate4.wSize, delegate4.wSize, point + 1)
		);
	}
}
