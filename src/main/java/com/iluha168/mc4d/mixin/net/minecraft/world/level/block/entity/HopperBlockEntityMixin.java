package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.net.neoforged.neoforge.transfer.item.VanillaInventoryCodeHooks4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.block.entity.Hopper4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ContainerOrHandler;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(HopperBlockEntity.class)
class HopperBlockEntityMixin extends BlockEntityMixin implements Hopper4 {
	@Shadow
	private static @Nullable Container getBlockContainer(Level level, BlockPos pos, BlockState state) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Redirect(method = "suckInItems", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos suckInItems(double x, double y, double z, @Local(argsOnly = true, name = "hopper") Hopper hopper) {
		return BlockPos4.containing(x, y, z, ((Hopper4) hopper).getLevelW());
	}

	@Redirect(method = "getSourceContainer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getContainerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;DDD)Lnet/minecraft/world/Container;"
	))
	private static Container getSourceContainer(Level level, BlockPos pos, BlockState state, double x, double y, double z, @Local(argsOnly = true, name = "hopper") Hopper hopper) {
		return getContainerAt(level, pos, state, x, y, z, ((Hopper4) hopper).getLevelW());
	}

	@Redirect(method = "getItemsAtAndAbove", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getItemsAtAndAbove(AABB instance, double xa, double ya, double za, @Local(argsOnly = true, name = "hopper") Hopper hopper) {
		return ((AABB4) instance).move(xa, ya, za, ((Hopper4) hopper).getLevelW() - 0.5);
	}

	@Redirect(method = "getContainerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/Container;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getContainerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;DDD)Lnet/minecraft/world/Container;"
	))
	private static Container getContainerAt_getContainerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z) {
		return getContainerAt(level, pos, state, x, y, z, Vec4i.getW(pos) + 0.5);
	}

	@Overwrite
	@Deprecated
	private static @Nullable Container getContainerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z) {
		throw Err4.arguments3(null);
	}
	@SuppressWarnings("DataFlowIssue")
	@Unique
	private static @Nullable Container getContainerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z, double w) {
		Container result = getBlockContainer(level, pos, state);
		if (result == null) {
			result = getEntityContainer(level, x, y, z, w);
		}

		return result;
	}

	@Overwrite
	@Deprecated
	private static @Nullable Container getEntityContainer(Level level, double x, double y, double z) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static @Nullable Container getEntityContainer(Level level, double x, double y, double z, double w) {
		List<Entity> entities = level.getEntities(
			(Entity) null,
			new AABB4(
				x - 0.5, y - 0.5, z - 0.5, w - 0.5,
				x + 0.5, y + 0.5, z + 0.5, w + 0.5
			),
			EntitySelector.CONTAINER_ENTITY_SELECTOR
		);
		return !entities.isEmpty() ? (Container)entities.get(level.getRandom().nextInt(entities.size())) : null;
	}

	@Redirect(method = "getSourceContainerOrHandler", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getContainerOrHandlerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;DDDLnet/minecraft/core/Direction;)Lnet/neoforged/neoforge/transfer/item/ContainerOrHandler;"
	))
	private static ContainerOrHandler getSourceContainerOrHandler(
		Level level, BlockPos pos, BlockState state, double x, double y, double z, Direction side,
		@Local(argsOnly = true, name = "hopper") Hopper hopper
	) {
		return getContainerOrHandlerAt(level, pos, state, x, y, z, ((Hopper4) hopper).getLevelW(), side);
	}

	@Redirect(method = "getContainerOrHandlerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/neoforged/neoforge/transfer/item/ContainerOrHandler;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getContainerOrHandlerAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;DDDLnet/minecraft/core/Direction;)Lnet/neoforged/neoforge/transfer/item/ContainerOrHandler;"
	))
	private static ContainerOrHandler getContainerOrHandlerAt_getContainerOrHandlerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z, Direction side) {
		return getContainerOrHandlerAt(level, pos, state, x, y, z, (double) Vec4i.getW(pos) + 0.5, side);
	}

	@Overwrite
	@Deprecated
	private static net.neoforged.neoforge.transfer.item.ContainerOrHandler getContainerOrHandlerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z, @Nullable Direction side) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static net.neoforged.neoforge.transfer.item.ContainerOrHandler getContainerOrHandlerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z, double w, @Nullable Direction side) {
		Container container = getBlockContainer(level, pos, state);
		if (container != null) {
			return new net.neoforged.neoforge.transfer.item.ContainerOrHandler(container, null);
		}
		var blockItemHandler = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, pos, state, null, side);
		if (blockItemHandler != null) {
			return new net.neoforged.neoforge.transfer.item.ContainerOrHandler(null, blockItemHandler);
		}
		return VanillaInventoryCodeHooks4.getEntityContainerOrHandler(level, x, y, z, w, side);
	}

	@Override
	public double getLevelW() {
		return Vec4i.getW(this.worldPosition) + 0.5;
	}

	@Redirect(method = "entityInside", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB entityInside(AABB instance, double xa, double ya, double za, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((AABB4) instance).move(xa, ya, za, -Vec4i.getW(pos));
	}
}
