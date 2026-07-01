package com.iluha168.mc4d.mixin.net.minecraft.world;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.Containers4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Containers.class)
class ContainersMixin implements Containers4 {
	@Redirect(method = "dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/Container;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/Containers;dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V"
	))
	private static void dropContents_blockPos(Level level, double x, double y, double z, Container container, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		dropContents(level, x, y, z, Vec4i.getW(pos), container);
	}

	@Redirect(method = "dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/Container;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/Containers;dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V"
	))
	private static void dropContents_entity(Level level, double x, double y, double z, Container container, @Local(argsOnly = true, name = "entity") Entity entity) {
		dropContents(level, x, y, z, ((Entity4) entity).getW(), container);
	}

	@Overwrite
	@Deprecated
	private static void dropContents(Level level, double x, double y, double z, Container container) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static void dropContents(Level level, double x, double y, double z, double w, Container container) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			Containers4.dropItemStack(level, x, y, z, w, container.getItem(i));
		}
	}

	@Redirect(method = "lambda$dropContents$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"
	))
	private static void dropContents_list(Level level, double x, double y, double z, ItemStack itemStack, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		Containers4.dropItemStack(level, x, y, z, Vec4i.getW(pos), itemStack);
	}

	@Overwrite
	@Deprecated
	public static void dropItemStack(Level level, double x, double y, double z, ItemStack itemStack) {
		throw Err4.arguments3("Container4#dropItemStack");
	}
}
