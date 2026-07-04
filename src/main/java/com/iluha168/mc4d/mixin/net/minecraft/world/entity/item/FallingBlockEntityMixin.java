package com.iluha168.mc4d.mixin.net.minecraft.world.entity.item;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.mixin.net.minecraft.world.entity.EntityMixin;
import com.iluha168.mc4d.network.protocol.game.ClientboundAddEntityPacket4;
import com.iluha168.mc4d.world.entity.item.FallingBlockEntity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends EntityMixin implements FallingBlockEntity4 {
	@Shadow
	public abstract void setStartPos(BlockPos pos);

	@Unique	protected byte initIncomplete;

	@Redirect(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;setPos(DDD)V"
	))
	void init_setPos(FallingBlockEntity instance, double x, double y, double z) {
		this.initIncomplete = 1;
	}
	@Redirect(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)V", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	Vec3 init_setDeltaMovement() {
		return Vec4.ZERO;
	}
	@Override
	public void init_finish(double x, double y, double z, double w) {
		if (this.initIncomplete == 0) {
			throw new IllegalStateException("Programmer error: FallingBlockEntity4#init_finish called more than once.");
		}
		if (this.initIncomplete != 1) {
			throw new IllegalStateException("Programmer error: wrong FallingBlockEntity4#init_finish called.");
		}
		this.initIncomplete = 0;
		try {
			this.setPos(new Vec4(x, y, z, w));
			this.wo = w;
			this.setStartPos(this.blockPosition());
		} catch (Throwable e) {
			this.initIncomplete = 1;
			throw e;
		}
	}

	@Redirect(method = "fall", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/entity/item/FallingBlockEntity;"
	))
	private static FallingBlockEntity fall(Level level, double x, double y, double z, BlockState blockState, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return FallingBlockEntity4.from(level, x, y, z, Vec4i.getW(pos) + 0.5, blockState);
	}

	@Redirect(method = "tick", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 tick_clip(double x, double y, double z) {
		return new Vec4(x, y, z, this.wo);
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 tick_multiply(Vec3 instance, double xScale, double yScale, double zScale) {
		return ((Vec4) instance).multiply(xScale, yScale, zScale, zScale);
	}

	@Redirect(method = "recreateFromPacket", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;setPos(DDD)V"
	))
	void recreateFromPacket(FallingBlockEntity instance, double x, double y, double z, @Local(argsOnly = true, name = "packet") ClientboundAddEntityPacket packet) {
		instance.setPos(new Vec4(x, y, z, ((ClientboundAddEntityPacket4) packet).getW()));
	}
}
