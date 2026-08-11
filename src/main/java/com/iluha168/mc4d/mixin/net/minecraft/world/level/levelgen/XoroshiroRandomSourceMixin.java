package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.util.Mth4;
import com.iluha168.mc4d.world.level.levelgen.PositionalRandomFactory4;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(XoroshiroRandomSource.class)
class XoroshiroRandomSourceMixin {
	@Mixin(XoroshiroRandomSource.XoroshiroPositionalRandomFactory.class)
	static class XoroshiroPositionalRandomFactoryMixin implements PositionalRandomFactory4 {
		@Shadow
		@Final
		private long seedLo;

		@Shadow
		@Final
		private long seedHi;

		@Overwrite
		@Deprecated
		public RandomSource at(int x, int y, int z) {
			throw Err4.arguments3("PositionalRandomFactory4#at");
		}
		@Override
		public RandomSource at(int x, int y, int z, int w) {
			final long positionalSeed = Mth4.getSeed(x, y, z, w);
			final long randomSeed = positionalSeed ^ this.seedLo;
			return new XoroshiroRandomSource(randomSeed, this.seedHi);
		}
	}
}
