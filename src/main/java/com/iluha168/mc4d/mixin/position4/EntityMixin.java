package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

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

	@Shadow
	public abstract void setPos(Vec3 pos);

	@Shadow
	public abstract void absSnapRotationTo(float yRot, float xRot);

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
	void setInitialPosWithVector(Entity instance, double x, double y, double z){
		this.position = Vec4.ZERO;
		instance.setPos(new Vec4(x, y, z, 0.0));
	}

	@Override
	public double getW() {
		return ((Vec4) this.position).w;
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	public final void setPosRaw(double x, double y, double z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use Entity4.setPosRaw instead."));
	}

	@Override
	public void setPosRaw(Vec4 newPos) {
		if (this.position.x != newPos.x || this.position.y != newPos.y || this.position.z != newPos.z || ((Vec4) this.position).w != newPos.w) {
			this.position = newPos;
			int fx = Mth.floor(newPos.x);
			int fy = Mth.floor(newPos.y);
			int fz = Mth.floor(newPos.z);
			if (fx != this.blockPosition.getX() || fy != this.blockPosition.getY() || fz != this.blockPosition.getZ()) { // TODO || fw != this.blockPosition.getW()
				this.blockPosition = new BlockPos(fx, fy, fz); // TODO new BlockPos(fx, fy, fz, fw);
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

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, making call sites use setPos(Vec3).
	 */
	@Overwrite
	public void setPos(double x, double y, double z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use setPos(Vec3)."));
	}

	@Redirect(method = "setPos(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
	))
	void setPos4(Entity instance, double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		this.setPosRaw((Vec4) pos);
		this.setBoundingBox(this.makeBoundingBox());
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, making call sites use snapTo(Vec3, yRot, xRot).
	 */
	@Overwrite
	public void snapTo(double x, double y, double z, float yRot, float xRot) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use snapTo(Vec3, yRot, xRot)."));
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

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, making call sites use snapTo(Vec3).
	 */
	@Overwrite
	public void snapTo(double x, double y, double z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use snapTo(Vec3)."));
	}

	@Redirect(method = "snapTo(Lnet/minecraft/world/phys/Vec3;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDD)V"
	))
	void snapTo4(Entity entity, double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		entity.snapTo(pos, entity.getYRot(), entity.getXRot());
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, making call sites use absSnapTo(Vec3).
	 */
	@Overwrite
	public void absSnapTo(double x, double y, double z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use Entity4.absSnapTo instead."));
	}

	@Override
	public void absSnapTo(Vec4 newPos) {
		double cx = Mth.clamp(newPos.x, -3.0E7, 3.0E7);
		double cz = Mth.clamp(newPos.z, -3.0E7, 3.0E7);
		double cw = Mth.clamp(newPos.w, -3.0E7, 3.0E7);
		this.xo = cx;
		this.yo = newPos.y;
		this.zo = cz;
		this.setPos(new Vec4(cx, newPos.y, cz, cw));
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, making call sites use absSnapTo(Vec3, yRot, xRot).
	 */
	@Overwrite
	public void absSnapTo(double x, double y, double z, float yRot, float xRot) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use Entity4.absSnapTo instead."));
	}

	@Override
	public void absSnapTo(Vec4 newPos, float yRot, float xRot) {
		this.absSnapTo(newPos);
		this.absSnapRotationTo(yRot, xRot);
	}

	// NBT
	@Redirect(method = "load", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/ValueInput;read(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Ljava/util/Optional;",
		ordinal = 0
	))
	Optional<Vec4> loadPos(ValueInput input, String Pos, Codec<Vec3> tCodec){
		assert Pos.equals("Pos");
		final Optional<Vec4> pos = input.read(Pos, Vec4.CODEC);
		if (pos.isPresent()) return pos;
		return Optional.of(Vec4.ZERO);
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

	@Redirect(method = "saveWithoutId", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V",
		ordinal = 0
	))
	<T> void storePosInVehicle(ValueOutput output, String Pos, Codec<T> tCodec, T posInVehicle) {
		assert Pos.equals("Pos");
		assert this.vehicle != null;
		output.store(Pos, Vec4.CODEC, Vec4.of(
			(Vec3) posInVehicle,
			((Entity4) this.vehicle).getW()
		));
	}

	@Redirect(method = "saveWithoutId", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V",
		ordinal = 1
	))
	<T> void storePos(ValueOutput output, String Pos, Codec<T> tCodec, T entityPos) {
		assert Pos.equals("Pos");
		output.store(Pos, Vec4.CODEC, (Vec4) entityPos);
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
}
