package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.EntityType4;
import com.iluha168.mc4d.world.level.EntityGetter4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class)
class BaseSpawnerMixin {
	@Shadow
	private int spawnRange;

	@Redirect(method = "isNearPlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;hasNearbyAlivePlayer(DDDD)Z"
	))
	boolean isNearPlayer(Level instance, double x, double y, double z, double range, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((EntityGetter4) instance).hasNearbyAlivePlayer(x, y, z, Vec4i.getW(pos) + 0.5, range);
	}

	@Definition(id = "zP", local = @Local(type = double.class, name = "zP"))
	@Expression("zP = @(?)")
	@Inject(method = "clientTick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void clientTick_wP(
		Level level, BlockPos pos, CallbackInfo ci,
		@Share("wP") LocalDoubleRef wP,
		@Local(name = "random") RandomSource random
	) {
		wP.set(Vec4i.getW(pos) + random.nextDouble());
	}
	@Redirect(method = "clientTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void clientTick_addParticle(Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd, @Share("wP") LocalDoubleRef wP) {
		((LevelAccessor4) instance).addParticle(particle, x, y, z, wP.get(), xd, yd, zd, zd);
	}

	@Redirect(method = "serverTick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;CODEC:Lcom/mojang/serialization/Codec;",
		opcode = Opcodes.GETSTATIC
	))
	Codec<Vec4> serverTick_CODEC() {
		return Vec4.CODEC;
	}
	@Redirect(method = "lambda$serverTick$0", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 serverTick_spawnPos(
		double x, double y, double z,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		return new Vec4(x, y, z, Vec4i.getW(pos) + (random.nextDouble() - random.nextDouble()) * this.spawnRange + 0.5);
	}
	@Redirect(method = "serverTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/EntityType;getSpawnAABB(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB serverTick_getSpawnAABB(EntityType<?> instance, double x, double y, double z, @Local(name = "spawnPos") Vec3 spawnPos) {
		return ((EntityType4<?>) instance).getSpawnAABB(x, y, z, ((Vec4) spawnPos).w);
	}
	@Redirect(method = "lambda$serverTick$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	private static void serverTick_snapTo(Entity e, double x, double y, double z, float yRot, float xRot, @Local(argsOnly = true, name = "spawnPos") Vec3 spawnPos) {
		e.snapTo(new Vec4(x, y, z, ((Vec4) spawnPos).w), yRot, xRot);
	}
	@Redirect(method = "serverTick", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB serverTick_getEntities(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(name = "pos", argsOnly = true) BlockPos pos) {
		final int posW = Vec4i.getW(pos);
		return new AABB4(
			minX, minY, minZ, posW,
			maxX, maxY, maxZ, posW + 1
		);
	}
	@Redirect(method = "serverTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;snapTo(DDDFF)V"
	))
	void serverTick_snapTo(Entity entity, double x, double y, double z, float yRot, float xRot) {
		entity.snapTo(new Vec4(x, y, z, ((Entity4) entity).getW()), yRot, xRot);
	}
}
