package com.iluha168.mc4d.mixin.net.minecraft.core.dispenser;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.entity.item.ItemEntity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DefaultDispenseItemBehavior.class)
class DefaultDispenseItemBehaviorMixin {
	@Redirect(method = "spawnItem", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
	))
	private static ItemEntity spawnItem_itemEntity(
		Level level, double x, double y, double z, ItemStack itemStack,
		@Local(argsOnly = true, name = "position") Position position
	) {
		return ItemEntity4.from(level, x, y, z, ((Vec4) position).w, itemStack);
	}
	@Redirect(method = "spawnItem", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
	))
	private static void spawnItem_setDeltaMovement(
		ItemEntity instance, double x, double y, double z,
		@Local(argsOnly = true, name = "accuracy") int accuracy,
		@Local(argsOnly = true, name = "direction") Direction direction,
		@Local(name = "random") RandomSource random,
		@Local(name = "pow") double pow
	) {
		instance.setDeltaMovement(new Vec4(
			x, y, z,
			random.triangle(Direction4.as(direction).getStepW() * pow, 0.0172275 * accuracy)
		));
	}
}
