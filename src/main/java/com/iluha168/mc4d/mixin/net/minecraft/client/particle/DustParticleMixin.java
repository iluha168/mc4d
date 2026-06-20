package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DustParticle.Provider.class)
class DustParticleProviderMixin implements ParticleProvider4<DustParticleOptions> {
	@Shadow
	@Final
	private SpriteSet sprites;

	@Overwrite
	@Deprecated
	public Particle createParticle(
		DustParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
	) {
		throw Err4.arguments3("ParticleProvider4#createParticle");
	}
	@Override
	public @Nullable Particle createParticle(DustParticleOptions options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
		DustParticle particle = new DustParticle(level, x, y, z, xAux, yAux, zAux, options, this.sprites);
		//noinspection DataFlowIssue
		((Particle4) particle).init_finish(w, wAux);
		return particle;
	}
}
