package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleEngine4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.iluha168.mc4d.api.net.minecraft.world.level.Level4;
import com.iluha168.mc4d.api.net.minecraft.world.phys.Vec4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkParticles.class)
class FireworkParticlesMixin {
	@Mixin(FireworkParticles.FlashProvider.class)
	abstract static class FlashProviderMixin implements ParticleProvider<ColorParticleOption>, ParticleProvider4<ColorParticleOption> {
		@Override
		public @Nullable Particle createParticle(ColorParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}

	@Mixin(targets = "net.minecraft.client.particle.FireworkParticles$SparkParticle")
	abstract static class SparkParticleMixin extends SingleQuadParticleMixin {
		@Override
		public void init_finish(double w, double xd, double yd, double zd, double wd) {
			super.init_finish(w);
            this.xd = xd;
            this.yd = yd;
            this.zd = zd;
			this.wd = wd;
		}

		@ModifyExpressionValue(method = "tick", at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/client/particle/ParticleEngine;Lnet/minecraft/client/particle/SpriteSet;)Lnet/minecraft/client/particle/FireworkParticles$SparkParticle;"
		))
		FireworkParticles.SparkParticle tick(FireworkParticles.SparkParticle particle) {
			((Particle4) particle).init_finish(this.w(), 0.0, 0.0, 0.0, 0.0);
			return particle;
		}
	}

	@Mixin(FireworkParticles.SparkProvider.class)
	abstract static class SparkProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}

	@Mixin(FireworkParticles.Starter.class)
	abstract static class StarterMixin extends ParticleMixin {
		@Shadow
		@Final
		private ParticleEngine engine;

		@Override
		public void init_finish(double w, double xd, double yd, double zd, double wd) {
			super.init_finish(w);
			this.xd = xd;
			this.yd = yd;
			this.zd = zd;
			this.wd = wd;
		}

		@Redirect(method = "tick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
		))
		void tick_playLocalSound(ClientLevel instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
			((Level4) instance).playLocalSound(x, y, z, this.w(), sound, source, volume, pitch, distanceDelay);
		}
		@Redirect(method = "tick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/ParticleEngine;createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"
		))
		@Nullable Particle tick_createParticle(ParticleEngine instance, ParticleOptions options, double x, double y, double z, double xa, double ya, double za) {
			return ((ParticleEngine4) instance).createParticle(options, x, y, z, this.w(), xa, ya, za, za);
		}

		@Redirect(method = "isFarAwayFromCamera", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(DDD)D"
		))
		double isFarAwayFromCamera(Vec3 instance, double x, double y, double z) {
			return ((Vec4) instance).distanceToSqr(x, y, z, this.w());
		}

		@Overwrite
		@Deprecated
		public void createParticle(double x, double y, double z, double xa, double ya, double za, IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle) {
			throw Err4.arguments3(null);
		}
		@Unique
		public void createParticle(double x, double y, double z, double w, double xa, double ya, double za, double wa, IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle) {
			final FireworkParticles.SparkParticle sparkParticle = (FireworkParticles.SparkParticle) ((ParticleEngine4) this.engine)
				.createParticle(ParticleTypes.FIREWORK, x, y, z, w, xa, ya, za, wa);
			//noinspection DataFlowIssue
			sparkParticle.setTrail(trail);
			sparkParticle.setTwinkle(twinkle);
			sparkParticle.setAlpha(0.99F);
			sparkParticle.setColor(Util.getRandom(rgbColors, this.random));
			if (!fadeColors.isEmpty()) {
				sparkParticle.setFadeColor(Util.getRandom(fadeColors, this.random));
			}
		}

		@Definition(id = "yStep", local = @Local(type = int.class, name = "yStep"))
		@Definition(id = "steps", local = @Local(type = int.class, name = "steps", argsOnly = true))
		@Expression("yStep = @(-steps)")
		@ModifyExpressionValue(method = "createParticleBall", at = @At("MIXINEXTRAS:EXPRESSION"))
		int createParticleBall_wStep(int negSteps, @Share("wStep") LocalIntRef wStep) {
			wStep.set(negSteps);
			return negSteps;
		}
		// This does apply properly, IDE is lying
		@Definition(id = "yStep", local = @Local(type = int.class, name = "yStep"))
		@Expression("yStep = yStep + @(1)")
		@ModifyExpressionValue(method = "createParticleBall", at = @At("MIXINEXTRAS:EXPRESSION"))
		int createParticleBall_incrementWStep(int one, @Share("wStep") LocalIntRef wStep, @Local(argsOnly = true, name = "steps") int steps) {
			final int wNext = wStep.get() + 1;
			if (wNext <= steps) {
				wStep.set(wNext);
				return 0;
			}
			wStep.set(-steps);
			return 1;
		}
		@ModifyArg(method = "createParticleBall", at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;sqrt(D)D"
		))
		double createParticleBall_sqrt(double dist3d, @Share("wStep") LocalIntRef wStep, @Share("wa") LocalDoubleRef wa) {
			wa.set(wStep.get() + (this.random.nextDouble() - this.random.nextDouble()) * 0.5);
			return dist3d + wa.get() * wa.get();
		}
		@Redirect(method = "createParticleBall", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/FireworkParticles$Starter;createParticle(DDDDDDLit/unimi/dsi/fastutil/ints/IntList;Lit/unimi/dsi/fastutil/ints/IntList;ZZ)V"
		))
		void createParticleBall_createParticle(
			FireworkParticles.Starter instance,
			double x, double y, double z, double xa, double ya, double za,
			IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle,
			@Local(name = "len") double len,
			@Share("wa") LocalDoubleRef wa
		) {
			this.createParticle(x, y, z, this.w(), xa, ya, za, wa.get() / len, rgbColors, fadeColors, trail, twinkle);
		}
		@Definition(id = "xStep", local = @Local(type = int.class, name = "xStep"))
		@Definition(id = "steps", local = @Local(type = int.class, name = "steps", argsOnly = true))
		@Expression("xStep != steps")
		@ModifyExpressionValue(method = "createParticleBall", at = @At("MIXINEXTRAS:EXPRESSION"))
		boolean createParticleBall_onEdge(boolean original, @Share("wStep") LocalIntRef wStep, @Local(argsOnly = true, name = "steps") int steps) {
			return original && wStep.get() != -steps && wStep.get() != steps;
		}

		@Definition(id = "createParticle", method = "Lnet/minecraft/client/particle/FireworkParticles$Starter;createParticle(DDDDDDLit/unimi/dsi/fastutil/ints/IntList;Lit/unimi/dsi/fastutil/ints/IntList;ZZ)V")
		@Expression("this.createParticle(?, ?, ?, ?, ?, 0.0, ?, ?, ?, ?)")
		@Redirect(method = "createParticleShape", at = @At("MIXINEXTRAS:EXPRESSION"))
		void createParticleShape_createParticleStart(
			FireworkParticles.Starter instance,
			double x, double y, double z, double xa, double ya, double za,
			IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle,
			// Normal perpendicular vectors U and V that define the random shape plane.
			@Share("shapeUx") LocalDoubleRef shapeUx, @Share("shapeUz") LocalDoubleRef shapeUz, @Share("shapeUw") LocalDoubleRef shapeUw,
			@Share("shapeVz") LocalDoubleRef shapeVz, @Share("shapeVw") LocalDoubleRef shapeVw // Vx == 0
		) {
			// Choosing a random 2D plane in the horizontal 3D plane.
			final double circleCos = this.random.nextDouble() * 2.0 - 1.0;
			final double circleSin = Math.sqrt(1.0 - circleCos * circleCos);
			final double roll = this.random.nextDouble() * Mth.TWO_PI;
			final double rollCos = Math.cos(roll);
			final double rollSin = Math.sin(roll);
			shapeUx.set(circleSin);
			shapeUz.set(-circleCos * rollCos);
			shapeUw.set(-circleCos * rollSin);
			shapeVz.set(rollSin);
			shapeVw.set(-rollCos);

			this.createParticle(
				x, y, z, this.w(),
				xa * shapeUx.get(), ya, xa * shapeUz.get(), xa * shapeUw.get(),
				rgbColors, fadeColors, trail, twinkle
			);
		}
		@ModifyExpressionValue(method = "createParticleShape", at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;sin(D)D"
		))
		double createParticleShape_za_wa(
			double angleSin,
			@Local(name = "angle") double angle, @Local(name = "xa") double xa, @Share("wa") LocalDoubleRef wa,
			@Share("shapeUz") LocalDoubleRef shapeUz, @Share("shapeUw") LocalDoubleRef shapeUw,
			@Share("shapeVz") LocalDoubleRef shapeVz, @Share("shapeVw") LocalDoubleRef shapeVw
		) {
			final double angleCos = Math.cos(angle);
			wa.set(xa * (angleCos * shapeUw.get() + angleSin * shapeVw.get()));
			return angleCos * shapeUz.get() + angleSin * shapeVz.get();
		}
		@ModifyExpressionValue(method = "createParticleShape", at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;cos(D)D"
		))
		double createParticleShape_xa(double angleCos, @Share("shapeUx") LocalDoubleRef shapeUx) {
			return angleCos * shapeUx.get();
		}
		@Definition(id = "createParticle", method = "Lnet/minecraft/client/particle/FireworkParticles$Starter;createParticle(DDDDDDLit/unimi/dsi/fastutil/ints/IntList;Lit/unimi/dsi/fastutil/ints/IntList;ZZ)V")
		@Expression("this.createParticle(?, ?, ?, ?, ?, ? * ?, ?, ?, ?, ?)")
		@Redirect(method = "createParticleShape", at = @At("MIXINEXTRAS:EXPRESSION"))
		void createParticleShape_createParticle(
			FireworkParticles.Starter instance,
			double x, double y, double z, double xa, double ya, double za,
			IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle,
			@Local(name = "flip") double flip,
			@Share("wa") LocalDoubleRef wa
		) {
			this.createParticle(x, y, z, this.w, xa, ya, za, wa.get() * flip, rgbColors, fadeColors, trail, twinkle);
		}

		@Inject(method = "createParticleBurst", at = @At("HEAD"))
		void createParticleBurst_baseOffW(CallbackInfo ci, @Share("baseOffW") LocalDoubleRef baseOffW) {
			baseOffW.set(this.random.nextGaussian() * 0.05);
		}
		@Redirect(method = "createParticleBurst", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/FireworkParticles$Starter;createParticle(DDDDDDLit/unimi/dsi/fastutil/ints/IntList;Lit/unimi/dsi/fastutil/ints/IntList;ZZ)V"
		))
		void createParticleBurst_createParticle(
			FireworkParticles.Starter instance,
			double x, double y, double z, double xa, double ya, double za,
			IntList rgbColors, IntList fadeColors, boolean trail, boolean twinkle,
			@Share("baseOffW") LocalDoubleRef baseOffW
		) {
			final double wa = this.wd() * 0.5 + this.random.nextGaussian() * 0.15 + baseOffW.get();
			this.createParticle(x, y, z, this.w, xa, ya, za, wa, rgbColors, fadeColors, trail, twinkle);
		}
	}
}
