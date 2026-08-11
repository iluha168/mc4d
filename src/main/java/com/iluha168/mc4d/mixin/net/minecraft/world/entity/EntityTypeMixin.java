package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.EntityAttachments4;
import com.iluha168.mc4d.world.entity.EntityType4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityType.class)
abstract
class EntityTypeMixin<T extends Entity> implements EntityType4<T> {
	@Shadow
	@Final
	private float spawnDimensionsScale;

	@Shadow
	public abstract float getWidth();

	@Shadow
	public abstract float getHeight();

	@Definition(id = "passengerAttachments", method = "Lnet/minecraft/world/entity/EntityType$Builder;passengerAttachments([Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/EntityType$Builder;")
	@Expression("?.passengerAttachments(?)")
	@ModifyArg(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Vec3[] passengerAttachments(Vec3[] vec3s) {
		if (vec3s.length == 1) {
			vec3s[0] = new Vec4(vec3s[0].x, vec3s[0].y, vec3s[0].z, 0);
			return vec3s;
		}
		// HAPPY_GHAST: 6 seats.
		final Vec3[] result = new Vec3[vec3s.length + 2];
		for (int i = 0; i < vec3s.length; i++) {
			final Vec3 point = vec3s[i];
			result[i] = new Vec4(point.x, point.y, point.z, 0);
		}
		final double y = vec3s[0].y;
		final double horizontalRadius = Mth.absMax(vec3s[0].x, vec3s[0].z);
		result[vec3s.length] = new Vec4(0, y, 0, -horizontalRadius);
		result[vec3s.length + 1] = new Vec4(0, y, 0, horizontalRadius);
		return result;
	}

	@Redirect(method = "create(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
	))
	void create_setPos(Entity entity, double x, double y, double z, @Local(argsOnly = true, name = "spawnPos") BlockPos spawnPos) {
		entity.setPos(new Vec4(x, y, z, Vec4i.getW(spawnPos) + 0.5));
	}
	@Redirect(method = "create(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void create_snapTo(
		Entity entity, double x, double y, double z, float yRot, float xRot,
		@Local(argsOnly = true, name = "spawnPos") BlockPos spawnPos,
		@Local(argsOnly = true, name = "level") ServerLevel level
	) {
		((Entity4) entity).snapTo(x, y, z, Vec4i.getW(spawnPos) + 0.5, yRot, xRot, RotationVec.randomWRotDeg(level.getRandom()), xRot);
	}
	@Redirect(method = "create(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/entity/Mob;yHeadRot:F",
		opcode = Opcodes.PUTFIELD
	))
	void create_yHeadRot(Mob mob, float yHeadRot) {
		final Entity4 mob4 = (Entity4) mob;
		mob4.setYHeadRot(yHeadRot, mob4.getWRot());
	}
	@Redirect(method = "create(Lnet/minecraft/server/level/ServerLevel;Ljava/util/function/Consumer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/entity/Mob;yBodyRot:F",
		opcode = Opcodes.PUTFIELD
	))
	void create_yBodyRot(Mob mob, float yBodyRot) {
		final Entity4 mob4 = (Entity4) mob;
		mob4.setYBodyRot(yBodyRot, mob4.getWRot());
	}

	@Redirect(method = "getYOffset", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getYOffset(AABB aabb, double xa, double ya, double za) {
		return ((AABB4) aabb).expandTowards(xa, ya, za, za);
	}

	@Overwrite
	@Deprecated
	public AABB getSpawnAABB(double x, double y, double z) {
		throw Err4.arguments3("EntityType4#getSpawnAABB");
	}
	@Override
	public AABB4 getSpawnAABB(double x, double y, double z, double w) {
		final float halfWidth = this.spawnDimensionsScale * this.getWidth() / 2.0F;
		final float height = this.spawnDimensionsScale * this.getHeight();
		return new AABB4(
			x - halfWidth, y, z - halfWidth, w - halfWidth,
			x + halfWidth, y + height, z + halfWidth, w + halfWidth
		);
	}

	@Mixin(EntityType.Builder.class)
	static class BuilderMixin<T extends Entity> implements EntityType4.Builder<T> {
		@Shadow
		private EntityAttachments.Builder attachments;

		@Redirect(method = "passengerAttachments([F)Lnet/minecraft/world/entity/EntityType$Builder;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/EntityAttachments$Builder;attach(Lnet/minecraft/world/entity/EntityAttachment;FFF)Lnet/minecraft/world/entity/EntityAttachments$Builder;"
		))
		EntityAttachments.Builder passengerAttachments(EntityAttachments.Builder attachments, EntityAttachment attachment, float x, float y, float z) {
			return ((EntityAttachments4.Builder) attachments).attach(attachment, x, y, z, z);
		}

		@Overwrite
		@Deprecated
		public EntityType.Builder<T> attach(EntityAttachment attachment, float x, float y, float z) {
			//noinspection ConstantValue TODO reconsider when 4D renderer entity models
			if (true) {
				return this.attach(attachment, x, y, z, 0F);
			}
			throw Err4.arguments3("EntityType4.Builder#attach");
		}
		@Override
		public EntityType.Builder<T> attach(EntityAttachment attachment, float x, float y, float z, float w) {
			this.attachments = ((EntityAttachments4.Builder) this.attachments).attach(attachment, x, y, z, w);
			//noinspection unchecked
			return (EntityType.Builder<T>) (Object) this;
		}
	}
}
