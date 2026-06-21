package com.iluha168.mc4d.mixin.net.minecraft.client.player;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.mixin.net.minecraft.world.entity.player.PlayerMixin;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.player.Input4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.HorizontalVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.stream.StreamSupport;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin extends PlayerMixin {
	@Shadow
	protected abstract boolean suffocatesAt(BlockPos pos);

	@Unique
	private double wLast;

	@Shadow
	public ClientInput input;

	@Shadow
	private boolean handsBusy;

	@Shadow
	protected abstract boolean canAutoJump();

	@Shadow
	private int autoJumpTime;

	@Redirect(method = "sendPosition", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;lengthSquared(DDD)D"
	))
	double sendPosition(double x, double y, double z) {
		return MathHelpers.lengthSquared(x, y, z, this.getW() - this.wLast);
	}
	@Definition(id = "zLast", field = "Lnet/minecraft/client/player/LocalPlayer;zLast:D")
	@Expression("this.zLast = ?")
	@Inject(method = "sendPosition", at = @At("MIXINEXTRAS:EXPRESSION"))
	void sendPosition(CallbackInfo ci) {
		this.wLast = this.getW();
	}

	@Overwrite
	@Deprecated
	private void moveTowardsClosestSpace(double x, double z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void moveTowardsClosestSpace(LocalPlayer This, double x, double z, double w) {
		BlockPos pos = BlockPos4.containing(x, This.getY(), z, w);
		if (this.suffocatesAt(pos)) {
			double xd = x - pos.getX();
			double zd = z - pos.getZ();
			double wd = w - Vec4i.getW(pos);

			Direction dir = null;
			double closest = Double.MAX_VALUE;

			for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
				double axisDistance = Direction4.Axis.as(direction.getAxis()).choose(xd, 0.0, zd, wd);
				double distanceToEdge = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - axisDistance : axisDistance;
				if (distanceToEdge < closest && !this.suffocatesAt(pos.relative(direction))) {
					closest = distanceToEdge;
					dir = direction;
				}
			}

			if (dir != null) {
				Vec3 oldMovement = This.getDeltaMovement();
				double oldMovementW = ((Position4) oldMovement).w();
				if (dir.getAxis() == Direction.Axis.X) {
					This.setDeltaMovement(new Vec4(0.1 * dir.getStepX(), oldMovement.y, oldMovement.z, oldMovementW));
				} else if (dir.getAxis() == Direction.Axis.Z) {
					This.setDeltaMovement(new Vec4(oldMovement.x, oldMovement.y, 0.1 * dir.getStepZ(), oldMovementW));
				} else {
					This.setDeltaMovement(new Vec4(oldMovement.x, oldMovement.y, oldMovement.z, 0.1 * Direction4.as(dir).getStepW()));
				}
			}
		}
	}

	@Redirect(method = "suffocatesAt", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB suffocatesAt(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		double minW = Vec4i.getW(pos);
		return new AABB4(
			minX, minY, minZ, minW,
			maxX, maxY, maxZ, minW + 1
		);
	}

	@Redirect(method = "playSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void playLocalSound(Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
		// TODO obviously remove this in favor of 4D sound engine
	}

	@Definition(id = "zza", field = "Lnet/minecraft/client/player/LocalPlayer;zza:F")
	@Expression("this.zza = @(?)")
	@Inject(method = "applyInput", at = @At("MIXINEXTRAS:EXPRESSION"))
	void applyInput(CallbackInfo ci, @Local(name = "modifiedInput") Vec2 modifiedInput) {
		this.wwa = ((HorizontalVec) modifiedInput).z;
	}

	@Expression("return @(?)")
	@ModifyExpressionValue(method = "distanceToUnitSquare", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static float distanceToUnitSquare(float original, @Local(argsOnly = true, name = "direction") Vec2 direction) {
		if (!(direction instanceof HorizontalVec direction3)) throw Err4.container3();
		float directionX = Math.abs(direction3.x);
		float directionY = Math.abs(direction3.y);
		float directionZ = Math.abs(direction3.z);
		float max = Math.max(directionX, Math.max(directionY, directionZ));
		float tanX = directionX / max;
		float tanY = directionY / max;
		float tanZ = directionZ / max;
		//noinspection SuspiciousNameCombination
		return Mth.sqrt(Mth.square(tanX) + Mth.square(tanY) + Mth.square(tanZ));
	}

	@Redirect(method = "resetPos", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;setPos(DDD)V"
	))
	void resetPos(LocalPlayer player, double x, double y, double z) {
		player.setPos(new Vec4(x, y, z, this.getW()));
	}

	@Redirect(method = "resetPos", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
	))
	void resetPos_resetDeltaMovement(LocalPlayer This, Vec3 vec3) {
		This.setDeltaMovement(Vec4.ZERO);
	}

	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/player/LocalPlayer;moveTowardsClosestSpace(DD)V"
	))
	void aiStep_moveTowardsClosestSpace4(LocalPlayer This, double x, double z) {
		final double w = this.getW();
		final double wPlus  = w + This.getBbWidth() * 0.35;
		final double wMinus = w - This.getBbWidth() * 0.35;
		this.moveTowardsClosestSpace(This, x, z, wPlus);
		this.moveTowardsClosestSpace(This, x, z, wMinus);
	}

	@ModifyExpressionValue(method = "rideTick", at = @At(
		value = "NEW",
		target = "(ZZZZZZZ)Lnet/minecraft/world/entity/player/Input;"
	))
	Input rideTick_copy(Input copy, @Local(name = "input") Input input) {
		Input4 copy4 = Input4.as(copy);
		Input4 input4 = Input4.as(input);
		copy4.setAna(input4.ana());
		copy4.setKata(input4.kata());
		return copy;
	}
	@Inject(method = "rideTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;setInput(ZZZZ)V"
	))
	void rideTick_handsBusy(CallbackInfo ci) {
		Input4 keyPresses4 = Input4.as(this.input.keyPresses);
		this.handsBusy = this.handsBusy || keyPresses4.ana() || keyPresses4.kata();
	}

	@Definition(id = "prevZ", local = @Local(type = double.class, name = "prevZ"))
	@Expression("prevZ = @(?)")
	@Inject(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move_prevW(MoverType moverType, Vec3 delta, CallbackInfo ci, @Share("prevW") LocalDoubleRef prevW) {
		prevW.set(this.getW());
	}
	@Definition(id = "updateAutoJump", method = "Lnet/minecraft/client/player/LocalPlayer;updateAutoJump(FF)V")
	@Expression("this.updateAutoJump(?, ?)")
	@Redirect(method = "move", at = @At("MIXINEXTRAS:EXPRESSION"))
	void move_updateAutoJump(LocalPlayer This, float xa, float za, @Share("prevW") LocalDoubleRef prevW, @Share("deltaW") LocalFloatRef deltaW) {
		deltaW.set((float)(this.getW() - prevW.get()));
		this.updateAutoJump(xa, za, deltaW.get());
	}
	@Redirect(method = "move", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;length(FF)F"
	))
	float move_walkedDistance(float x, float y, @Share("deltaW") LocalFloatRef deltaW) {
		return (float) Math.sqrt(Mth.lengthSquared(x, y, deltaW.get()));
	}

	@Overwrite
	@Deprecated
	protected void updateAutoJump(float xa, float za) {
		throw Err4.arguments2(null);
	}
	@SuppressWarnings("resource")
	@Unique // I am so sad that I had to rewrite this
	protected void updateAutoJump(float xa, float za, float wa) {
		if (!this.canAutoJump()) {
			return;
		}
		Vec4 moveBegin = (Vec4) this.position();
		Vec4 moveEnd = moveBegin.add(xa, 0.0, za, wa);
		Vec4 moveDiff = new Vec4(xa, 0.0, za, wa);
		float currentSpeed = this.getSpeed();
		float moveDistSq = (float) moveDiff.lengthSqr();
		if (moveDistSq <= 0.001F) {
			HorizontalVec move = (HorizontalVec) this.input.getMoveVector();
			float inputXa = currentSpeed * move.x;
			float inputZa = currentSpeed * move.y;
			float inputWa = currentSpeed * move.z;
			float sin = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
			float cos = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
			moveDiff = new Vec4(
				inputXa * cos - inputZa * sin,
				moveDiff.y,
				inputZa * cos + inputXa * sin,
				inputWa
			);
			moveDistSq = (float) moveDiff.lengthSqr();
			if (moveDistSq <= 0.001F) {
				return;
			}
		}

		float moveDistInverted = Mth.invSqrt(moveDistSq);
		Vec4 moveDir = moveDiff.scale(moveDistInverted);
		Vec4 facingDir3 = Vec4.of(this.getForward(), 0);
		float facingVsMovingDotProduct2 = (float)(facingDir3.x * moveDir.x + facingDir3.z * moveDir.z + facingDir3.w * moveDir.w);
		if (facingVsMovingDotProduct2 < -0.15F) {
			return;
		}
		Entity This = (Entity) (Object) this;
		CollisionContext context = CollisionContext.of(This);
		BlockPos ceilingPos = BlockPos4.containing(this.getX(), this.getBoundingBox().maxY, this.getZ(), this.getW());
		BlockState aboveBlock1 = this.level().getBlockState(ceilingPos);
		if (aboveBlock1.getCollisionShape(this.level(), ceilingPos, context).isEmpty()) {
			ceilingPos = ceilingPos.above();
			BlockState aboveBlock2 = this.level().getBlockState(ceilingPos);
			if (aboveBlock2.getCollisionShape(this.level(), ceilingPos, context).isEmpty()) {
				float lookAheadSteps = 7.0F;
				float jumpHeight = 1.2F;
				if (this.hasEffect(MobEffects.JUMP_BOOST)) {
					//noinspection DataFlowIssue
					jumpHeight += (this.getEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75F;
				}

				float lookAheadDist = Math.max(currentSpeed * lookAheadSteps, 1.0F / moveDistInverted);
				Vec4 segEnd = moveEnd.add(moveDir.scale(lookAheadDist));
				float playerWidth = this.getBbWidth();
				float playerHeight = this.getBbHeight();
				AABB4 testBox = new AABB4(moveBegin, segEnd.add(0.0, playerHeight, 0.0, 0.0))
					.inflate(playerWidth, 0.0, playerWidth, playerWidth);
				Vec4 segBegin = moveBegin.add(0.0, 0.51F, 0.0, 0.0);
				segEnd = segEnd.add(0.0, 0.51F, 0.0, 0.0);
				Vec4 rightDir = Vec4.cross(moveDir, new Vec4(0.0, 1.0, 0.0, 0.0), new Vec4(0.0, 0.0, 0.0, 1.0));
				Vec4 rightOffset = rightDir.scale(playerWidth * 0.5F);
				Vec4 leftSegBegin = segBegin.subtract(rightOffset);
				Vec4 leftSegEnd = segEnd.subtract(rightOffset);
				Vec4 rightSegBegin = segBegin.add(rightOffset);
				Vec4 rightSegEnd = segEnd.add(rightOffset);
				Iterable<VoxelShape> collisions = this.level().getCollisions(This, testBox);
				Iterator<AABB> shape = StreamSupport.stream(collisions.spliterator(), false).flatMap(s -> s.toAabbs().stream()).iterator();
				float obstacleHeight = Float.MIN_VALUE;

				while (shape.hasNext()) {
					AABB4 box = (AABB4) shape.next();
					if (box.intersects(leftSegBegin, leftSegEnd) || box.intersects(rightSegBegin, rightSegEnd)) {
						obstacleHeight = (float) box.maxY;
						Vec4 obstacleShapeCenter = box.getCenter();
						BlockPos obstacleBlockPos = BlockPos.containing(obstacleShapeCenter);

						for (int steps = 1; steps < jumpHeight; steps++) {
							BlockPos abovePos1 = obstacleBlockPos.above(steps);
							BlockState aboveBlock = this.level().getBlockState(abovePos1);
							VoxelShape blockShape;
							if (!(blockShape = aboveBlock.getCollisionShape(this.level(), abovePos1, context)).isEmpty()) {
								obstacleHeight = (float) blockShape.max(Direction.Axis.Y) + abovePos1.getY();
								if (obstacleHeight - this.getY() > jumpHeight) {
									return;
								}
							}

							if (steps > 1) {
								ceilingPos = ceilingPos.above();
								BlockState aboveBlock3 = this.level().getBlockState(ceilingPos);
								if (!aboveBlock3.getCollisionShape(this.level(), ceilingPos, context).isEmpty()) {
									return;
								}
							}
						}
						break;
					}
				}

				if (obstacleHeight != Float.MIN_VALUE) {
					float yDelta = (float)(obstacleHeight - this.getY());
					if (!(yDelta <= 0.5F) && !(yDelta > jumpHeight)) {
						this.autoJumpTime = 1;
					}
				}
			}
		}
	}

	@Definition(id = "aLengthSquared", local = @Local(type = double.class, name = "aLengthSquared"))
	@Expression("aLengthSquared = @(?)")
	@ModifyExpressionValue(method = "isHorizontalCollisionMinor", at = @At("MIXINEXTRAS:EXPRESSION"))
	double isHorizontalCollisionMinor_aLengthSquared(double original) {
		return original + Mth.square(this.wwa);
	}
	@Definition(id = "movementLengthSquared", local = @Local(type = double.class, name = "movementLengthSquared"))
	@Expression("movementLengthSquared = @(?)")
	@ModifyExpressionValue(method = "isHorizontalCollisionMinor", at = @At("MIXINEXTRAS:EXPRESSION"))
	double isHorizontalCollisionMinor_movementLengthSquared(double original, @Local(argsOnly = true, name = "movement") Vec3 movement) {
		return original + Mth.square(((Vec4) movement).w);
	}
	@Definition(id = "dotProduct", local = @Local(type = double.class, name = "dotProduct"))
	@Expression("dotProduct = @(?)")
	@ModifyExpressionValue(method = "isHorizontalCollisionMinor", at = @At("MIXINEXTRAS:EXPRESSION"))
	double isHorizontalCollisionMinor_dotProduct(double original, @Local(argsOnly = true, name = "movement") Vec3 movement) {
		return original + this.wwa * ((Vec4) movement).w;
	}

	// TODO updateIsUnderwater when 4D sound engine
	// TODO getRopeHoldPosition?

	@Redirect(method = "pick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 pick(
		Vec3 from, double x, double y, double z,
		@Local(name = "direction") Vec3 direction,
		@Local(name = "maxDistance") double maxDistance
	) {
		double w = ((Position4) direction).w() * maxDistance;
		return ((Vec4) from).add(x, y, z, w);
	}
	@Redirect(method = "pick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB pick(AABB instance, double xAdd, double yAdd, double zAdd) {
		return instance.inflate(xAdd);
	}

	@Redirect(method = "filterHitResult", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getApproximateNearest(DDD)Lnet/minecraft/core/Direction;"
	))
	private static Direction filterHitResult(double dx, double dy, double dz, @Local(name = "location") Vec3 location, @Local(argsOnly = true, name = "from") Vec3 from) {
		if (!(location instanceof Vec4 location4 && from instanceof Vec4 from4)) throw Err4.container3();
		return Direction4.getApproximateNearest(dx, dy, dz, location4.w - from4.w);
	}
}
