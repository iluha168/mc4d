package com.iluha168.mc4d.mixin.voxelshape4.patches;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.level.block.Block4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(SegmentableBlock.class)
interface SegmentableBlockMixin {
	@Shadow
	double getShapeHeight();

	/**
	 * @author iluha168
	 * @reason Relies on the fact that in 2D, 4 directions can be mapped to 4 segments.
	 * With 8 segments, 6 3D directions need a different algo entirely.
	 */
	@Overwrite
	default Function<BlockState, VoxelShape> getShapeCalculator(EnumProperty<Direction> facing, IntegerProperty amount) {
		/* Horizontal: 1/8 of a 3D block, vertical: [0; height] */
		VoxelShape segment = Block4.box(0, 0, 0, 0, 8, this.getShapeHeight(), 8, 8);
		VoxelShape[] segments = new VoxelShape[]{
			/* 0 */segment,                                         // X- Z- W-
			/* 1 */segment.move(new Vec4(0, 0, 0, .5)), // X- Z- W+
			/* 2 */segment.move(new Vec4(0, 0, .5, .5)), // X- Z+ W+
			/* 3 */segment.move(new Vec4(0, 0, .5, 0)), // X- Z+ W-
			/* 4 */segment.move(new Vec4(.5, 0, .5, 0)), // X+ Z+ W-
			/* 5 */segment.move(new Vec4(.5, 0, 0, 0)), // X+ Z- W-
			/* 6 */segment.move(new Vec4(.5, 0, 0, .5)), // X+ Z- W+
			/* 7 */segment.move(new Vec4(.5, 0, .5, .5)), // X+ Z+ W+
		};

		return state -> {
			VoxelShape shape = Shapes.empty();
			Direction facingDir = state.getValue(facing);
			int startingIndex = facingDir == Direction4.ANA ? 1 :
				facingDir == Direction4.KATA ? 3 :
				switch (facingDir) {
					case Direction.WEST -> 0;
					// case Direction4.ANA -> 1;
					case Direction.SOUTH -> 2;
					// case Direction4.KATA -> 3;
					case Direction.EAST -> 4;
					case Direction.NORTH -> 5;
					default -> throw new IllegalStateException("Unknown horizontal direction (" + facingDir + ")");
				};
			int count = state.getValue(amount);

			for (int i = 0; i < count; i++) {
				shape = Shapes.or(shape, segments[(startingIndex + i) % segments.length]);
			}

			return shape.singleEncompassing();
		};
	}
}
