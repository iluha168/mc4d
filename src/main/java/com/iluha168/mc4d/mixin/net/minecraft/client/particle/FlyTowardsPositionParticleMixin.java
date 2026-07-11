package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.FlyTowardsPositionParticle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlyTowardsPositionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlyTowardsPositionParticle.class)
abstract class FlyTowardsPositionParticleMixin extends SingleQuadParticleMixin implements FlyTowardsPositionParticle4 {
	@Mutable @Shadow @Final	private double xStart;
	@Mutable @Shadow @Final	private double yStart;
	@Mutable @Shadow @Final	private double zStart;
	@Unique                 private double wStart;

	@Override
	public void init_finish(double x, double y, double z, double w, double xd, double yd, double zd, double wd) {
		if (this.initIncomplete != 3) {
			throw new IllegalStateException("Programmer error: use FlyTowardsPositionParticle4#init_finish.");
		}
		try {
			this.initIncomplete = 1;
			this.init_finish(w);
			this.xd = xd;
			this.yd = yd;
			this.zd = zd;
			this.wd = wd;
			this.xStart = x;
			this.yStart = y;
			this.zStart = z;
			this.wStart = w;
			this.xo = x + xd;
			this.yo = y + yd;
			this.zo = z + zd;
			this.wo = w + wd;
			this.x = this.xo;
			this.y = this.yo;
			this.z = this.zo;
			this.w = this.wo;
		} catch (Throwable e) {
			this.initIncomplete = 3;
			throw e;
		}
	}
	@Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDZLnet/minecraft/client/particle/Particle$LifetimeAlpha;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V", at = @At("TAIL"))
	void init(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, boolean isGlowing, Particle.LifetimeAlpha lifetimeAlpha, TextureAtlasSprite sprite, CallbackInfo ci) {
		this.initIncomplete = 3;
	}

	@SuppressWarnings({"RedundantMethodOverride", "deprecation"})
	@Overwrite
	@Deprecated
	public void move(double xa, double ya, double za) {
		throw Err4.arguments3("Particle4#move");
	}
	@Override
	public void move(double xa, double ya, double za, double wa) {
		this.setBoundingBox(((AABB4) this.getBoundingBox()).move(xa, ya, za, wa));
		this.setLocationFromBoundingbox();
	}

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/FlyTowardsPositionParticle;zo:D")
	@Expression("this.zo = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/FlyTowardsPositionParticle;z:D")
	@Expression("this.z = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_w(CallbackInfo ci, @Local(name = "pos") float pos) {
		this.w = this.wStart + this.wd * pos;
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.FlyTowardsPositionParticle$EnchantProvider",
		"net.minecraft.client.particle.FlyTowardsPositionParticle$NautilusProvider",
		"net.minecraft.client.particle.FlyTowardsPositionParticle$VaultConnectionProvider",
	})
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((FlyTowardsPositionParticle4) particle).init_finish(x, y, z, w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
