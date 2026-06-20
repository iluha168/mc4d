package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Particle.class)
abstract
class ParticleMixin implements Particle4 {
	@Shadow
	protected float bbWidth;

	@Shadow
	protected double xo;

	@Shadow
	protected double yo;

	@Shadow
	protected double zo;

	@Shadow
	@Final
	protected RandomSource random;

	@Shadow
	protected double xd;

	@Shadow
	protected double yd;

	@Shadow
	protected double zd;

	@Shadow
	protected float friction;

	@Shadow
	protected double x;

	@Shadow
	protected double y;

	@Shadow
	protected double z;

	@Shadow
	protected float bbHeight;

	@Shadow
	public abstract void setBoundingBox(AABB bb);

	@SuppressWarnings("BooleanVariableAlwaysNegated")
	@Shadow
	private boolean stoppedByCollision;

	@Shadow
	protected boolean hasPhysics;

	@Shadow
	@Final
	private static double MAXIMUM_COLLISION_VELOCITY_SQUARED;

	@Shadow
	public abstract AABB getBoundingBox();

	@Shadow
	@Final
	protected ClientLevel level;

	@Shadow
	protected boolean onGround;

	@Shadow
	protected abstract void setLocationFromBoundingbox();

	@Shadow
	protected abstract int getLightCoords(float a);

	@Redirect(method = "<clinit>", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB INITIAL_AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new AABB4(minX, minY, minZ, 0, maxX, maxY, maxZ, 0);
	}

