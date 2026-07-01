package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.network.protocol.game.ClientboundAddEntityPacket4;
import com.iluha168.mc4d.server.level.ServerLevel4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements Entity4 {
	@Shadow
	private Vec3 position;

	@Shadow
	private BlockPos blockPosition;

	@Shadow
	private @Nullable BlockState inBlockState;

	@Shadow
	private ChunkPos chunkPosition;

	@Shadow
	private EntityInLevelCallback levelCallback;

	@Shadow
	protected boolean firstTick;

	@Shadow
	private Level level;

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	@Shadow
	public boolean isRemoved() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public boolean isAddedToLevel() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public void setBoundingBox(AABB bb) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	protected AABB makeBoundingBox() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private @Nullable Entity vehicle;

	@Shadow
	protected void reapplyPosition() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public double xo;

	@Shadow
	public double yo;

	@Shadow
	public double zo;

	@Unique
	public double wo;

	@Unique
	public double wOld;

	@Shadow
	public void absSnapRotationTo(float yRot, float xRot) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public void setDeltaMovement(Vec3 deltaMovement) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public boolean touchingUnloadedChunk() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	protected void checkSupportingBlock(boolean onGround, @Nullable Vec3 movement) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public BlockPos getOnPosLegacy() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	protected void checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public boolean horizontalCollision;

	@Shadow
	public @Nullable Entity teleport(TeleportTransition transition) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public float getYRot() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public float getXRot() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public void setYRot(float yRot) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public void setXRot(float xRot) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public void setOldPosAndRot() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow public Vec3 position() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public Vec3 getForward() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public double getX() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public AABB getBoundingBox() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public double getZ() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public Level level() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public float getBbWidth() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public float getBbHeight() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public double getY() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	protected RandomSource random;

	@Shadow
	private EntityDimensions dimensions;

	@Shadow
	public Vec3 getDeltaMovement() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	private VecDeltaCodec packetPositionCodec;

	@Shadow
	public boolean needsSync;

	@Shadow
	private boolean isFree(AABB box) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public boolean shouldRenderAtSqrDistance(double distance) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Override
	public void setWO(double wo) {
		this.wo = wo;
	}
	@Override
	public double getWO() {
		return this.wo;
	}
	@Override
	public void setWOld(double wOld) {
		this.wOld = wOld;
	}
	@Override
	public double wOld() {
		return this.wOld;
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
	void setInitialPosWithVector(Entity instance, double x, double y, double z){
		this.position = Vec4.ZERO;
		instance.setPos(new Vec4(x, y, z, 0.0));
	}
	@Definition(id = "deltaMovement", field = "Lnet/minecraft/world/entity/Entity;deltaMovement:Lnet/minecraft/world/phys/Vec3;")
	@Definition(id = "ZERO", field = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;")
	@Expression("this.deltaMovement = @(ZERO)")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Vec3 deltaMovement(Vec3 original) {
		return Vec4.ZERO;
	}

	@Overwrite
	@Deprecated
	public void syncPacketPositionCodec(double x, double y, double z) {
		throw Err4.arguments3("Entity4#syncPacketPositionCodec");
	}
	@Override
	public void syncPacketPositionCodec(double x, double y, double z, double w) {
		this.packetPositionCodec.setBase(new Vec4(x, y, z, w));
	}

	@Redirect(method = "closerThan(Lnet/minecraft/world/entity/Entity;DD)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;lengthSquared(DD)D"
	))
	double closerThan(double x, double y, @Local(argsOnly = true, name = "other") Entity other) {
		final double dw = ((Entity4) other).getW() - this.getW();
		return Mth.lengthSquared(x, y, dw);
	}

	@Overwrite
	@Deprecated
	public void setPos(double x, double y, double z) {
		throw Err4.arguments3("Entity#setPos(Vec4)");
	}
	@Overwrite
	public final void setPos(Vec3 pos) {
		this.setPosRaw(pos.x, pos.y, pos.z, ((Vec4) pos).w);
		this.setBoundingBox(this.makeBoundingBox());
	}

	@Redirect(method = "reapplyPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
	))
	void reapplyPosition4(Entity instance, double x, double y, double z){
		instance.setPos(new Vec4(x, y, z, ((Vec4) this.position).w));
	}

	// TODO? turn

	@Redirect(method = "lavaHurt", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void lavaHurt(ServerLevel instance, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
		((Level4) instance).playSound(except, x, y, z, this.getW(), sound, source, volume, pitch);
	}

	@Overwrite
	@Deprecated
	public boolean isFree(double xa, double ya, double za) {
		throw Err4.arguments3("Entity4#isFree");
	}
	@Override
	public boolean isFree(double xa, double ya, double za, double wa) {
		return this.isFree(((AABB4) this.getBoundingBox()).move(xa, ya, za, wa));
	}

	@Redirect(method = "checkSupportingBlock", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB checkSupportingBlock(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(name = "boundingBox") AABB boundingBox
	) {
		AABB4 bb = (AABB4) boundingBox;
		return new AABB4(minX, minY, minZ, bb.minW, maxX, maxY, maxZ, bb.maxW);
	}
	@Redirect(method = "checkSupportingBlock", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB checkSupportingBlock(
		AABB testArea, double xa, double ya, double za,
		@Local(name = "movement", argsOnly = true) Vec3 movement
	) {
		return ((AABB4) testArea).move(xa, ya, za, -((Vec4) movement).w);
	}

	@Redirect(method = "move", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
	))
	void moveNoPhysics(Entity This, double x, double y, double z, @Local(argsOnly = true, name = "delta") Vec3 delta) {
		double w = this.getW() + ((Position4) delta).w();
		This.setPos(new Vec4(x, y, z, w));
	}
	@Definition(id = "zCollision", local = @Local(type = boolean.class, name = "zCollision"))
	@Expression("zCollision = ?")
	@Inject(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move_wCollision(
		MoverType moverType, Vec3 delta, CallbackInfo ci,
		@Share("wCollision") LocalBooleanRef wCollision,
		@Local(name = "movement") Vec3 movement
	) {
		wCollision.set(!Mth.equal(((Vec4) delta).w, ((Vec4) movement).w));
	}
	@Definition(id = "abs", method = "Ljava/lang/Math;abs(D)D")
	@Definition(id = "delta", local = @Local(type = Vec3.class, name = "delta", argsOnly = true))
	@Definition(id = "y", field = "Lnet/minecraft/world/phys/Vec3;y:D")
	@Expression("abs(delta.y)")
	@Inject(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move_horizontalCollision(MoverType moverType, Vec3 delta, CallbackInfo ci, @Share("wCollision") LocalBooleanRef wCollision) {
		this.horizontalCollision = this.horizontalCollision || wCollision.get();
	}
	@Redirect(method = "move", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
	))
	void move_setDeltaMovement(
		Entity instance, double xd, double yd, double zd,
		@Share("wCollision") LocalBooleanRef wCollision,
		@Local(name = "vec3") Vec3 vec3
	) {
		double wd = wCollision.get() ? 0 : ((Vec4) vec3).w;
		instance.setDeltaMovement(new Vec4(xd, yd, zd, wd));
	}
	@Redirect(method = "move", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 moveBlockSpeedFactor(Vec3 instance, double xScale, double yScale, double zScale) {
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}

	@Redirect(method = "playEntityOnFireExtinguishedSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void playEntityOnFireExtinguishedSound(Level instance, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
		((Level4) instance).playSound(except, x, y, z, this.getW(), sound, source, volume, pitch);
	}

	@Redirect(method = "getOnPos(F)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getOnPos(int x, int y, int z) {
		return BlockPos4.from(x, y, z, Mth.floor(((Vec4) this.position).w));
	}

	// TODO limitPistonMovement

	@Definition(id = "movement", local = @Local(type = Vec3.class, name = "movement", argsOnly = true))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Definition(id = "movementStep", local = @Local(type = Vec3.class, name = "movementStep"))
	@Expression("movement.z != movementStep.z")
	@ModifyExpressionValue(method = "collide", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean collide_hasHorizontalCollision(
		boolean original,
		@Local(argsOnly = true, name = "movement") Vec3 movement,
		@Local(name = "movementStep") Vec3 movementStep
	) {
		return original || ((Vec4) movement).w != ((Vec4) movementStep).w;
	}
	@Definition(id = "expandTowards", method = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;")
	@Definition(id = "movement", local = @Local(type = Vec3.class, name = "movement", argsOnly = true))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Expression("?.expandTowards(?, ?, movement.z)")
	@Redirect(method = "collide", at = @At("MIXINEXTRAS:EXPRESSION"))
	AABB collide(
		AABB instance, double xa, double ya, double za,
		@Local(argsOnly = true, name = "movement") Vec3 movement
	) {
		return ((AABB4) instance).expandTowards(xa, ya, za, ((Vec4) movement).w);
	}
	@Redirect(method = "collide", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 collide(
		double x, double y, double z,
		@Local(argsOnly = true, name = "movement") Vec3 movement
	) {
		return new Vec4(x, y, z, ((Vec4) movement).w);
	}

	@ModifyExpressionValue(method = "collideWithShapes", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	private static Vec3 collideWithShapes_resolvedMovement(Vec3 original) {
		return Vec4.ZERO;
	}

	@ModifyArg(method = "waterSwimSound", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sqrt(D)D"
	))
	double waterSwimSound(double lengthSquared, @Local(name = "deltaMovement") Vec3 deltaMovement) {
		final double w = ((Vec4) deltaMovement).w;
		return lengthSquared + w * w * 0.2F;
	}

	@Redirect(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void playSound(Level instance, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch) {
		((Level4) instance).playSound(except, x, y, z, this.getW(), sound, source, volume, pitch);
	}

	@Overwrite
	@Deprecated
	public final void doCheckFallDamage(double xa, double ya, double za, boolean onGround) {
		throw Err4.arguments3("Entity4#doCheckFallDamage");
	}
	@Override
	public void doCheckFallDamage(double xa, double ya, double za, double wa, boolean onGround) {
		if (!this.touchingUnloadedChunk()) {
			this.checkSupportingBlock(onGround, new Vec4(xa, ya, za, wa));
			BlockPos pos = this.getOnPosLegacy();
			BlockState state = this.level.getBlockState(pos);
			this.checkFallDamage(ya, onGround, state, pos);
		}
	}

	@Redirect(method = "isInRain", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos isInRain(double x, double y, double z, @Local(name = "pos") BlockPos pos) {
		return BlockPos4.containing(x, y, z, Vec4i.getW(pos));
	}

	@ModifyArg(method = "doWaterSplashEffect", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sqrt(D)D"
	))
	double doWaterSplashEffect_speed(double a, @Local(name = "movement") Vec3 movement) {
		final double w = ((Vec4) movement).w;
		return a + w * w * 0.2F;
	}
	@Redirect(method = "doWaterSplashEffect", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void doWaterSplashEffect(Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, @Local(name = "movement") Vec3 movement) {
		final double wo = (this.random.nextDouble() * 2.0 - 1.0) * this.dimensions.width();
		((LevelAccessor4) instance).addParticle(particle, x, y, z, this.getW() + wo, xd, yd, zd, ((Vec4) movement).w);
	}

	@Redirect(method = "spawnSprintParticle", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void spawnSprintParticle(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(name = "entityPosition") BlockPos entityPosition,
		@Local(name = "pos") BlockPos pos,
		@Local(name = "movement") Vec3 movement
	) {
		double w = this.getW() + (this.random.nextDouble() - 0.5) * this.dimensions.width();
		final int posW = Vec4i.getW(pos);
		if (Vec4i.getW(entityPosition) != posW) {
			w = Mth.clamp(w, posW, posW + 1.0);
		}
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, ((Vec4) movement).w * -4.0);
	}

	@Redirect(method = "getInputVector", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 getInputVector(double x, double y, double z, @Local(name = "movement") Vec3 movement) {
		return new Vec4(x, y, z, ((Vec4) movement).w);
	}

	@Redirect(method = "getLightLevelDependentMagicValue", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;hasChunkAt(II)Z"
	))
	boolean getLightLevelDependentMagicValue_hasChunkAt(Level level, int x, int z) {
		return ((LevelReader4) level).hasChunk(x, z, this.getBlockW());
	}
	@Redirect(method = "getLightLevelDependentMagicValue", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos getLightLevelDependentMagicValue_containing(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.getW());
	}

	@Overwrite
	@Deprecated
	public void absSnapTo(double x, double y, double z, float yRot, float xRot) {
		throw Err4.arguments3("Entity4#absSnapTo");
	}
	@Override
	public void absSnapTo(double x, double y, double z, double w, float yRot, float xRot) {
		this.absSnapTo(x, y, z, w);
		this.absSnapRotationTo(yRot, xRot);
	}

	@Overwrite
	@Deprecated
	public void absSnapTo(double x, double y, double z) {
		throw Err4.arguments3("Entity4#absSnapTo instead.");
	}
	@Override
	public void absSnapTo(double x, double y, double z, double w) {
		double cx = Mth.clamp(x, -3.0E7, 3.0E7);
		double cz = Mth.clamp(z, -3.0E7, 3.0E7);
		double cw = Mth.clamp(w, -3.0E7, 3.0E7);
		this.xo = cx;
		this.yo = y;
		this.zo = cz;
		this.wo = cw;
		this.setPos(new Vec4(cx, y, cz, cw));
	}

	@Redirect(method = "snapTo(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDD)V"
	))
	void snapTo4(Entity entity, double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		((Entity4) entity).snapTo(x, y, z, ((Vec4) pos).w);
	}

	@Overwrite
	@Deprecated
	public void snapTo(double x, double y, double z) {
		throw Err4.arguments3("Entity#snapTo(Vec4)");
	}
	@Override
	public void snapTo(double x, double y, double z, double w) {
		this.snapTo(x, y, z, w, this.getYRot(), this.getXRot());
	}

	@Redirect(method = "snapTo(Lnet/minecraft/world/phys/Vec3;FF)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void snapTo(Entity entity, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "spawnPos") Vec3 spawnPos) {
		((Entity4) entity).snapTo(spawnPos.x, spawnPos.y, spawnPos.z, ((Vec4) spawnPos).w, yRot, xRot);
	}

	@Overwrite
	@Deprecated
	public void snapTo(double x, double y, double z, float yRot, float xRot) {
		throw Err4.arguments3("Entity#snapTo(Vec4, yRot, xRot).");
	}
	@Override
	public void snapTo(double x, double y, double z, double w, float yRot, float xRot) {
		this.setPosRaw(x, y, z, w);
		this.setYRot(yRot);
		this.setXRot(xRot);
		this.setOldPosAndRot();
		this.reapplyPosition();
	}

	@Inject(method = "setOldPos(Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
	void setOldPos(Vec3 position, CallbackInfo ci) {
		this.wo = this.wOld = ((Vec4) position).w;
	}

	@Redirect(method = "oldPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 oldPosition(double x, double y, double z) {
		return new Vec4(x, y, z, this.wOld);
	}

	@ModifyArg(method = "distanceTo", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;sqrt(F)F"
	))
	float distanceTo(float lengthSquared, @Local(argsOnly = true, name = "entity") Entity entity) {
		final float wd = (float) (this.getW() - ((Entity4) entity).getW());
		return lengthSquared + wd * wd;
	}

	@Overwrite
	@Deprecated
	public double distanceToSqr(double x2, double y2, double z2) {
		throw Err4.arguments3("Entity4#distanceToSqr");
	}
	@Override
	public double distanceToSqr(double x2, double y2, double z2, double w2) {
		final double xd = this.getX() - x2;
		final double yd = this.getY() - y2;
		final double zd = this.getZ() - z2;
		final double wd = this.getW() - w2;
		return xd * xd + yd * yd + zd * zd + wd * wd;
	}

	@WrapMethod(method = "distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")
	double distanceToSqr(Vec3 pos, Operation<Double> original) {
		double wd = this.getW() - ((Vec4) pos).w;
		return original.call(pos) + wd * wd;
	}

	@Definition(id = "dd", local = @Local(type = double.class, name = "dd"))
	@Expression("dd = @(?)")
	@ModifyExpressionValue(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	double push_dd(double dd, @Share("wa") LocalDoubleRef wa, @Local(argsOnly = true, name = "entity") Entity entity) {
		wa.set(((Entity4) entity).getW() - this.getW());
		return Math.max(dd, Math.abs(wa.get()));
	}
	@Definition(id = "isVehicle", method = "Lnet/minecraft/world/entity/Entity;isVehicle()Z")
	@Expression("this.isVehicle()")
	@Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	void push_wa(
		Entity entity, CallbackInfo ci,
		@Share("wa") LocalDoubleRef wa,
		@Local(name = "dd") double dd,
		@Local(name = "pow") double pow
	) {
		wa.set(wa.get() / dd * pow * 0.05F);
	}
	@Definition(id = "push", method = "Lnet/minecraft/world/entity/Entity;push(DDD)V")
	@Expression("this.push(?, ?, ?)")
	@Redirect(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	void push_me(Entity instance, double xa, double ya, double za, @Share("wa") LocalDoubleRef wa) {
		((Entity4) instance).push(xa, ya, za, -wa.get());
	}
	@Definition(id = "entity", local = @Local(type = Entity.class, name = "entity", argsOnly = true))
	@Definition(id = "push", method = "Lnet/minecraft/world/entity/Entity;push(DDD)V")
	@Expression("entity.push(?, ?, ?)")
	@Redirect(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	void push_other(Entity instance, double xa, double ya, double za, @Share("wa") LocalDoubleRef wa) {
		((Entity4) instance).push(xa, ya, za, wa.get());
	}

	@Redirect(method = "push(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"
	))
	void push(Entity instance, double xa, double ya, double za, @Local(argsOnly = true, name = "impulse") Vec3 impulse) {
		((Entity4) instance).push(xa, ya, za, ((Vec4) impulse).w);
	}

	@Overwrite
	@Deprecated
	public void push(double xa, double ya, double za) {
		throw Err4.arguments3("Entity4#push");
	}
	@Override
	public void push(double xa, double ya, double za, double wa) {
		if (Double.isFinite(xa) && Double.isFinite(ya) && Double.isFinite(za) && Double.isFinite(wa)) {
			this.setDeltaMovement(((Vec4) this.getDeltaMovement()).add(xa, ya, za, wa));
			this.needsSync = true;
		}
	}

	// TODO? calculateViewVector

	@Redirect(method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getEyePosition(double x, double y, double z) {
		return new Vec4(x, y, z, this.getW());
	}

	@Redirect(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getEyePositionPartial(double x, double y, double z, @Local(argsOnly = true, name = "partialTickTime") float partialTickTime) {
		double w = Mth.lerp(partialTickTime, this.wo, this.getW());
		return new Vec4(x, y, z, w);
	}

	@Redirect(method = "getPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition(double x, double y, double z, @Local(argsOnly = true, name = "partialTickTime") float partialTickTime) {
		double endW = Mth.lerp(partialTickTime, this.wo, this.getW());
		return new Vec4(x, y, z, endW);
	}

	@Redirect(method = "pick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 pickAddViewVector(
		Vec3 from, double x, double y, double z,
		@Local(name = "viewVector") Vec3 viewVector,
		@Local(argsOnly = true, name = "range") double range
	) {
		double w = ((Position4) viewVector).w() * range;
		return ((Vec4) from).add(x, y, z, w);
	}

	@Overwrite
	@Deprecated
	public boolean shouldRender(double camX, double camY, double camZ) {
		throw Err4.arguments3("Entity4#shouldRender");
	}
	@Override
	public boolean shouldRender(double camX, double camY, double camZ, double camW) {
		final double xd = this.getX() - camX;
		final double yd = this.getY() - camY;
		final double zd = this.getZ() - camZ;
		final double wd = this.getW() - camW;
		final double distance = xd * xd + yd * yd + zd * zd + wd * wd;
		return this.shouldRenderAtSqrDistance(distance);
	}

	@Definition(id = "store", method = "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V")
	@Definition(id = "CODEC", field = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("?.store(?, CODEC, ?)")
	@ModifyArg(method = "saveWithoutId", at = @At("MIXINEXTRAS:EXPRESSION"), index = 1)
	Codec<Vec4> storePosAndMotionCodec(Codec<Vec3> codec) {
		return Vec4.CODEC;
	}
	@Redirect(method = "saveWithoutId", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 0
	))
	Vec3 storePosInVehicle(double x, double y, double z) {
		assert this.vehicle != null;
		return new Vec4(x, y, z, ((Entity4) this.vehicle).getW());
	}

	@Definition(id = "input", local = @Local(type = ValueInput.class, name = "input", argsOnly = true))
	@Definition(id = "read", method = "Lnet/minecraft/world/level/storage/ValueInput;read(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Ljava/util/Optional;")
	@Definition(id = "CODEC", field = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("input.read(?, CODEC)")
	@ModifyArg(method = "load", at = @At("MIXINEXTRAS:EXPRESSION"), index = 1)
	Codec<Vec4> load_PosAndMotionCodec(Codec<Vec3> codec){
		return Vec4.CODEC;
	}
	@Definition(id = "orElse", method = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;")
	@Definition(id = "ZERO", field = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;")
	@Expression("?.orElse(ZERO)")
	@ModifyArg(method = "load", at = @At("MIXINEXTRAS:EXPRESSION"), index = 0)
	Object loadPosAndMotionDefault(Object o){
		return Vec4.ZERO;
	}
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPosRaw(DDD)V"
	))
	void loadPosRaw(
		Entity instance, double x, double y, double z,
		@Local(name = "pos") Vec3 pos,
		@Local(name = "maxHorizontalPosition") double maxHorizontalPosition
	) {
		this.setPosRaw(x, y, z, Mth.clamp(((Vec4) pos).w, -maxHorizontalPosition, maxHorizontalPosition));
	}
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
	))
	void loadDeltaMovementRaw(
		Entity instance, double xd, double yd, double zd,
		@Local(name = "motion") Vec3 motion
	) {
		double wd = ((Vec4) motion).w;
		this.setDeltaMovement(new Vec4(xd, yd, zd, Math.abs(wd) > 10.0 ? 0.0 : wd));
	}

	// TODO spawnAtLocation
	// TODO spawnAtLocation

	@Redirect(method = "isInWall", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;ofSize(Lnet/minecraft/world/phys/Vec3;DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB isInWall(Vec3 center, double sizeX, double sizeY, double sizeZ) {
		return AABB4.ofSize((Vec4) center, sizeX, sizeY, sizeZ, sizeX);
	}

	// TODO rideTick
	// TODO positionRider
	// TODO getHandHoldingItemAngle

	@Redirect(method = "handleOnAboveBubbleColumn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
	))
	private static void handleOnAboveBubbleColumn(Entity instance, double xd, double yd, double zd, @Local(name = "movement") Vec3 movement) {
		instance.setDeltaMovement(new Vec4(xd, yd, zd, ((Vec4) movement).w));
	}

	@Redirect(method = "sendBubbleColumnParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
	))
	private static int sendBubbleColumnParticles(
		ServerLevel instance, ParticleOptions particle, double x, double y, double z, int count, double xDist, double yDist, double zDist, double speed,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(name = "random") RandomSource random
	) {
		final double w = Vec4i.getW(pos) + random.nextDouble();
		return ((ServerLevel4) instance).sendParticles(particle, x, y, z, w, count, xDist, yDist, zDist, zDist, speed);
	}

	@Redirect(method = "handleOnInsideBubbleColumn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
	))
	private static void handleOnInsideBubbleColumn(Entity instance, double xd, double yd, double zd, @Local(name = "movement") Vec3 movement) {
		instance.setDeltaMovement(new Vec4(xd, yd, zd, ((Vec4) movement).w));
	}

	@Overwrite
	@Deprecated
	protected void moveTowardsClosestSpace(double x, double y, double z) {
		throw Err4.arguments3("Entity4#moveTowardsClosestSpace");
	}
	@Override
	public void moveTowardsClosestSpace(double x, double y, double z, double w) {
		BlockPos pos = BlockPos4.containing(x, y, z, w);
		Vec4 delta = new Vec4(x - pos.getX(), y - pos.getY(), z - pos.getZ(), w - Vec4i.getW(pos));
		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
		Direction closestDirection = Direction.UP;
		double closest = Double.MAX_VALUE;

		for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction4.KATA, Direction4.ANA, Direction.UP}) {
			neighborPos.setWithOffset(pos, direction);
			//noinspection resource
			if (!this.level().getBlockState(neighborPos).isCollisionShapeFullBlock(this.level(), neighborPos)) {
				double d = delta.get(direction.getAxis());
				double orientedDelta = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d : d;
				if (orientedDelta < closest) {
					closest = orientedDelta;
					closestDirection = direction;
				}
			}
		}

		float speed = this.random.nextFloat() * 0.2F + 0.1F;
		float step = closestDirection.getAxisDirection().getStep();
		Vec4 scaledMovement = (Vec4) this.getDeltaMovement().scale(0.75);
		if (closestDirection.getAxis() == Direction.Axis.X) {
			this.setDeltaMovement(new Vec4(step * speed, scaledMovement.y, scaledMovement.z, scaledMovement.w));
		} else if (closestDirection.getAxis() == Direction.Axis.Y) {
			this.setDeltaMovement(new Vec4(scaledMovement.x, step * speed, scaledMovement.z, scaledMovement.w));
		} else if (closestDirection.getAxis() == Direction.Axis.Z) {
			this.setDeltaMovement(new Vec4(scaledMovement.x, scaledMovement.y, step * speed, scaledMovement.w));
		} else if (closestDirection.getAxis() == Direction4.Axis.W) {
			this.setDeltaMovement(new Vec4(scaledMovement.x, scaledMovement.y, scaledMovement.z, step * speed));
		}
	}

	// TODO toString

	@Redirect(method = "copyPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void copyPosition(Entity instance, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "target") Entity target) {
		((Entity4) instance).snapTo(x, y, z, ((Entity4) target).getW(), yRot, xRot);
	}

	// TODO calculatePassengerTransition

	@Redirect(method = "teleportSetPosition(Lnet/minecraft/world/entity/PositionMoveRotation;Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPosRaw(DDD)V"
	))
	void teleportSetPosition(Entity entity, double x, double y, double z, @Local(name = "absoluteDestination") PositionMoveRotation absoluteDestination) {
		((Entity4) entity).setPosRaw(x, y, z, ((Vec4) absoluteDestination.position()).w);
	}

	// TODO fillCrashReportCategory

	@Overwrite
	@Deprecated
	public boolean teleportTo(ServerLevel level, double x, double y, double z, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera) {
		throw Err4.arguments3("Entity4#teleportTo");
	}
	@Override
	public boolean teleportTo(ServerLevel level, double x, double y, double z, double w, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera) {
		return this.teleport(new TeleportTransition(level, new Vec4(x, y, z, w), Vec4.ZERO, newYRot, newXRot, relatives, TeleportTransition.DO_NOTHING)) != null;
	}

	// TODO dismountTo
	// TODO teleportTo
	// TODO teleportRelative
	// TODO fudgePositionAfterSizeChange
	// TODO? rotate
	// TODO? mirror

	@Redirect(method = "getCollisionHorizontalEscapeVector", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 getCollisionHorizontalEscapeVector(double x, double y, double z) {
		return new Vec4(x, y, z, 0.0); // Ugh
	}

	@Redirect(method = "getDismountLocationForPassenger", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getDismountLocationForPassenger(double x, double y, double z) {
		return new Vec4(x, y, z, this.getW());
	}

	// TODO? lookAt

	@Redirect(method = "touchingUnloadedChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;hasChunksAt(IIII)Z"
	))
	boolean touchingUnloadedChunk(Level level, int x0, int z0, int x1, int z1, @Local(name = "box") AABB box) {
		if (!(box instanceof AABB4 box4)) throw Err4.container3();
		return ((LevelReader4) level).hasChunksAt4(x0, z0, Mth.floor(box4.minW), x1, z1, Mth.ceil(box4.maxW));
	}

	@Inject(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"))
	void setDeltaMovement_force4D(Vec3 deltaMovement, CallbackInfo ci) {
		if (!(deltaMovement instanceof Vec4)) {
			throw Err4.container3();
		}
	}
	@Overwrite
	@Deprecated
	public void setDeltaMovement(double xd, double yd, double zd) {
		throw Err4.arguments3("Entity#setDeltaMovement(Vec4)");
	}

	@Override
	public int getBlockW() {
		return Vec4i.getW(this.blockPosition);
	}

	@Override
	public double getW() {
		return ((Vec4) this.position).w;
	}

	// TODO getRandomW

	@Overwrite
	@Deprecated
	public final void setPosRaw(double x, double y, double z) {
		throw Err4.arguments3("Entity4#setPosRaw");
	}
	@Override
	public void setPosRaw(double x, double y, double z, double w) {
		if (this.position.x != x || this.position.y != y || this.position.z != z || ((Vec4) this.position).w != w) {
			this.position = new Vec4(x, y, z, w);
			int fx = Mth.floor(x);
			int fy = Mth.floor(y);
			int fz = Mth.floor(z);
			int fw = Mth.floor(w);
			if (fx != this.blockPosition.getX() || fy != this.blockPosition.getY() || fz != this.blockPosition.getZ() || fw != Vec4i.getW(this.blockPosition)) {
				this.blockPosition = BlockPos4.from(fx, fy, fz, fw);
				this.inBlockState = null;
				if (SectionPos.blockToSectionCoord(fx) != this.chunkPosition.x() ||
					SectionPos.blockToSectionCoord(fz) != this.chunkPosition.z() ||
					SectionPos.blockToSectionCoord(fw) != ChunkPos4.as(this.chunkPosition).w()
				) {
					this.chunkPosition = ChunkPos.containing(this.blockPosition);
				}
			}

			this.levelCallback.onMove();
			if (!this.firstTick && this.level instanceof ServerLevel serverLevel && !this.isRemoved()) {
				if (this instanceof WaypointTransmitter waypoint && waypoint.isTransmittingWaypoint()) {
					serverLevel.getWaypointManager().updateWaypoint(waypoint);
				}

				if (((Entity) (Object) this) instanceof ServerPlayer player
					&& player.isReceivingWaypoints()
					&& player.connection != null
				) {
					serverLevel.getWaypointManager().updatePlayer(player);
				}
			}
		}
		// Neo: ensure target chunk is loaded.
		if (this.isAddedToLevel() && !this.level.isClientSide() && !this.isRemoved()) {
			((LevelReader4) this.level).getChunk(
				SectionPos.blockToSectionCoord(x),
				SectionPos.blockToSectionCoord(z),
				SectionPos.blockToSectionCoord(w)
			);
		}
	}

	@Redirect(method = "recreateFromPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;syncPacketPositionCodec(DDD)V"
	))
	void recreateFromPacket(Entity instance, double x, double y, double z, @Local(argsOnly = true, name = "packet") ClientboundAddEntityPacket packet) {
		((Entity4) instance).syncPacketPositionCodec(x, y, z, ((ClientboundAddEntityPacket4) packet).getW());
	}
	@Redirect(method = "recreateFromPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void recreateFromPacket(Entity instance, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "packet") ClientboundAddEntityPacket packet) {
		((Entity4) instance).snapTo(x, y, z, ((ClientboundAddEntityPacket4) packet).getW(), yRot, xRot);
	}

	// TODO lerpPositionAndRotationStep
	// TODO MoveFunction

}
