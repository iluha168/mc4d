package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FallingDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingDustParticle.class)
abstract class FallingDustParticleMixin extends SingleQuadParticleMixin {
	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/FallingDustParticle;zo:D")
	@Expression("this.zo = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/FallingDustParticle;move(DDD)V"
	))
	void tick(FallingDustParticle instance, double x, double y, double z) {
		((Particle4) instance).move(x, y, z, this.wd);
	}

	@Mixin(FallingDustParticle.Provider.class)
	static class ProviderMixin implements ParticleProvider4<BlockParticleOption> {
		@Shadow
		@Final
		private SpriteSet sprite;

		@Overwrite
		@Deprecated
		public @Nullable Particle createParticle(
			BlockParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
		) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			BlockState blockState = options.getState();
			if (!blockState.isAir() && blockState.getRenderShape() == RenderShape.INVISIBLE) {
				return null;
			}
			BlockPos pos = BlockPos4.containing(x, y, z, w);
			int tintColor;
			if (blockState.getBlock() instanceof FallingBlock fallingBlock) {
				tintColor = fallingBlock.getDustColor(blockState, level, pos);
			} else {
				BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(blockState, 0);
				if (tintSource != null) {
					tintColor = tintSource.colorAsTerrainParticle(blockState, level, pos);
				} else {
					tintColor = blockState.getMapColor(level, pos).col;
				}
			}

			float r = (tintColor >> 16 & 0xFF) / 255.0F;
			float g = (tintColor >> 8 & 0xFF) / 255.0F;
			float b = (tintColor & 0xFF) / 255.0F;
			FallingDustParticle particle = new FallingDustParticle(level, x, y, z, r, g, b, this.sprite);
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}
}
