package com.iluha168.mc4d.mixin.net.minecraft.commands.arguments.coordinates;

import com.iluha168.mc4d.commands.arguments.coordinates.Coordinates4;
import com.iluha168.mc4d.commands.arguments.coordinates.WorldCoordinates4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldCoordinates.class)
class WorldCoordinatesMixin implements Coordinates4, WorldCoordinates4 {
	@Shadow
	@Final
	private WorldCoordinate z;
	@Unique
	private WorldCoordinate w;

	@Override
	@NonNull
	public WorldCoordinate w() {
		if (this.w == null)
			throw Err4.field4missing("w");
		return this.w;
	}
	@Override
	public void setW(WorldCoordinate w) {
		this.w = w;
	}

	@Redirect(method = "<clinit>", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	private static Vec2 ZERO_ROTATION(float x, float y) {
		return new RotationVec(x, y, x, y);
	}

	@Redirect(method = "getPosition", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getPosition(double x, double y, double z, @Local(name = "pos") Vec3 pos) {
		return new Vec4(x, y, z, this.w().get(((Vec4) pos).w));
	}

	@Redirect(method = "getRotation", at = @At(
		value = "NEW",
		target = "(FF)Lnet/minecraft/world/phys/Vec2;"
	))
	Vec2 getRotation(float x, float y, @Local(name = "rot") Vec2 rot) {
		final RotationVec rot4 = (RotationVec) rot;
		return new RotationVec(x, y, (float) this.z.get(rot4.w), (float) this.w().get(rot4.v));
	}

	@Override
	public boolean isWRelative() {
		return this.w().isRelative();
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
			WorldCoordinates4.as(coordinates).setW(WorldCoordinate.parseInt(reader));
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
			WorldCoordinates4.as(coordinates).setW(WorldCoordinate.parseDouble(reader, centerCorrect));
			return coordinates;
		} else {
			reader.setCursor(start);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}

	@Overwrite
	@Deprecated
	public static WorldCoordinates absolute(double x, double y, double z) {
		throw Err4.arguments3(null);
	}

	@Definition(id = "WorldCoordinate", type = WorldCoordinate.class)
	@Expression("new WorldCoordinate(true, 0.0)")
	@ModifyExpressionValue(method = "absolute(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates;", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static WorldCoordinate absolute_wRot(WorldCoordinate original, @Local(argsOnly = true, name = "rotation") Vec2 rotation) {
		return new WorldCoordinate(false, ((RotationVec) rotation).w);
	}
	@ModifyExpressionValue(method = "absolute(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates;", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;Lnet/minecraft/commands/arguments/coordinates/WorldCoordinate;)Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates;"
	))
	private static WorldCoordinates absolute_vRot(WorldCoordinates coordinates, @Local(argsOnly = true, name = "rotation") Vec2 rotation) {
		WorldCoordinates4.as(coordinates).setW(new WorldCoordinate(false, ((RotationVec) rotation).v));
		return coordinates;
	}
}
