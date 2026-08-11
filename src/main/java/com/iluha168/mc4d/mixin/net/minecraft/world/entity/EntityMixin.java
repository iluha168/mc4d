package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.math.ArrayHelpers;
import com.iluha168.mc4d.network.protocol.game.ClientboundAddEntityPacket4;
import com.iluha168.mc4d.server.level.ServerLevel4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.InterpolationHandler4;
import com.iluha168.mc4d.world.entity.PositionMoveRotation4;
import com.iluha168.mc4d.world.entity.Relative4;
import com.iluha168.mc4d.world.entity.item.ItemEntity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.level.portal.TeleportTransition4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.util.Util;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
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

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Mixin(Entity.class)
public class EntityMixin implements Entity4 {
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
	public @Nullable InterpolationHandler getInterpolation() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow	public double xo, yo, zo;
	@Unique	public double wo;

	@Unique	public float wRot, vRot;

	@Shadow	public float yRotO, xRotO;
	@Unique	public float wRotO, vRotO;

	@Unique
	public double wOld;

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
	public void setOldRot() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public void setOldPosAndRot() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private void setOldPos(Vec3 position) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow public Vec3 position() {
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

	@Shadow
	private void teleportPassengers() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public BlockPos blockPosition() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private double applyPistonMovementRestriction(Direction.Axis axis, double amount) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public @Nullable Entity teleport(TeleportTransition transition) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public @Nullable Entity getVehicle() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public boolean isPassenger() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public boolean onGround() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@SuppressWarnings("unused") // Actually used for overriding
	@Shadow
	public void moveRelative(float speed, Vec3 input) {
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

	@Override
	public float getWRot() {
		return this.wRot;
	}
	@Override
	public void setWRot(float wRot) {
		if (!Float.isFinite(wRot)) {
			Util.logAndPauseIfInIde("Invalid entity rotation: " + wRot + ", discarding.");
		} else {
			this.wRot = Math.clamp(wRot % 360.0F, -90.0F, 90.0F);
		}
	}
	@Override
	public float getWRotO() {
		return this.wRotO;
	}
	@Override
	public void setWRotO(float wRotO) {
		this.wRotO = Math.clamp(wRotO % 360.0F, -90.0F, 90.0F);
	}

	@Override
	public float getVRot() {
		return this.vRot;
	}
	@Override
	public void setVRot(float vRot) {
		if (!Float.isFinite(vRot)) {
			Util.logAndPauseIfInIde("Invalid entity rotation: " + vRot + ", discarding.");
		} else {
			this.vRot = vRot;
		}
	}
	@Override
	public float getVRotO() {
		return this.vRotO;
	}
	@Override
	public void setVRotO(float vRotO) {
		this.vRotO = vRotO;
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB INITIAL_AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new AABB4(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
	void init_setPos(Entity instance, double x, double y, double z){
		instance.setPos(new Vec4(x, y, z, z));
	}
	@Definition(id = "pistonDeltas", field = "Lnet/minecraft/world/entity/Entity;pistonDeltas:[D")
	@Expression("this.pistonDeltas = @(?)")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	double[] init_pistonDeltas(double[] original) {
		return ArrayHelpers.addAll(original, 0.0);
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
	protected void setRot(float yRot, float xRot) {
		throw Err4.rotation("Entity4#setRot");
	}
	@Override
	public void setRot(float yRot, float xRot, float wRot, float vRot) {
		this.setYRot(yRot % 360.0F);
		this.setXRot(xRot % 360.0F);
		this.setWRot(wRot % 360.0F);
		this.setVRot(vRot % 360.0F);
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

	@Overwrite
	@Deprecated
	public void turn(double xo, double yo) {
		throw Err4.rotation("Entity4#turn");
	}
	@Override
	public void turn(double xo, double yo, double wo, double vo) {
		final float vr = this.getVRot() * Mth.DEG_TO_RAD;
		final float vSin = Mth.sin(vr);
		final float vCos = Mth.cos(vr);
		final float xDelta = (float) yo * 0.15F;
		final float yDelta = (float) (vCos * xo - vSin * wo) * 0.15F;
		final float wDelta = (float) (vSin * xo + vCos * wo) * 0.15F;
		final float vDelta = (float) vo * 0.15F;
		this.setXRot(this.getXRot() + xDelta);
		this.setYRot(this.getYRot() + yDelta);
		this.setWRot(Mth.clamp(this.getWRot() + wDelta, -90.0F, 90.0F));
		this.setVRot(this.getVRot() + vDelta);
		this.setXRot(Mth.clamp(this.getXRot(), -90.0F, 90.0F));
		this.xRotO += xDelta;
		this.yRotO += yDelta;
		this.wRotO += wDelta;
		this.vRotO += vDelta;
		this.xRotO = Mth.clamp(this.xRotO, -90.0F, 90.0F);
		this.wRotO = Mth.clamp(this.wRotO, -90.0F, 90.0F);
		if (this.vehicle != null) {
			this.vehicle.onPassengerTurned((Entity) (Object) this);
		}
	}

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
		double w = this.getW() + ((Vec4) delta).w;
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

	@Redirect(method = "limitPistonMovement", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 limitPistonMovement_vec(double x, double y, double z) {
		return new Vec4(x, y, z, 0.0);
	}
	@Definition(id = "ZERO", field = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;")
	@Expression("return @(ZERO)")
	@ModifyExpressionValue(method = "limitPistonMovement", at = @At("MIXINEXTRAS:EXPRESSION"))
	Vec3 limitPistonMovement_return(Vec3 ZERO, @Local(argsOnly = true, name = "vec") Vec3 vec) {
		final double vecW = ((Vec4) vec).w;
		if (vecW != 0.0) {
			final double wa = this.applyPistonMovementRestriction(Direction4.Axis.W, vecW);
			return Math.abs(wa) <= Mth.EPSILON ? ZERO : new Vec4(0.0, 0.0, 0.0, wa);
		}
		return ZERO;
	}

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
	@Redirect(method = "collide", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB collide_groundedAABB(AABB instance, double xa, double ya, double za) {
		return ((AABB4) instance).move(xa, ya, za, za);
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
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;",
		ordinal = 1
	))
	AABB collide_stepUpAABB(AABB instance, double xa, double ya, double za) {
		return ((AABB4) instance).expandTowards(xa, ya, za, za);
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
	@Redirect(method = "collide", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;subtract(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 collide_stepFromGround(Vec3 instance, double x, double y, double z) {
		return ((Vec4) instance).subtract(x, y, z, z);
	}

	@ModifyArg(method = "waterSwimSound", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sqrt(D)D"
	))
	double waterSwimSound(double lengthSquared, @Local(name = "deltaMovement") Vec3 deltaMovement) {
		final double w = ((Vec4) deltaMovement).w;
		return lengthSquared + w * w * 0.2F;
	}

	@Redirect(method = "applyGravity", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 applyGravity(Vec3 instance, double x, double y, double z) {
		return ((Vec4) instance).add(x, y, z, z);
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

	@Redirect(method = "moveRelative", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;getInputVector(Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 moveRelative(Vec3 input, float speed, float yRot) {
		return Entity4.getInputVector((Vec4) input, speed, yRot, this.getWRot(), this.getVRot());
	}

	@Overwrite
	@Deprecated
	protected static Vec3 getInputVector(Vec3 input, float speed, float yRot) {
		throw Err4.rotation("Entity4#getInputVector");
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
	public void absSnapTo(double x, double y, double z, double w, float yRot, float xRot, float wRot, float vRot) {
		this.absSnapTo(x, y, z, w);
		this.absSnapRotationTo(yRot, xRot, wRot, vRot);
	}

	@Overwrite
	@Deprecated
	public void absSnapRotationTo(float yRot, float xRot) {
		throw Err4.rotation("Entity4#absSnapRotationTo");
	}
	@Override
	public void absSnapRotationTo(float yRot, float xRot, float wRot, float vRot) {
		this.setYRot(yRot % 360.0F);
		this.setXRot(Mth.clamp(xRot, -90.0F, 90.0F) % 360.0F);
		this.setWRot(Mth.clamp(wRot, -90.0F, 90.0F) % 360.0F);
		this.setVRot(vRot % 360.0F);
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
		this.wRotO = this.getWRot();
		this.vRotO = this.getVRot();
	}

	@Overwrite
	@Deprecated
	public void absSnapTo(double x, double y, double z) {
		throw Err4.arguments3("Entity4#absSnapTo");
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
		this.snapTo(x, y, z, w, this.getYRot(), this.getXRot(), this.getWRot(), this.getVRot());
	}

	@Overwrite
	@Deprecated
	public void snapTo(BlockPos spawnPos, float yRot, float xRot) {
		throw Err4.rotation("Entity4#snapTo");
	}
	@Override
	public void snapTo(BlockPos spawnPos, float yRot, float xRot, float wRot, float vRot) {
		this.snapTo(spawnPos.getBottomCenter(), yRot, xRot, wRot, vRot);
	}

	@Overwrite
	@Deprecated
	public void snapTo(Vec3 spawnPos, float yRot, float xRot) {
		throw Err4.rotation("Entity4#snapTo");
	}
	@Override
	public void snapTo(Vec3 spawnPos, float yRot, float xRot, float wRot, float vRot) {
		this.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, ((Vec4) spawnPos).w, yRot, xRot, wRot, vRot);
	}

	@Overwrite
	@Deprecated
	public void snapTo(double x, double y, double z, float yRot, float xRot) {
		throw Err4.arguments3("Entity4#snapTo");
	}
	@Override
	public void snapTo(double x, double y, double z, double w, float yRot, float xRot, float wRot, float vRot) {
		this.setPosRaw(x, y, z, w);
		this.setYRot(yRot);
		this.setXRot(xRot);
		this.setWRot(wRot);
		this.setVRot(vRot);
		this.setOldPosAndRot();
		this.reapplyPosition();
	}

	@Overwrite
	@Deprecated
	public final void setOldPosAndRot(Vec3 position, float yRot, float xRot) {
		throw Err4.rotation("Entity4#setOldPosAndRot");
	}
	@Override
	public void setOldPosAndRot(Vec3 position, float yRot, float xRot, float wRot, float vRot) {
		this.setOldPos(position);
		this.setOldRot(yRot, xRot, wRot, vRot);
	}

	@Definition(id = "setOldRot", method = "Lnet/minecraft/world/entity/Entity;setOldRot(FF)V")
	@Expression("this.setOldRot(?, ?)")
	@Redirect(method = "setOldRot()V", at = @At("MIXINEXTRAS:EXPRESSION"))
	void setOldRot(Entity This, float yRot, float xRot) {
		this.setOldRot(yRot, xRot, this.getWRot(), this.getVRot());
	}

	@Inject(method = "setOldPos(Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
	void setOldPos(Vec3 position, CallbackInfo ci) {
		this.wo = this.wOld = ((Vec4) position).w;
	}

	@Overwrite
	@Deprecated
	void setOldRot(float yRot, float xRot) {
		throw Err4.rotation(null);
	}
	@Unique
	private void setOldRot(float yRot, float xRot, float wRot, float vRot) {
		this.yRotO = yRot;
		this.xRotO = xRot;
		this.wRotO = wRot;
		this.vRotO = vRot;
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

	@Redirect(method = "getViewVector", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getViewVector(Entity instance, float xRot, float yRot, @Local(argsOnly = true, name = "a") float a) {
		return this.calculateViewVector(xRot, yRot, this.getViewWRot(a));
	}

	@Override
	public float getViewWRot(float partialTick) {
		return this.getWRot(partialTick);
	}
	@Override
	public float getViewVRot(float partialTick) {
		return this.getVRot(partialTick);
	}
	@Override
	public float getWRot(float partialTick) {
		return partialTick == 1.0F ? this.getWRot() : Mth.lerp(partialTick, this.wRotO, this.getWRot());
	}
	@Override
	public float getVRot(float partialTick) {
		return partialTick == 1.0F ? this.getVRot() : Mth.rotLerp(partialTick, this.vRotO, this.getVRot());
	}

	@Overwrite
	@Deprecated
	public final Vec3 calculateViewVector(float xRot, float yRot) {
		throw Err4.rotation("Entity4#calculateViewVector");
	}
	@Override
	public Vec4 calculateViewVector(float xRot, float yRot, float wRot) {
		return Vec4.directionFromRotation(xRot, yRot, wRot);
	}

	@Redirect(method = "getUpVector", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;calculateUpVector(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getUpVector(Entity instance, float xRot, float yRot, @Local(argsOnly = true, name = "a") float a) {
		return this.calculateUpVector(xRot, yRot, this.getViewWRot(a));
	}

	@Overwrite
	@Deprecated
	protected final Vec3 calculateUpVector(float xRot, float yRot) {
		throw Err4.rotation(null);
	}
	@Unique
	protected Vec4 calculateUpVector(float xRot, float yRot, float wRot) {
		return this.calculateViewVector(xRot - 90.0F, yRot, wRot);
	}

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
		double w = ((Vec4) viewVector).w * range;
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

	@Redirect(method = "saveWithoutId", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;",
		opcode = Opcodes.GETSTATIC
	))
	Codec<Vec4> saveWithoutId_PosAndMotion_CODEC() {
		return Vec4.CODEC;
	}
	@Redirect(method = "saveWithoutId", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 saveWithoutId_PosInVehicle(double x, double y, double z) {
		assert this.vehicle != null;
		return new Vec4(x, y, z, ((Entity4) this.vehicle).getW());
	}
	@Redirect(method = "saveWithoutId", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec2;CODEC:Lcom/mojang/serialization/Codec;",
		opcode = Opcodes.GETSTATIC
	))
	Codec<RotationVec> saveWithoutId_Rotation_CODEC() {
		return RotationVec.CODEC;
	}
	@Redirect(method = "saveWithoutId", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	Vec2 saveWithoutId_Rotation(float x, float y) {
		return new RotationVec(x, y, this.getWRot(), this.getVRot());
	}

	@Redirect(method = "load", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;",
		opcode = Opcodes.GETSTATIC
	))
	Codec<Vec4> load_PosAndMotion_CODEC(){
		return Vec4.CODEC;
	}
	@Redirect(method = "load", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec2;CODEC:Lcom/mojang/serialization/Codec;",
		opcode = Opcodes.GETSTATIC
	))
	Codec<RotationVec> load_Rotation_CODEC() {
		return RotationVec.CODEC;
	}
	@Redirect(method = "load", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec2;ZERO:Lnet/minecraft/world/phys/Vec2;",
		opcode = Opcodes.GETSTATIC
	))
	Vec2 load_Rotation_ZERO() {
		return RotationVec.ZERO;
	}
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
	))
	void load_setDeltaMovement(
		Entity instance, double xd, double yd, double zd,
		@Local(name = "motion") Vec3 motion
	) {
		double wd = ((Vec4) motion).w;
		this.setDeltaMovement(new Vec4(xd, yd, zd, Math.abs(wd) > 10.0 ? 0.0 : wd));
	}
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPosRaw(DDD)V"
	))
	void load_setPosRaw(
		Entity instance, double x, double y, double z,
		@Local(name = "pos") Vec3 pos,
		@Local(name = "maxHorizontalPosition") double maxHorizontalPosition
	) {
		this.setPosRaw(x, y, z, Mth.clamp(((Vec4) pos).w, -maxHorizontalPosition, maxHorizontalPosition));
	}
	@Inject(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setXRot(F)V"
	))
	void load_setWRot_setVRot(ValueInput input, CallbackInfo ci, @Local(name = "rotation") Vec2 rotation) {
		final RotationVec rot4 = (RotationVec) rotation;
		this.setWRot(rot4.w);
		this.setVRot(rot4.v);
	}
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setYHeadRot(F)V"
	))
	void load_setYHeadRot(Entity instance, float yHeadRot) {
		((Entity4) instance).setYHeadRot(yHeadRot, this.getWRot());
	}
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setYBodyRot(F)V"
	))
	void load_setYBodyRot(Entity instance, float yBodyRot) {
		((Entity4) instance).setYBodyRot(yBodyRot, this.getWRot());
	}
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setRot(FF)V"
	))
	void load_setRot(Entity instance, float yRot, float xRot) {
		this.setRot(yRot, xRot, this.getWRot(), this.getVRot());
	}

	@Redirect(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
	))
	ItemEntity spawnAtLocation(Level level, double x, double y, double z, ItemStack itemStack, @Local(argsOnly = true, name = "offset") Vec3 offset) {
		return ItemEntity4.from(level, x, y, z, this.getW() + ((Vec4) offset).w, itemStack);
	}

	@Redirect(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 spawnAtLocation(double x, double y, double z) {
		return new Vec4(x, y, z, z);
	}

	@Redirect(method = "isInWall", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;ofSize(Lnet/minecraft/world/phys/Vec3;DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB isInWall(Vec3 center, double sizeX, double sizeY, double sizeZ) {
		return AABB4.ofSize((Vec4) center, sizeX, sizeY, sizeZ, sizeX);
	}

	// TODO positionRider
	// TODO getVehicleAttachmentPoint
	// TODO getDefaultPassengerAttachmentPoint

	@Overwrite
	@Deprecated
	public final void moveOrInterpolateTo(Vec3 position, float yRot, float xRot) {
		throw Err4.rotation("Entity4#moveOrInterpolateTo");
	}
	@Override
	public final void moveOrInterpolateTo(Vec3 position, float yRot, float xRot, float wRot, float vRot) {
		this.moveOrInterpolateTo(Optional.of(position), Optional.of(yRot), Optional.of(xRot), Optional.of(wRot), Optional.of(vRot));
	}

	@Overwrite
	@Deprecated
	public final void moveOrInterpolateTo(float yRot, float xRot) {
		throw Err4.rotation("Entity4#moveOrInterpolateTo");
	}
	@Override
	public final void moveOrInterpolateTo(float yRot, float xRot, float wRot, float vRot) {
		this.moveOrInterpolateTo(Optional.empty(), Optional.of(yRot), Optional.of(xRot), Optional.of(wRot), Optional.of(vRot));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Redirect(method = "moveOrInterpolateTo(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;moveOrInterpolateTo(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;)V"
	))
	void moveOrInterpolateTo_positionOnly(Entity instance, Optional<Vec3> position, Optional<Float> yRot, Optional<Float> xRot) {
		((Entity4) instance).moveOrInterpolateTo(position, yRot, xRot, xRot, yRot);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Overwrite
	@Deprecated
	public final void moveOrInterpolateTo(Optional<Vec3> position, Optional<Float> yRot, Optional<Float> xRot) {
		throw Err4.rotation("Entity4#moveOrInterpolateTo");
	}
	@Override
	public final void moveOrInterpolateTo(Optional<Vec3> position, Optional<Float> yRot, Optional<Float> xRot, Optional<Float> wRot, Optional<Float> vRot) {
		final InterpolationHandler interpolationHandler = this.getInterpolation();
		if (interpolationHandler != null) {
			final InterpolationHandler4 interpolationHandler4 = (InterpolationHandler4) interpolationHandler;
			interpolationHandler4.interpolateTo(
				position.orElse(interpolationHandler.position()),
				yRot.orElse(interpolationHandler.yRot()),
				xRot.orElse(interpolationHandler.xRot()),
				wRot.orElse(interpolationHandler4.wRot()),
				vRot.orElse(interpolationHandler4.vRot())
			);
		} else {
			position.ifPresent(this::setPos);
			yRot.ifPresent(y -> this.setYRot(y % 360.0F));
			xRot.ifPresent(x -> this.setXRot(x % 360.0F));
			wRot.ifPresent(w -> this.setWRot(w % 360.0F));
			vRot.ifPresent(v -> this.setVRot(v % 360.0F));
		}
	}

	@Overwrite
	@Deprecated
	public void lerpHeadTo(float yRot, int steps) {
		throw Err4.rotation("Entity4#lerpHeadTo");
	}
	@Override
	public void lerpHeadTo(float yHeadRot, float wHeadRot, int steps) {
		this.setYHeadRot(yHeadRot, wHeadRot);
	}

	@Redirect(method = "getLookAngle", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getLookAngle(Entity instance, float xRot, float yRot) {
		return this.calculateViewVector(xRot, yRot, this.getWRot());
	}

	@Redirect(method = "getHeadLookAngle", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getHeadLookAngle(Entity instance, float xRot, float yRot) {
		return this.calculateViewVector(xRot, yRot, this.getWHeadRot());
	}

	@Redirect(method = "getHandHoldingItemAngle", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getHandHoldingItemAngle_calculateViewVector(Entity instance, float xRot, float yRot) {
		final float armOffset = (yRot - this.getYRot()) * Mth.DEG_TO_RAD;
		final RotationVec rotation = new RotationVec(xRot, this.getYRot(), this.getWRot(), this.getVRot());
		return        Vec4.directionFromRotation(rotation).scale(Mth.cos(armOffset))
			.subtract(Vec4.leftFromRotation     (rotation).scale(Mth.sin(armOffset)));
	}

	@Redirect(method = "getRotationVector", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	Vec2 getRotationVector(float x, float y) {
		return new RotationVec(x, y, this.getWRot(), this.getVRot());
	}

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

	@Override
	public float getWHeadRot() {
		return 0.0F;
	}

	@Overwrite
	@Deprecated
	public void setYHeadRot(float yHeadRot) {
		throw Err4.rotation("Entity4#setYHeadRot");
	}
	@Override
	public void setYHeadRot(float yHeadRot, float wHeadRot) {}

	@Overwrite
	@Deprecated
	public void setYBodyRot(float yBodyRot) {
		throw Err4.rotation("Entity4#setYBodyRot");
	}
	@Override
	public void setYBodyRot(float yBodyRot, float wBodyRot) {}

	@WrapOperation(method = "toString", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
	))
	String toString(Locale l, String format, Object[] args, Operation<String> original) {
		return original.call(
			l,
			format.replace("z=%.2f", "z=%.2f, w=%.2f"),
			format.contains("removed=")
				? ArrayUtils.add(args, args.length - 1, this.getW())
				: ArrayUtils.add(args, this.getW())
		);
	}

	@Redirect(method = "copyPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void copyPosition(Entity This, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "target") Entity target) {
		final Entity4 target4 = (Entity4) target;
		((Entity4) This).snapTo(x, y, z, target4.getW(), yRot, xRot, target4.getWRot(), target4.getVRot());
	}

	@Redirect(method = "calculatePassengerTransition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 calculatePassengerTransition_passengerPos(
		Vec3 instance, double x, double y, double z,
		@Local(argsOnly = true, name = "transition") TeleportTransition transition,
		@Local(name = "passengerOffset") Vec3 passengerOffset
	) {
		return ((Vec4) instance).add(
			x, y, z,
			transition.relatives().contains(Relative4.W) ? 0.0 : ((Vec4) passengerOffset).w
		);
	}
	@Redirect(method = "calculatePassengerTransition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/portal/TeleportTransition;withRotation(FF)Lnet/minecraft/world/level/portal/TeleportTransition;"
	))
	TeleportTransition calculatePassengerTransition_withRotation(
		TeleportTransition instance, float yRot, float xRot,
		@Local(argsOnly = true, name = "transition") TeleportTransition transition,
		@Local(argsOnly = true, name = "passenger") Entity passenger
	) {
		final TeleportTransition4 transition4 = TeleportTransition4.as(transition);
		final Entity4 passenger4 = (Entity4) passenger;
		final float passengerWRot = transition4.wRot() + (transition.relatives().contains(Relative4.W_ROT) ? 0.0F : passenger4.getWRot() - this.getWRot());
		final float passengerVRot = transition4.vRot() + (transition.relatives().contains(Relative4.V_ROT) ? 0.0F : passenger4.getVRot() - this.getVRot());
		return TeleportTransition4.as(instance).withRotation(yRot, xRot, passengerWRot, passengerVRot);
	}

	@Redirect(method = "teleportSetPosition(Lnet/minecraft/world/entity/PositionMoveRotation;Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPosRaw(DDD)V"
	))
	void teleportSetPosition_setPosRaw(Entity entity, double x, double y, double z, @Local(name = "absoluteDestination") PositionMoveRotation absoluteDestination) {
		((Entity4) entity).setPosRaw(x, y, z, ((Vec4) absoluteDestination.position()).w);
	}
	@Redirect(method = "teleportSetPosition(Lnet/minecraft/world/entity/PositionMoveRotation;Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setYHeadRot(F)V"
	))
	void teleportSetPosition_setYHeadRot(Entity entity, float yHeadRot, @Local(name = "absoluteDestination") PositionMoveRotation absoluteDestination) {
		final Entity4 entity4 = (Entity4) entity;
		final PositionMoveRotation4 absoluteDestination4 = PositionMoveRotation4.as(absoluteDestination);
		entity4.setYHeadRot(yHeadRot, absoluteDestination4.wRot());
		entity4.setWRot(absoluteDestination4.wRot());
		entity4.setVRot(absoluteDestination4.vRot());
	}

	@Overwrite
	@Deprecated
	public void forceSetRotation(float yRot, boolean relativeY, float xRot, boolean relativeX) {
		throw Err4.rotation("Entity4#forceSetRotation");
	}
	@Override
	public void forceSetRotation(float yRot, boolean relativeY, float xRot, boolean relativeX, float wRot, boolean relativeW, float vRot, boolean relativeV) {
		final Set<Relative> relatives = Relative4.rotation(relativeY, relativeX, relativeW, relativeV);
		final PositionMoveRotation currentValues = PositionMoveRotation.of((Entity) (Object) this);
		final PositionMoveRotation destination = PositionMoveRotation4.as(currentValues).withRotation(yRot, xRot, wRot, vRot);
		final PositionMoveRotation absoluteDestination = PositionMoveRotation.calculateAbsolute(currentValues, destination, relatives);
		final PositionMoveRotation4 absoluteDestination4 = PositionMoveRotation4.as(absoluteDestination);
		this.setYRot(absoluteDestination.yRot());
		this.setWRot(absoluteDestination4.wRot());
		this.setVRot(absoluteDestination4.vRot());
		this.setYHeadRot(absoluteDestination.yRot(), absoluteDestination4.wRot());
		this.setXRot(absoluteDestination.xRot());
		this.setOldRot();
	}

	// TODO fillCrashReportCategory

	@Overwrite
	@Deprecated
	public boolean teleportTo(ServerLevel level, double x, double y, double z, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera) {
		throw Err4.arguments3("Entity4#teleportTo");
	}
	@Override
	public boolean teleportTo(ServerLevel level, double x, double y, double z, double w, Set<Relative> relatives, float newYRot, float newXRot, float newWRot, float newVRot, boolean resetCamera) {
		final TeleportTransition transition = TeleportTransition4.from(level, new Vec4(x, y, z, w), Vec4.ZERO, newYRot, newXRot, newWRot, newVRot, relatives, TeleportTransition.DO_NOTHING);
		return this.teleport(transition) != null;
	}

	@Overwrite
	@Deprecated
	public void dismountTo(double x, double y, double z) {
		throw Err4.arguments3("Entity4#dismountTo");
	}
	@Override
	public void dismountTo(double x, double y, double z, double w) {
		this.teleportTo(x, y, z, w);
	}

	@Overwrite
	@Deprecated
	public void teleportTo(double x, double y, double z) {
		throw Err4.arguments3("Entity4#teleportTo");
	}
	@Override
	public void teleportTo(double x, double y, double z, double w) {
		//noinspection resource
		if (this.level() instanceof ServerLevel) {
			this.snapTo(x, y, z, w, this.getYRot(), this.getXRot(), this.getWRot(), this.getVRot());
			this.teleportPassengers();
		}
	}

	@Overwrite
	@Deprecated
	public void teleportRelative(double dx, double dy, double dz) {
		throw Err4.arguments3("Entity4#teleportRelative");
	}
	@Override
	public void teleportRelative(double dx, double dy, double dz, double dw) {
		this.teleportTo(this.getX() + dx, this.getY() + dy, this.getZ() + dz, this.getW() + dw);
	}

	@Redirect(method = "fudgePositionAfterSizeChange", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 fudgePositionAfterSizeChange(Vec3 instance, double x, double y, double z) {
		assert x == z;
		return ((Vec4) instance).add(x, y, z, z);
	}

	@Redirect(method = "getDirection", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;fromYRot(D)Lnet/minecraft/core/Direction;"
	))
	Direction getDirection(double yRot) {
		return Direction4.fromYRotWRot(this.getYRot(), this.getWRot());
	}

	// TODO rotate
	// TODO mirror

	@Overwrite
	@Deprecated
	protected static Vec3 getCollisionHorizontalEscapeVector(double colliderWidth, double collidingWidth, float directionDegrees) {
		throw Err4.rotation("Entity4#getCollisionHorizontalEscapeVector");
	}

	@Redirect(method = "getDismountLocationForPassenger", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getDismountLocationForPassenger(double x, double y, double z) {
		return new Vec4(x, y, z, this.getW());
	}

	@Overwrite // I am lazy, can be made with proper injections later
	public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 pos) {
		final Vec4 from = (Vec4) anchor.apply((Entity) (Object) this);
		final RotationVec rot = from.vectorTo(pos).rotation();
		this.setXRot(Mth.wrapDegrees(rot.x));
		this.setYRot(Mth.wrapDegrees(rot.y));
		this.setWRot(Mth.wrapDegrees(rot.w));
		this.setYHeadRot(this.getYRot(), this.getWRot());
		this.xRotO = this.getXRot();
		this.yRotO = this.getYRot();
		this.wRotO = this.getWRot();
	}

	// TODO getPreciseBodyRotation

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
	@Override
	public double getW(double progress) {
		return ((Vec4) this.position).w + this.getBbWidth() * progress;
	}
	@Override
	public double getRandomW(double spread) {
		return this.getW((2.0 * this.random.nextDouble() - 1.0) * spread);
	}

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

	@Redirect(method = "getRopeHoldPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getRopeHoldPosition(Vec3 instance, double x, double y, double z) {
		return ((Vec4) instance).add(x, y, z, z);
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
		final ClientboundAddEntityPacket4 packet4 = (ClientboundAddEntityPacket4) packet;
		((Entity4) instance).snapTo(x, y, z, packet4.getW(), yRot, xRot, packet4.getWRot(), packet4.getVRot());
	}

	// TODO getVisualRotationYInDegrees (compass)

	@Overwrite
	@Deprecated
	protected void lerpPositionAndRotationStep(int stepsToTarget, double targetX, double targetY, double targetZ, double targetYRot, double targetXRot) {
		throw Err4.arguments3("Entity4#lerpPositionAndRotationStep");
	}
	@Override
	public void lerpPositionAndRotationStep(int stepsToTarget, double targetX, double targetY, double targetZ, double targetW, double targetYRot, double targetXRot, double targetWRot, double targetVRot) {
		final double alpha = 1.0 / stepsToTarget;
		final double x = Mth.lerp(alpha, this.getX(), targetX);
		final double y = Mth.lerp(alpha, this.getY(), targetY);
		final double z = Mth.lerp(alpha, this.getZ(), targetZ);
		final double w = Mth.lerp(alpha, this.getW(), targetW);
		float yRot = (float)Mth.rotLerp(alpha, this.getYRot(), targetYRot);
		float xRot = (float)Mth.lerp(alpha, this.getXRot(), targetXRot);
		float wRot = (float)Mth.lerp(alpha, this.getWRot(), targetWRot);
		float vRot = (float)Mth.rotLerp(alpha, this.getVRot(), targetVRot);
		this.setPos(new Vec4(x, y, z, w));
		this.setRot(yRot, xRot, wRot, vRot);
	}

	// TODO MoveFunction
}
