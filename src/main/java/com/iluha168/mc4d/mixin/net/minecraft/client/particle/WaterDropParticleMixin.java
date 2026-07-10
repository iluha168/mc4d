package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.shapes.VoxelShape4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterDropParticle.class)
abstract class WaterDropParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WaterDropParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(WaterDropParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WaterDropParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(WaterDropParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WaterDropParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(WaterDropParticle instance, double value) {}

	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w, 0.0);
		this.xd *= 0.3F;
		this.yd = this.random.nextFloat() * 0.2F + 0.1F;
		this.zd *= 0.3F;
		this.wd *= 0.3F;
	}

	@Definition(id = "xo", field = "Lnet/minecraft/client/particle/WaterDropParticle;xo:D")
	@Definition(id = "x", field = "Lnet/minecraft/client/particle/WaterDropParticle;x:D")
	@Expression("this.xo = this.x")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/WaterDropParticle;move(DDD)V"
	))
	void tick_move(WaterDropParticle instance, double x, double y, double z) {
		((Particle4) instance).move(x, y, z, this.wd);
		this.wd *= 0.98F;
	}
	@Inject(method = "tick", at = @At(
		value = "CONSTANT",
		args = "doubleValue=0.699999988079071",
		ordinal = 0
	))
	void tick_onGround(CallbackInfo ci) {
		this.wd *= 0.699999988079071;
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tick_containing(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.w);
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;max(Lnet/minecraft/core/Direction$Axis;DD)D"
	))
	double tick_max(VoxelShape instance, Direction.Axis aAxis, double b, double c, @Local(name = "pos") BlockPos pos) {
		return ((VoxelShape4) instance).max(aAxis, b, c, this.w - Vec4i.getW(pos));
	}

	@Mixin(WaterDropParticle.Provider.class)
	static class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		@Final
		private SpriteSet sprite;

		@Overwrite
		@Deprecated
		public Particle createParticle(
			SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
		) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			WaterDropParticle particle = new WaterDropParticle(level, x, y, z, this.sprite.get(random));
			((Particle4) particle).init_finish(w, 0.0);
			return particle;
		}
	}
}
