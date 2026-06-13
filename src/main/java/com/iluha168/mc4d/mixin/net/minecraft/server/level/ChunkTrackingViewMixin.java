package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.server.level.ChunkTrackingView4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ChunkTrackingView.class)
interface ChunkTrackingViewMixin extends ChunkTrackingView4 {
	@Mixin(targets = "net/minecraft/server/level/ChunkTrackingView$1")
	class EMPTYMixin implements ChunkTrackingView4 {
		@Overwrite
		@Deprecated
		public boolean contains(int chunkX, int chunkZ, boolean includeNeighbors) {
			throw Err4.arguments2("ChunkTrackingView#contains(ChunkPos)");
		}

		@Override
		public boolean contains(int chunkX, int chunkZ, int chunkW, boolean includeNeighbors) {
			return false;
		}
	}

	@Definition(id = "maxZ", local = @Local(type = int.class, name = "maxZ"))
	@Expression("maxZ = @(?)")
	@Inject(method = "difference", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void difference_minMaxW(
		ChunkTrackingView from, ChunkTrackingView _to, Consumer<ChunkPos> onEnter, Consumer<ChunkPos> onLeave, CallbackInfo ci,
		@Local(name = "last") ChunkTrackingView.Positioned last,
		@Local(name = "next") ChunkTrackingView.Positioned next,
		@Share("minW") LocalIntRef minW,
		@Share("maxW") LocalIntRef maxW
	) {
		ChunkTrackingView4.Positioned last4 = ChunkTrackingView4.Positioned.as(last);
		ChunkTrackingView4.Positioned next4 = ChunkTrackingView4.Positioned.as(next);
		minW.set(Math.min(last4.minW(), next4.minW()));
		maxW.set(Math.max(last4.maxW(), next4.maxW()));
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "minZ", local = @Local(type = int.class, name = "minZ"))
	@Expression("z = @(minZ)")
	@Inject(method = "difference", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void difference_w(
		ChunkTrackingView from, ChunkTrackingView _to, Consumer<ChunkPos> onEnter, Consumer<ChunkPos> onLeave, CallbackInfo ci,
		@Share("w") LocalIntRef w,
		@Share("minW") LocalIntRef minW
	) {
		w.set(minW.get());
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "difference", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int difference_incrementW(
		int one,
		@Share("w") LocalIntRef w,
		@Share("minW") LocalIntRef minW,
		@Share("maxW") LocalIntRef maxW
	) {
		w.set(w.get() + 1);
		if (w.get() <= maxW.get()) return 0;
		w.set(minW.get());
		return 1;
	}
	@Redirect(method = "difference", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ChunkTrackingView$Positioned;contains(II)Z"
	))
	private static boolean difference_contains(ChunkTrackingView.Positioned instance, int x, int z, @Share("w") LocalIntRef w) {
		return ChunkTrackingView4.as(instance).contains(x, z, w.get());
	}
	@WrapOperation(method = "difference", at = @At(
		value = "NEW",
		target = "(II)Lnet/minecraft/world/level/ChunkPos;"
	))
	private static ChunkPos difference_chunkPos(int x, int z, Operation<ChunkPos> original, @Share("w") LocalIntRef w) {
		ChunkPos pos = original.call(x, z);
		ChunkPos4.as(pos).setW(w.get());
		return pos;
	}

	@Redirect(method = "contains(Lnet/minecraft/world/level/ChunkPos;)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ChunkTrackingView;contains(II)Z"
	))
	default boolean contains(ChunkTrackingView instance, int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ChunkTrackingView4.as(instance).contains(x, z, ChunkPos4.as(pos).w());
	}

	@Overwrite
	@Deprecated
	default boolean contains(int x, int z) {
		throw Err4.arguments2("ChunkTrackingView#contains(ChunkPos)");
	}

	@Overwrite
	@Deprecated
	default boolean isInViewDistance(int chunkX, int chunkZ) {
		throw Err4.arguments2("ChunkTrackingView4#isInViewDistance");
	}

	@Overwrite
	@Deprecated
	static boolean isInViewDistance(int centerX, int centerZ, int viewDistance, int chunkX, int chunkZ) {
		throw Err4.arguments2("ChunkTrackingView4#isInViewDistance");
	}

	@Overwrite
	@Deprecated
	static boolean isWithinDistance(int centerX, int centerZ, int viewDistance, int chunkX, int chunkZ, boolean includeNeighbors) {
		throw Err4.arguments2("ChunkTrackingView4#isWithinDistance");
	}

	@Mixin(ChunkTrackingView.Positioned.class)
	class PositionedMixin implements ChunkTrackingView4, ChunkTrackingView4.Positioned {
		@Shadow
		@Final
		private ChunkPos center;

		@Shadow
		@Final
		private int viewDistance;

		@Override
		public int minW() {
			return ChunkPos4.as(this.center).w() - this.viewDistance - 1;
		}

		@Override
		public int maxW() {
			return ChunkPos4.as(this.center).w() + this.viewDistance + 1;
		}

		@WrapMethod(method = "squareIntersects")
		boolean squareIntersects(ChunkTrackingView.Positioned other, Operation<Boolean> original) {
			ChunkTrackingView4.Positioned other4 = ChunkTrackingView4.Positioned.as(other);
			return original.call(other) && this.minW() <= other4.maxW() && this.maxW() >= other4.minW();
		}

		@Overwrite
		@Deprecated
		public boolean contains(int chunkX, int chunkZ, boolean includeNeighbors) {
			throw Err4.arguments2("ChunkTrackingView#contains(ChunkPos)");
		}
		@Override
		public boolean contains(int chunkX, int chunkZ, int chunkW, boolean includeNeighbors) {
			return ChunkTrackingView4.isWithinDistance(
				this.center.x(), this.center.z(), ChunkPos4.as(this.center).w(),
				this.viewDistance,
				chunkX, chunkZ, chunkW,
				includeNeighbors
			);
		}

		@Definition(id = "z", local = @Local(type = int.class, name = "z"))
		@Definition(id = "minZ", method = "Lnet/minecraft/server/level/ChunkTrackingView$Positioned;minZ()I")
		@Expression("z = this.minZ()")
		@Inject(method = "forEach", at = @At("MIXINEXTRAS:EXPRESSION"))
		void forEach_w(Consumer<ChunkPos> consumer, CallbackInfo ci, @Share("w") LocalIntRef w) {
			w.set(this.minW());
		}
		// This does apply properly, IDE is lying. hold on, what?
		@Definition(id = "z", local = @Local(type = int.class, name = "z"))
		@Expression("z = z + @(1)")
		@ModifyExpressionValue(method = "forEach", at = @At("MIXINEXTRAS:EXPRESSION"))
		private int forEach_incrementW(int one, @Share("w") LocalIntRef w) {
			w.set(w.get() + 1);
			if (w.get() <= this.maxW()) return 0;
			w.set(this.minW());
			return 1;
		}
		@Redirect(method = "forEach", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ChunkTrackingView$Positioned;contains(II)Z"
		))
		boolean forEach_contains(ChunkTrackingView.Positioned instance, int x, int z, @Share("w") LocalIntRef w) {
			return ChunkTrackingView4.as(instance).contains(x, z, w.get());
		}
		@WrapOperation(method = "forEach", at = @At(
			value = "NEW",
			target = "(II)Lnet/minecraft/world/level/ChunkPos;"
		))
		ChunkPos forEach_chunkPos(int x, int z, Operation<ChunkPos> original, @Share("w") LocalIntRef w) {
			ChunkPos pos = original.call(x, z);
			ChunkPos4.as(pos).setW(w.get());
			return pos;
		}
	}
}
