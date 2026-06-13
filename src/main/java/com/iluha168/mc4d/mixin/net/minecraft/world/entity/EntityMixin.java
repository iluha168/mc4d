package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
	public abstract boolean isRemoved();

	@Shadow
	public abstract boolean isAddedToLevel();

	@Shadow
	public abstract void setBoundingBox(AABB bb);

	@Shadow
	protected abstract AABB makeBoundingBox();

	@Shadow
	private @Nullable Entity vehicle;

	@Shadow
	protected abstract void reapplyPosition();

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
	public abstract void setPos(Vec3 pos);

	@Shadow
	public abstract void absSnapRotationTo(float yRot, float xRot);

	@Shadow
	public abstract void setDeltaMovement(Vec3 deltaMovement);

	@Shadow
	public abstract boolean touchingUnloadedChunk();

	@Shadow
	protected abstract void checkSupportingBlock(boolean onGround, @Nullable Vec3 movement);

	@Shadow
	public abstract BlockPos getOnPosLegacy();

	@Shadow
	protected abstract void checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos);

	@Shadow
	public boolean horizontalCollision;

	@Shadow
	public abstract @Nullable Entity teleport(TeleportTransition transition);

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
	void setInitialPosWithVector(Entity instance, double x, double y, double z){
		this.position = Vec4.ZERO;
		instance.setPos(new Vec4(x, y, z, 0.0));
	}

	@Override
	public double getW() {
		return ((Vec4) this.position).w;
	}

	@Overwrite
	public final void setPosRaw(double x, double y, double z) {
		throw Err4.arguments3("Entity4#setPosRaw");
	}

	@Override
	public void setPosRaw(Vec4 newPos) {
		if (this.position.x != newPos.x || this.position.y != newPos.y || this.position.z != newPos.z || ((Vec4) this.position).w != newPos.w) {
			this.position = newPos;
			int fx = Mth.floor(newPos.x);
			int fy = Mth.floor(newPos.y);
			int fz = Mth.floor(newPos.z);
			int fw = Mth.floor(newPos.w);
			if (fx != this.blockPosition.getX() || fy != this.blockPosition.getY() || fz != this.blockPosition.getZ() || fw != Vec4i.getW(this.blockPosition)) {
				this.blockPosition = BlockPos4.from(fx, fy, fz, fw);
				this.inBlockState = null;
				if (SectionPos.blockToSectionCoord(fx) != this.chunkPosition.x()
					|| SectionPos.blockToSectionCoord(fz) != this.chunkPosition.z()
					// TODO || SectionPos.blockToSectionCoord(fw) != this.chunkPosition.w()
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
		// Ensures target chunk is loaded.
		if (this.isAddedToLevel() && !this.level.isClientSide() && !this.isRemoved()) {
			this.level.getChunk((int) Math.floor(newPos.x) >> 4, (int) Math.floor(newPos.z) >> 4); // TODO (int) Math.floor(w) >> 4
		}
	}

	@Overwrite
	public void setPos(double x, double y, double z) {
		throw Err4.arguments3("Entity#setPos(Vec4)");
	}

	@Redirect(method = "setPos(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
	))
	void setPos4(Entity instance, double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		this.setPosRaw((Vec4) pos);
		this.setBoundingBox(this.makeBoundingBox());
	}

	@Overwrite
	public void snapTo(double x, double y, double z, float yRot, float xRot) {
		throw Err4.arguments3("Entity#snapTo(Vec4, yRot, xRot).");
	}

	@Redirect(method = "snapTo(Lnet/minecraft/world/phys/Vec3;FF)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void snapTo4(Entity entity, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "spawnPos") Vec3 spawnPos) {
		assert this == (Object) entity;
		this.setPosRaw(Vec4.of(spawnPos, 0)); // TODO replace with `(Vec4) spawnPos` once 4D world is implemented
		entity.setYRot(yRot);
		entity.setXRot(xRot);
		entity.setOldPosAndRot();
		this.reapplyPosition();
	}

	@Overwrite
	public void snapTo(double x, double y, double z) {
		throw Err4.arguments3("Entity#snapTo(Vec4)");
	}

	@Redirect(method = "snapTo(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDD)V"
	))
	void snapTo4(Entity entity, double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		entity.snapTo(pos, entity.getYRot(), entity.getXRot());
	}

	@Overwrite
	public void absSnapTo(double x, double y, double z) {
		throw Err4.arguments3("Entity4#absSnapTo instead.");
	}

	@Override
	public void absSnapTo(Vec4 newPos) {
		double cx = Mth.clamp(newPos.x, -3.0E7, 3.0E7);
		double cz = Mth.clamp(newPos.z, -3.0E7, 3.0E7);
		double cw = Mth.clamp(newPos.w, -3.0E7, 3.0E7);
		this.xo = cx;
		this.yo = newPos.y;
		this.zo = cz;
		this.wo = cw;
		this.setPos(new Vec4(cx, newPos.y, cz, cw));
	}

	@Overwrite
	public void absSnapTo(double x, double y, double z, float yRot, float xRot) {
		throw Err4.arguments3("Entity4#absSnapTo");
	}

	@Override
	public void absSnapTo(Vec4 newPos, float yRot, float xRot) {
		this.absSnapTo(newPos);
		this.absSnapRotationTo(yRot, xRot);
	}

	// NBT
	@Definition(id = "input", local = @Local(type = ValueInput.class, name = "input", argsOnly = true))
	@Definition(id = "read", method = "Lnet/minecraft/world/level/storage/ValueInput;read(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Ljava/util/Optional;")
	@Definition(id = "CODEC", field = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;")
	@Expression("input.read(?, CODEC)")
	@ModifyArg(method = "load", at = @At("MIXINEXTRAS:EXPRESSION"), index = 1)
	Codec<Vec4> loadPosAndMotionCodec(Codec<Vec3> codec){
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
		this.setPosRaw(new Vec4(
			x, y, z,
			Mth.clamp(((Vec4) pos).w, -maxHorizontalPosition, maxHorizontalPosition)
		));
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
		this.setDeltaMovement(new Vec4(
			xd, yd, zd, Math.abs(wd) > 10.0 ? 0.0 : wd
		));
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

	// Random fixes
	@Redirect(method = "reapplyPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
	))
	void reapplyPosition4(Entity instance, double x, double y, double z){
		instance.setPos(this.position);
	}

	@Redirect(method = "teleportSetPosition(Lnet/minecraft/world/entity/PositionMoveRotation;Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPosRaw(DDD)V"
	))
	void teleportSetPosition(Entity entity, double x, double y, double z, @Local(name = "absoluteDestination") PositionMoveRotation absoluteDestination) {
		((Entity4) entity).setPosRaw(new Vec4(
			x, y, z,
			((Vec4) absoluteDestination.position()).w
		));
	}

	@Redirect(method = "isInWall", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;ofSize(Lnet/minecraft/world/phys/Vec3;DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB isInWall(Vec3 center, double sizeX, double sizeY, double sizeZ) {
		return AABB4.ofSize((Vec4) center, sizeX, sizeY, sizeZ, sizeX);
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

	@Inject(method = "setOldPos(Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
	void setOldPos(Vec3 position, CallbackInfo ci) {
		this.wo = this.wOld = ((Vec4) position).w;
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

	// if (this.noPhysics) {
	//     this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);
	@Redirect(method = "move", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
	))
	void moveNoPhysics(Entity This, double x, double y, double z, @Local(argsOnly = true, name = "delta") Vec3 delta) {
		double w = this.getW() + ((Position4) delta).w();
		This.setPos(new Vec4(x, y, z, w));
	}
	//     this.horizontalCollision = false;
	//     this.verticalCollision = false;
	//     this.verticalCollisionBelow = false;
	//     this.minorHorizontalCollision = false;
	// } else {
	//     if (moverType == MoverType.PISTON) {
	//         delta = this.limitPistonMovement(delta);
	//         if (delta.equals(Vec3.ZERO)) {
	//             return;
	//         }
	//     }
	//     ProfilerFiller profiler = Profiler.get();
	//     profiler.push("move");
	//     if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
	//         if (moverType != MoverType.PISTON) {
	//             delta = delta.multiply(this.stuckSpeedMultiplier);
	//         }
	//         this.stuckSpeedMultiplier = Vec3.ZERO;
	//         this.setDeltaMovement(Vec3.ZERO);
	//     }
	//     delta = this.maybeBackOffFromEdge(delta, moverType);
	//     Vec3 movement = this.collide(delta);
	//     double movementLength = movement.lengthSqr();
	//     if (movementLength > 1.0E-7 || delta.lengthSqr() - movementLength < 1.0E-7) {
	//         if (this.fallDistance != 0.0 && movementLength >= 1.0) {
	//             double checkDistance = Math.min(movement.length(), 8.0);
	//             Vec3 checkTo = this.position().add(movement.normalize().scale(checkDistance));
	//             BlockHitResult hitResult = this.level()
	//                 .clip(new ClipContext(this.position(), checkTo, ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, this));
	//             if (hitResult.getType() != HitResult.Type.MISS) {
	//                 this.resetFallDistance();
	//             }
	//         }
	//         Vec3 pos = this.position();
	//         Vec3 newPosition = pos.add(movement);
	//         this.addMovementThisTick(new Entity.Movement(pos, newPosition, delta));
	//         this.setPos(newPosition);
	//     }
	//     profiler.pop();
	//     profiler.push("rest");
	//     boolean xCollision = !Mth.equal(delta.x, movement.x);
	//     boolean zCollision = !Mth.equal(delta.z, movement.z);
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
	//     this.horizontalCollision = xCollision || zCollision;
	@Definition(id = "abs", method = "Ljava/lang/Math;abs(D)D")
	@Definition(id = "delta", local = @Local(type = Vec3.class, name = "delta", argsOnly = true))
	@Definition(id = "y", field = "Lnet/minecraft/world/phys/Vec3;y:D")
	@Expression("abs(delta.y)")
	@Inject(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move_horizontalCollision(MoverType moverType, Vec3 delta, CallbackInfo ci, @Share("wCollision") LocalBooleanRef wCollision) {
		this.horizontalCollision = this.horizontalCollision || wCollision.get();
	}
	//     if (Math.abs(delta.y) > 0.0 || this.isLocalInstanceAuthoritative()) {
	//         this.verticalCollision = delta.y != movement.y;
	//         this.verticalCollisionBelow = this.verticalCollision && delta.y < 0.0;
	//         this.setOnGroundWithMovement(this.verticalCollisionBelow, this.horizontalCollision, movement);
	//     }
	//     if (this.horizontalCollision) {
	//         this.minorHorizontalCollision = this.isHorizontalCollisionMinor(movement);
	//     } else {
	//         this.minorHorizontalCollision = false;
	//     }
	//     BlockPos effectPos = this.getOnPosLegacy();
	//     BlockState effectState = this.level().getBlockState(effectPos);
	//     if (this.isLocalInstanceAuthoritative()) {
	//         this.checkFallDamage(movement.y, this.onGround(), effectState, effectPos);
	//     }
	//     if (this.isRemoved()) {
	//         profiler.pop();
	//     } else {
	//         if (this.horizontalCollision) {
	//             Vec3 vec3 = this.getDeltaMovement();
	//             this.setDeltaMovement(xCollision ? 0.0 : vec3.x, vec3.y, zCollision ? 0.0 : vec3.z);
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
	//         }
	//         if (this.canSimulateMovement()) {
	//             Block onBlock = effectState.getBlock();
	//             if (delta.y != movement.y) {
	//                 onBlock.updateEntityMovementAfterFallOn(this.level(), this);
	//             }
	//         }
	//         if (!this.level().isClientSide() || this.isLocalInstanceAuthoritative()) {
	//             Entity.MovementEmission emission = this.getMovementEmission();
	//             if (emission.emitsAnything() && !this.isPassenger()) {
	//                 this.applyMovementEmissionAndPlaySound(emission, movement, effectPos, effectState);
	//             }
	//         }
	//         float blockSpeedFactor = this.getBlockSpeedFactor();
	//         this.setDeltaMovement(this.getDeltaMovement().multiply(blockSpeedFactor, 1.0, blockSpeedFactor));
	@Redirect(method = "move", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 moveBlockSpeedFactor(Vec3 instance, double xScale, double yScale, double zScale) {
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}
	//         profiler.pop();
	//     }
	// }

	@Overwrite
	public void setDeltaMovement(double xd, double yd, double zd) {
		throw Err4.arguments3("Entity#setDeltaMovement(Vec4)");
	}
	@Inject(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"))
	void setDeltaMovement(Vec3 deltaMovement, CallbackInfo ci) {
		if (!(deltaMovement instanceof Vec4)) {
			throw Err4.container3();
		}
	}

	@Definition(id = "deltaMovement", field = "Lnet/minecraft/world/entity/Entity;deltaMovement:Lnet/minecraft/world/phys/Vec3;")
	@Definition(id = "ZERO", field = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;")
	@Expression("this.deltaMovement = @(ZERO)")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Vec3 deltaMovement(Vec3 original) {
		return Vec4.ZERO;
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

	@ModifyExpressionValue(method = "collideWithShapes", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	private static Vec3 collideWithShapes_resolvedMovement(Vec3 original) {
		return Vec4.ZERO;
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

	@Overwrite
	public final void doCheckFallDamage(double xa, double ya, double za, boolean onGround) {
		throw Err4.arguments3("Entity4#doCheckFallDamage");
	}
	@Override
	public void doCheckFallDamage(Vec4 movement, boolean onGround) {
		if (!this.touchingUnloadedChunk()) {
			this.checkSupportingBlock(onGround, movement);
			BlockPos pos = this.getOnPosLegacy();
			BlockState state = this.level.getBlockState(pos);
			this.checkFallDamage(movement.y, onGround, state, pos);
		}
	}

	@Overwrite
	public boolean teleportTo(ServerLevel level, double x, double y, double z, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera) {
		throw Err4.arguments3("Entity4#teleportTo");
	}
	@Override
	public boolean teleportTo(ServerLevel level, Vec4 newPos, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera) {
		return this.teleport(new TeleportTransition(level, newPos, Vec4.ZERO, newYRot, newXRot, relatives, TeleportTransition.DO_NOTHING)) != null;
	}

	@Redirect(method = "oldPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 oldPosition(double x, double y, double z) {
		return new Vec4(x, y, z, this.wOld);
	}
}