	@Definition(id = "MAXIMUM_COLLISION_VELOCITY_SQUARED", field = "Lnet/minecraft/client/particle/Particle;MAXIMUM_COLLISION_VELOCITY_SQUARED:D")
	@Expression("MAXIMUM_COLLISION_VELOCITY_SQUARED = @(?)")
	@Redirect(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static double MAXIMUM_COLLISION_VELOCITY_SQUARED(double x) {
		return x * 4 / 3; // It is used in an x*x+y*y+z*z formula. Here we get value for a single dimension, and then multiply by 4.
	}

	@Unique protected double wo;
	@Unique protected double w;
	@Unique protected double wd;
	@Unique	protected byte initIncomplete;

	@Override
	public double wo() {
		if (this.initIncomplete != 0) throw Err4.field4missing("wo");
		return this.wo;
	}
	@Override
	public double w() {
		if (this.initIncomplete != 0) throw Err4.field4missing("w");
		return this.w;
	}
	@Override
	public double wd() {
		if (this.initIncomplete != 0) throw Err4.field4missing("wd");
		return this.wd;
	}

	@Redirect(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDD)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/Particle;setPos(DDD)V"
	))
	void init_withoutInitialVelocity_incompleteByDefault(Particle instance, double x, double y, double z) {
		// Removing the setPos call, it is now in `init_finish`.
		this.initIncomplete = 1;
	}
	@Override
	public void init_finish(double w) {
		if (this.initIncomplete == 0) {
			throw new IllegalStateException("Programmer error: Particle4#init_finish called more than once.");
		}
		if (this.initIncomplete != 1) {
			throw new IllegalStateException("Programmer error: wrong Particle4#init_finish called. This Particle has been created with initial velocity.");
		}
		this.initIncomplete = 0;
		try {
			this.setPos(this.xo, this.yo, this.zo, w);
			this.wo = w;
		} catch (Throwable e) {
			this.initIncomplete = 1;
			throw e;
		}
	}

	@Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)V", at = @At("TAIL"))
	void init_withInitialVelocity_incompleteByDefault(ClientLevel level, double x, double y, double z, double xa, double ya, double za, CallbackInfo ci) {
		this.initIncomplete = 2;
	}
	@Override
	public void init_finish(double w, double wa) {
		if (this.initIncomplete != 2) {
			throw new IllegalStateException("Programmer error: wrong Particle4#init_finish called. This Particle has been created without initial velocity.");
		}
		try {
			this.initIncomplete = 1;
			this.init_finish(w); // this(level, x, y, z, w);
			this.wd = wa + (this.random.nextFloat() * 2.0F - 1.0F) * 0.4F;
			final double speed = (this.random.nextFloat() + this.random.nextFloat() + 1.0F) * 0.15F;
			final double dd = 1 / Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd + this.wd * this.wd);
			this.xd = this.xd * dd * speed * 0.4F;
			this.yd = this.yd * dd * speed * 0.4F + 0.1F;
			this.zd = this.zd * dd * speed * 0.4F;
			this.wd = this.wd * dd * speed * 0.4F;
		} catch (Throwable e) {
			this.initIncomplete = 2;
			throw e;
		}
	}

	@Inject(method = "setPower", at = @At("HEAD"))
	void setPower(float power, CallbackInfoReturnable<Particle> cir) {
		if (this.initIncomplete != 0) throw Err4.field4missing("wd");
		this.wd *= power;
	}

	@Overwrite
	@Deprecated
	public void setParticleSpeed(double xd, double yd, double zd) {
		throw Err4.arguments3("Particle4#setParticleSpeed");
	}
	@Override
	public void setParticleSpeed(double xd, double yd, double zd, double wd) {
		if (this.initIncomplete != 0) throw Err4.field4missing("wd");
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
		this.wd = wd;
	}

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/Particle;zo:D")
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/Particle;z:D")
	@Expression("this.zo = @(this.z)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/Particle;move(DDD)V"
	))
	void tick_move(Particle instance, double xa, double ya, double za) {
		((Particle4) (instance)).move(xa, ya, za, this.wd);
	}
	@Expression("1.1")
	@Inject(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	void tick_speedUpWhenYMotionIsBlocked(CallbackInfo ci) {
		this.wd *= 1.1;
	}
	@Definition(id = "friction", field = "Lnet/minecraft/client/particle/Particle;friction:F")
	@Expression("this.friction")
	@Inject(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	void tick_friction(CallbackInfo ci) {
		this.wd *= this.friction;
	}
	@Inject(method = "tick", at = @At(
		value = "CONSTANT",
		args = "doubleValue=0.699999988079071",
		ordinal = 0
	))
	void tick_onGround(CallbackInfo ci) {
		this.wd *= 0.7F;
	}

	@Definition(id = "y", field = "Lnet/minecraft/client/particle/Particle;y:D")
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/Particle;z:D")
	@Expression("@(? + this.y + ? + this.z) + ?")
	@ModifyExpressionValue(method = "toString", at = @At("MIXINEXTRAS:EXPRESSION"))
	String toString(String original) {
		return original + "," + (this.initIncomplete != 0 ? "missingno" : this.w);
	}

	@Redirect(method = "setSize", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB setSize(
		double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "w") float w,
		@Local(name = "aabb") AABB aabb
	) {
		AABB4 aabb4 = (AABB4) aabb;
		final double newMinW = (aabb4.minW + aabb4.maxW - w) / 2.0;
		return new AABB4(minX, minY, minZ, newMinW, maxX, maxY, maxZ, newMinW + this.bbWidth);
	}

	@Overwrite
	@Deprecated
	public void setPos(double x, double y, double z) {
		throw Err4.arguments3("Particle4#setPos");
	}
	@Override
	public void setPos(double x, double y, double z, double w) {
		if (this.initIncomplete != 0) throw Err4.field4missing("w");
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		float width = this.bbWidth / 2.0F;
		float height = this.bbHeight;
		this.setBoundingBox(new AABB4(
			x - width, y, z - width, w - width,
			x + width, y + height, z + width, w + width
		));
	}

	@Overwrite
	@Deprecated
	public void move(double xa, double ya, double za) {
		// TODO ban overrides of this too
		throw Err4.arguments3("Particle4#move");
	}
	@Override
	public void move(double xa, double ya, double za, double wa) {
		if (this.initIncomplete != 0) throw Err4.field4missing("wd");
		if (!this.stoppedByCollision) {
			double originalXa = xa;
			double originalYa = ya;
			double originalZa = za;
			double originalWa = wa;
			if (this.hasPhysics
				&& (xa != 0.0 || ya != 0.0 || za != 0.0 || wa != 0.0)
				&& xa * xa + ya * ya + za * za + wa * wa < MAXIMUM_COLLISION_VELOCITY_SQUARED
			) {
				Vec4 movement = (Vec4) Entity.collideBoundingBox(null, new Vec4(xa, ya, za, wa), this.getBoundingBox(), this.level, List.of());
				xa = movement.x;
				ya = movement.y;
				za = movement.z;
				wa = movement.w;
			}

			if (xa != 0.0 || ya != 0.0 || za != 0.0 || wa != 0.0) {
				this.setBoundingBox(((AABB4) this.getBoundingBox()).move(xa, ya, za, wa));
				this.setLocationFromBoundingbox();
			}

			if (Math.abs(originalYa) >= Mth.EPSILON && Math.abs(ya) < Mth.EPSILON) {
				this.stoppedByCollision = true;
			}

			this.onGround = originalYa != ya && originalYa < 0.0;
			if (originalXa != xa) this.xd = 0.0;
			if (originalZa != za) this.zd = 0.0;
			if (originalWa != wa) this.wd = 0.0;
		}
	}

	@Inject(method = "setLocationFromBoundingbox", at = @At("TAIL"))
	void setLocationFromBoundingbox(CallbackInfo ci, @Local(name = "aabb") AABB aabb) {
		if (!(aabb instanceof AABB4 aabb4)) throw Err4.container3();
		if (this.initIncomplete != 0) throw Err4.field4missing("w");
		this.w = (aabb4.minW + aabb4.maxW) / 2.0;
	}

	@Redirect(method = "getLightCoords", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getLightCoords(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.w());
	}

	@Redirect(method = "getPos", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPos(double x, double y, double z) {
		return new Vec4(x, y, z, this.w());
	}
}
