package com.iluha168.mc4d.mixin.level4;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.chunk.Strategy4;
import net.minecraft.world.level.chunk.Strategy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Strategy.class)
public class StrategyMixin<T> implements Strategy4<T> {
	@Shadow
	@Final
	private int bitsPerAxis;

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 3))
	int init4(int constant) {
		return 4; // Hell yeah 4D!
	}

	@Overwrite
	public int getIndex(int x, int y, int z) {
		throw Err4.arguments3("Strategy4#getIndex");
	}
	@Override
	public int getIndex(int x, int y, int z, int w) {
		return ((w << this.bitsPerAxis | y) << this.bitsPerAxis | z) << this.bitsPerAxis | x;
	}
}
