package com.iluha168.mc4d.mixin.net.minecraft.world.level.border;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.border.BorderChangeListener4;
import com.iluha168.mc4d.world.level.border.WorldBorder4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.iluha168.mc4d.world.phys.shapes.Shapes4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldBorder.class)
abstract
class WorldBorderMixin extends SavedData implements WorldBorder4 {
	@Shadow
	public abstract double getMinX();

	@Shadow
	public abstract double getMaxX();

	@Shadow
	public abstract double getMinZ();

	@Shadow
	public abstract double getMaxZ();

	@Shadow
	private WorldBorder.BorderExtent extent;
	@Shadow
	private double centerX;
	@Shadow
	private double centerZ;

	@Shadow
	protected abstract List<BorderChangeListener> getListeners();

	@Shadow
	@Final
	private WorldBorder.Settings settings;
	@Unique private double centerW;

	@ModifyConstant(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder$Settings;)V", constant = @Constant(intValue = 29999984))
	private static int absoluteMaxSize(int constant) {
		return Level4.MAX_LEVEL_SIZE - 16;
	}
	@ModifyArg(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder$Settings;)V", index = 1, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder$StaticBorderExtent;<init>(Lnet/minecraft/world/level/border/WorldBorder;D)V"
	))
	private static double extent(double size) {
		return 2*Level4.MAX_LEVEL_SIZE - 30;
	}

	@Redirect(method = "isWithinBounds(Lnet/minecraft/core/BlockPos;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(DD)Z"
	))
	boolean isWithinBounds_blockPos(WorldBorder instance, double x, double z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((WorldBorder4) instance).isWithinBounds4(x, z, Vec4i.getW(pos));
	}
	@Redirect(method = "isWithinBounds(Lnet/minecraft/world/phys/Vec3;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(DD)Z"
	))
	boolean isWithinBounds_vec(WorldBorder instance, double x, double z, @Local(argsOnly = true, name = "pos") Vec3 pos) {
		if (!(pos instanceof Vec4 pos4)) throw Err4.container3();
		return ((WorldBorder4) instance).isWithinBounds4(x, z, pos4.w);
	}
	@Redirect(method = "isWithinBounds(Lnet/minecraft/world/level/ChunkPos;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(DD)Z",
		ordinal = 0
	))
	boolean isWithinBounds_chunkPosMin(WorldBorder instance, double x, double z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ((WorldBorder4) instance).isWithinBounds4(x, z, ChunkPos4.as(pos).getMinBlockW());
	}
	@Redirect(method = "isWithinBounds(Lnet/minecraft/world/level/ChunkPos;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(DD)Z",
		ordinal = 1
	))
	boolean isWithinBounds_chunkPosMax(WorldBorder instance, double x, double z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ((WorldBorder4) instance).isWithinBounds4(x, z, ChunkPos4.as(pos).getMaxBlockW());
	}
	@Redirect(method = "isWithinBounds(Lnet/minecraft/world/phys/AABB;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(DDDD)Z"
	))
	boolean isWithinBounds_box(WorldBorder instance, double minX, double minZ, double maxX, double maxZ, @Local(argsOnly = true, name = "aabb") AABB aabb) {
		if (!(aabb instanceof AABB4 aabb4)) throw Err4.container3();
		return ((WorldBorder4) instance).isWithinBounds4(minX, minZ, aabb4.minW, maxX, maxZ, aabb4.maxW - Mth.EPSILON);
	}
	
	@Overwrite
	@Deprecated
	private boolean isWithinBounds(double minX, double minZ, double maxX, double maxZ) {
		throw Err4.arguments2("WorldBorder4#isWithinBounds");
	}
	@Override
	public boolean isWithinBounds4(double minX, double minZ, double minW, double maxX, double maxZ, double maxW) {
		return this.isWithinBounds4(minX, minZ, minW) && this.isWithinBounds4(maxX, maxZ, maxW);
	}
	
	@Overwrite
	@Deprecated
	public boolean isWithinBounds(double x, double z) {
		throw Err4.arguments2("WorldBorder4#isWithinBounds");
	}
	@Override
	public boolean isWithinBounds4(double x, double z, double w) {
		return this.isWithinBounds4(x, z, w, 0.0);
	}
	
	@Overwrite
	@Deprecated
	public boolean isWithinBounds(double x, double z, double margin) {
		throw Err4.arguments2("WorldBorder4#isWithinBounds");
	}
	@Override
	public boolean isWithinBounds4(double x, double z, double w, double margin) {
		return x >= this.getMinX() - margin && x < this.getMaxX() + margin
			&& z >= this.getMinZ() - margin && z < this.getMaxZ() + margin
			&& w >= this.getMinW() - margin && w < this.getMaxW() + margin;
	}
	
	@Redirect(method = "clampToBounds(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;clampToBounds(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos clampToBounds_blockPos(WorldBorder instance, double x, double y, double z, @Local(argsOnly = true, name = "position") BlockPos position) {
		return ((WorldBorder4) instance).clampToBounds(x, y, z, Vec4i.getW(position));
	}
	@Redirect(method = "clampToBounds(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/core/BlockPos;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;clampToBounds(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos clampToBounds_vec(WorldBorder instance, double x, double y, double z, @Local(argsOnly = true, name = "position") Vec3 position) {
		if (!(position instanceof Vec4 position4)) throw Err4.container3();
		return ((WorldBorder4) instance).clampToBounds(x, y, z, position4.w());
	}
	
	@Overwrite
	@Deprecated
	public BlockPos clampToBounds(double x, double y, double z) {
		throw Err4.arguments3("WorldBorder4#clampToBounds");
	}
	@Override
	public BlockPos clampToBounds(double x, double y, double z, double w) {
		return BlockPos.containing(this.clampVec4ToBound(x, y, z, w));
	}
	
	@Redirect(method = "clampVec3ToBound(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;clampVec3ToBound(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 clampVec3ToBound(WorldBorder instance, double x, double y, double z, @Local(argsOnly = true, name = "position") Vec3 position) {
		if (!(position instanceof Vec4 position4)) throw Err4.container3();
		return ((WorldBorder4) instance).clampVec4ToBound(x, y, z, position4.w);
	}
	
	@Overwrite
	public Vec3 clampVec3ToBound(double x, double y, double z) {
		throw Err4.arguments3("WorldBorder4#clampVec4ToBound");
	}
	@Override
	public Vec4 clampVec4ToBound(double x, double y, double z, double w) {
		return new Vec4(
			Mth.clamp(x, this.getMinX(), this.getMaxX() - Mth.EPSILON),
			y,
			Mth.clamp(z, this.getMinZ(), this.getMaxZ() - Mth.EPSILON),
			Mth.clamp(w, this.getMinW(), this.getMaxW() - Mth.EPSILON)
		);
	}

	@Redirect(method = "getDistanceToBorder(Lnet/minecraft/world/entity/Entity;)D", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;getDistanceToBorder(DD)D"
	))
	double getDistanceToBorder(WorldBorder instance, double x, double z, @Local(argsOnly = true, name = "entity") Entity entity) {
		return ((WorldBorder4) instance).getDistanceToBorder(x, z, ((Entity4) entity).getW());
	}
	@Overwrite
	@Deprecated
	public double getDistanceToBorder(double x, double z) {
		throw Err4.arguments2("WorldBorder4#getDistanceToBorder");
	}
	@Override
	public double getDistanceToBorder(double x, double z, double w) {
		double fromKata = w - this.getMinW();
		double fromAna = this.getMaxW() - w;
		double fromNorth = z - this.getMinZ();
		double fromSouth = this.getMaxZ() - z;
		double fromWest = x - this.getMinX();
		double fromEast = this.getMaxX() - x;
		double min = Math.min(fromWest, fromEast);
		min = Math.min(min, fromNorth);
		min = Math.min(min, fromSouth);
		min = Math.min(min, fromKata);
		return Math.min(min, fromAna);
	}

	@Redirect(method = "isInsideCloseToBorder", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;absMax(DD)D"
	))
	double isInsideCloseToBorder(double a, double b, @Local(argsOnly = true, name = "boundingBox") AABB boundingBox) {
		if (!(boundingBox instanceof AABB4 bb4)) throw Err4.container3();
		return MathHelpers.absMax(a, b, bb4.getWsize());
	}
	@Redirect(method = "isInsideCloseToBorder", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(DDD)Z"
	))
	boolean isInsideCloseToBorder(WorldBorder instance, double x, double z, double margin, @Local(argsOnly = true, name = "source") Entity source) {
		return ((WorldBorder4) instance).isWithinBounds4(x, z, ((Entity4) source).getW(), margin);
	}

	@Override
	public double getMinW(float deltaPartialTick) {
		return ((WorldBorder4.BorderExtent) this.extent).getMinW(deltaPartialTick);
	}
	@Override
	public double getMaxW(float deltaPartialTick) {
		return ((WorldBorder4.BorderExtent) this.extent).getMaxW(deltaPartialTick);
	}

	@Override
	public double getCenterW() {
		return this.centerW;
	}

	@Overwrite
	@Deprecated
	public void setCenter(double x, double z) {
		throw Err4.arguments2("WorldBorder4#setCenter");
	}
	@Override
	public void setCenter(double x, double z, double w) {
		this.centerX = x;
		this.centerZ = z;
		this.centerW = w;
		this.extent.onCenterChange();
		this.setDirty();

		for (BorderChangeListener listener : this.getListeners()) {
			((BorderChangeListener4) listener).onSetCenter((WorldBorder) (Object) this, x, z, w);
		}
	}

	@Redirect(method = "applyInitialSettings", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/border/WorldBorder;setCenter(DD)V"
	))
	void applyInitialSettings(WorldBorder instance, double x, double z) {
		((WorldBorder4) instance).setCenter(x, z, WorldBorder4.Settings.as(this.settings).centerW());
	}

	@Mixin(WorldBorder.MovingBorderExtent.class)
	static abstract class MovingBorderExtentMixin implements WorldBorder4.BorderExtent {
		@Shadow
		@Final
		WorldBorder this$0;

		@Shadow
		public abstract double getPreviousSize();

		@Shadow
		public abstract double getSize();

		@Override
		public double getMinW(float deltaPartialTick) {
			return Mth.clamp(
				((WorldBorder4) this.this$0).getCenterW() - Mth.lerp(deltaPartialTick, this.getPreviousSize(), this.getSize()) / 2.0,
				-this$0.absoluteMaxSize,
				this$0.absoluteMaxSize
			);
		}
		@Override
		public double getMaxW(float deltaPartialTick) {
			return Mth.clamp(
				((WorldBorder4) this.this$0).getCenterW() + Mth.lerp(deltaPartialTick, this.getPreviousSize(), this.getSize()) / 2.0,
				-this$0.absoluteMaxSize,
				this$0.absoluteMaxSize
			);
		}

		@Redirect(method = "getCollisionShape", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/shapes/Shapes;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
		))
		VoxelShape getCollisionShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			return Shapes4.box(
				minX, minY, minZ, Math.floor(this.getMinW(0.0F)),
				maxX, maxY, maxZ, Math.ceil(this.getMaxW(0.0F))
			);
		}
	}

	@Mixin(WorldBorder.Settings.class)
	static class SettingsMixin implements WorldBorder4.Settings {
		@Unique private double centerW;
		// equals, hashCode, etc. mixins not necessary

		@Override
		public double centerW() {
			return this.centerW;
		}
		@Override
		public void setCenterW(double centerW) {
			this.centerW = centerW;
		}

		@Definition(id = "DEFAULT", field = "Lnet/minecraft/world/level/border/WorldBorder$Settings;DEFAULT:Lnet/minecraft/world/level/border/WorldBorder$Settings;")
		@Definition(id = "Settings", type = WorldBorder.Settings.class)
		@Expression("DEFAULT = @(new Settings(?, ?, ?, ?, ?, ?, ?, ?, ?))")
		@ModifyArg(method = "<clinit>", index = 6, at = @At("MIXINEXTRAS:EXPRESSION"))
		private static double DEFAULT_size(double size) {
			return 2*Level4.MAX_LEVEL_SIZE - 30;
		}
		@Definition(id = "DEFAULT", field = "Lnet/minecraft/world/level/border/WorldBorder$Settings;DEFAULT:Lnet/minecraft/world/level/border/WorldBorder$Settings;")
		@Expression("DEFAULT = @(?)")
		@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static WorldBorder.Settings DEFAULT_centerW(WorldBorder.Settings original) {
			WorldBorder4.Settings.as(original).setCenterW(0.0);
			return original;
		}

		@Definition(id = "CODEC", field = "Lnet/minecraft/world/level/border/WorldBorder$Settings;CODEC:Lcom/mojang/serialization/Codec;")
		@Expression("CODEC = @(?)")
		@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static Codec<WorldBorder.Settings> CODEC(Codec<WorldBorder.Settings> original) {
			// TODO split into small mixins with generic mess
			return RecordCodecBuilder.create(
				i -> i.group(
						Codec.doubleRange(-(Level4.MAX_LEVEL_SIZE - 16), Level4.MAX_LEVEL_SIZE - 16).fieldOf("center_x").forGetter(WorldBorder.Settings::centerX),
						Codec.doubleRange(-(Level4.MAX_LEVEL_SIZE - 16), Level4.MAX_LEVEL_SIZE - 16).fieldOf("center_z").forGetter(WorldBorder.Settings::centerZ),
						Codec.doubleRange(-(Level4.MAX_LEVEL_SIZE - 16), Level4.MAX_LEVEL_SIZE - 16).fieldOf("center_w").forGetter(border -> WorldBorder4.Settings.as(border).centerW()),
						Codec.DOUBLE.fieldOf("damage_per_block").forGetter(WorldBorder.Settings::damagePerBlock),
						Codec.DOUBLE.fieldOf("safe_zone").forGetter(WorldBorder.Settings::safeZone),
						Codec.INT.fieldOf("warning_blocks").forGetter(WorldBorder.Settings::warningBlocks),
						Codec.INT.fieldOf("warning_time").forGetter(WorldBorder.Settings::warningTime),
						Codec.DOUBLE.fieldOf("size").forGetter(WorldBorder.Settings::size),
						Codec.LONG.fieldOf("lerp_time").forGetter(WorldBorder.Settings::lerpTime),
						Codec.DOUBLE.fieldOf("lerp_target").forGetter(WorldBorder.Settings::lerpTarget)
					)
					.apply(i, WorldBorder4.Settings::of)
			);
		}

		@Inject(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder;)V", at = @At("TAIL"))
		void init(WorldBorder worldBorder, CallbackInfo ci) {
			this.centerW = ((WorldBorder4) worldBorder).getCenterW();
		}
	}


	@Mixin(WorldBorder.StaticBorderExtent.class)
	static class StaticBorderExtent implements WorldBorder4.BorderExtent {
		@Shadow
		@Final
		WorldBorder this$0;

		@Shadow
		@Final
		private double size;

		@Unique private double minW;
		@Unique private double maxW;

		@Override
		public double getMinW(float deltaPartialTick) {
			return this.minW;
		}
		@Override
		public double getMaxW(float deltaPartialTick) {
			return this.maxW;
		}

		@Redirect(method = "updateBox", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/phys/shapes/Shapes;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
		))
		VoxelShape updateBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			WorldBorder4 border = (WorldBorder4) this.this$0;
			this.minW = Mth.clamp(
				border.getCenterW() - this.size / 2.0, -this$0.absoluteMaxSize, this$0.absoluteMaxSize
			);
			this.maxW = Mth.clamp(
				border.getCenterW() + this.size / 2.0, -this$0.absoluteMaxSize, this$0.absoluteMaxSize
			);
			return Shapes4.box(
				minX, minY, minZ, Math.floor(this.getMinW(0.0F)),
				maxX, maxY, maxZ, Math.ceil(this.getMaxW(0.0F))
			);
		}
	}
}
