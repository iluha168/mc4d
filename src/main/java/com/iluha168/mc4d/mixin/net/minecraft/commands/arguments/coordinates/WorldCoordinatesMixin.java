package com.iluha168.mc4d.mixin.net.minecraft.commands.arguments.coordinates;

import com.iluha168.mc4d.commands.arguments.coordinates.Coordinates4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldCoordinates.class)
class WorldCoordinatesMixin implements Coordinates4 {
	@Unique private WorldCoordinate w;

	@Redirect(method = "getPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition(double x, double y, double z, @Local(name = "pos") Vec3 pos) {
		if (this.w == null)
			throw Err4.field4missing("w");
		return new Vec4(x, y, z, this.w.get(((Vec4) pos).w));
	}

	@Override
	public boolean isWRelative() {
		if (this.w == null)
			throw Err4.field4missing("w");
		return this.w.isRelative();
	}

	@ModifyExpressionValue(method = "parseInt", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates;"
	))
	private static WorldCoordinates parseInt(
		WorldCoordinates coordinates,
		@Local(argsOnly = true, name = "reader") StringReader reader,
		@Local(name = "start") int start
	) throws CommandSyntaxException {
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			((WorldCoordinatesMixin) (Object) coordinates).w = WorldCoordinate.parseInt(reader);
			return coordinates;
		} else {
			reader.setCursor(start);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}

	@ModifyExpressionValue(method = "parseDouble", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates;"
	))
	private static WorldCoordinates parseDouble(
		WorldCoordinates coordinates,
		@Local(argsOnly = true, name = "reader") StringReader reader,
		@Local(argsOnly = true, name = "centerCorrect") boolean centerCorrect,
		@Local(name = "start") int start
	) throws CommandSyntaxException {
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			((WorldCoordinatesMixin) (Object) coordinates).w = WorldCoordinate.parseDouble(reader, centerCorrect);
			return coordinates;
		} else {
			reader.setCursor(start);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}

	@Overwrite
	public static WorldCoordinates absolute(double x, double y, double z) {
		throw Err4.arguments3(null);
	}

	@WrapMethod(method = "absolute(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates;")
	private static WorldCoordinates absolute(Vec2 rotation, Operation<WorldCoordinates> original) {
		WorldCoordinates coordinates = original.call(rotation);
		((WorldCoordinatesMixin) (Object) coordinates).w = coordinates.z();
		return coordinates;
	}
}
