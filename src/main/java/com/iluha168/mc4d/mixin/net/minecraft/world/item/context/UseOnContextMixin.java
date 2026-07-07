package com.iluha168.mc4d.mixin.net.minecraft.world.item.context;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// TODO remove all hacks when 4D renderer
@Mixin(UseOnContext.class)
abstract class UseOnContextMixin {
	@Shadow
	public abstract BlockPos getClickedPos();

	@Shadow
	@Final
	private @Nullable Player player;

	@Shadow
	@Final
	private BlockHitResult hitResult;

	@Definition(id = "hitResult", field = "Lnet/minecraft/world/item/context/UseOnContext;hitResult:Lnet/minecraft/world/phys/BlockHitResult;")
	@Expression("this.hitResult = @(?)")
	@ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/BlockHitResult;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockHitResult init(BlockHitResult hitResult, @Local(argsOnly = true, name = "player") @Nullable Player player) {
		if (player != null && !player.isShiftKeyDown()) {
			final int playerBlockW = ((Entity4) player).getBlockW();
			final int hitBlockW = Vec4i.getW(hitResult.getBlockPos());

			if (hitBlockW > playerBlockW) {
				hitResult = hitResult.withDirection(Direction4.KATA);
			} else if (hitBlockW < playerBlockW) {
				hitResult = hitResult.withDirection(Direction4.ANA);
			}
		}
		return hitResult;
	}

	@WrapOperation(method = "getClickedFace", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/BlockHitResult;getDirection()Lnet/minecraft/core/Direction;"
	))
	Direction getClickedFace(BlockHitResult hitResult, Operation<Direction> original) {
		if (player != null) {
			final int playerBlockW = ((Entity4) this.player).getBlockW();
			final int hitBlockW = Vec4i.getW(hitResult.getBlockPos());

			if (hitBlockW > playerBlockW) return Direction4.KATA;
			if (hitBlockW < playerBlockW) return Direction4.ANA;
		}
		return original.call(hitResult);
	}

	@WrapOperation(method = "getHorizontalDirection", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;getDirection()Lnet/minecraft/core/Direction;"
	))
	Direction getHorizontalDirection(Player player, Operation<Direction> original) {
		final int playerBlockW = ((Entity4) player).getBlockW();
		final int hitBlockW = Vec4i.getW(this.hitResult.getBlockPos());

		if (hitBlockW > playerBlockW) return Direction4.KATA;
		if (hitBlockW < playerBlockW) return Direction4.ANA;

		return original.call(player);
	}
}
