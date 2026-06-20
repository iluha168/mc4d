package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.ParticleEngine4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(ParticleEngine.class)
abstract class ParticleEngineMixin implements ParticleEngine4 {
	@Shadow
	protected ClientLevel level;

	@Shadow
	@Final
	private ParticleResources resourceManager;

	@Shadow
	@Final
	private RandomSource random;

	@Shadow
	public abstract void add(Particle p);

	@Overwrite
	@Deprecated
	public @Nullable Particle createParticle(ParticleOptions options, double x, double y, double z, double xa, double ya, double za) {
		throw Err4.arguments3("ParticleEngine4#createParticle");
	}
	@Override
	public @Nullable Particle createParticle(ParticleOptions options, double x, double y, double z, double w, double xa, double ya, double za, double wa) {
		Particle particle = this.makeParticle(options, x, y, z, w, xa, ya, za, wa);
		if (particle != null) {
			this.add(particle);
			return particle;
		} else {
			return null;
		}
	}

	@Overwrite
	@Deprecated
	private <T extends ParticleOptions> @Nullable Particle makeParticle(T options, double x, double y, double z, double xa, double ya, double za) {
		throw Err4.arguments3("ParticleEngine4#makeParticle");
	}
	@Unique
	private <T extends ParticleOptions> @Nullable Particle makeParticle(T options, double x, double y, double z, double w, double xa, double ya, double za, double wa) {
		ParticleProvider<?> provider = this.resourceManager.getProviders().get(BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType()));
		if (provider == null) return null;
		if (!(provider instanceof ParticleProvider4<?> provider4)) throw Err4.container3();
		//noinspection unchecked
		return ((ParticleProvider4<T>) provider4).createParticle(options, this.level, x, y, z, w, xa, ya, za, wa, this.random);
	}
}
