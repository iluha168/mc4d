package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.client.renderer.Octree4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.structure.BoundingBox4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.renderer.Octree;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Octree.class)
abstract
class OctreeMixin implements Octree4 {
	@Shadow
	@Final
	public BlockPos cameraSectionCenter;

	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "NEW",
		target = "(IIIIII)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;"
	))
	BoundingBox init(
		BoundingBox bb,
		@Local(name = "cameraSectionOrigin") BlockPos cameraSectionOrigin,
		@Local(name = "distanceToBBEdgeInBlocks") int distanceToBBEdgeInBlocks,
		@Local(name = "boundingBoxSizeInSections") int boundingBoxSizeInSections
	) {
		final BoundingBox4 bb4 = (BoundingBox4) bb;
		final int minW = Vec4i.getW(cameraSectionOrigin) - distanceToBBEdgeInBlocks;
		final int maxW = minW + boundingBoxSizeInSections * 16 - 1;
		bb4.setMinW(minW);
		bb4.setMaxW(maxW);
		return bb;
	}

	@Overwrite
	@Deprecated
	private boolean isClose(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int closeDistance) {
		throw Err4.arguments3("Octree4#isClose");
	}
	@Override
	public boolean isClose(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW,
		int closeDistance
	) {
		final int cameraX = this.cameraSectionCenter.getX();
		final int cameraY = this.cameraSectionCenter.getY();
		final int cameraZ = this.cameraSectionCenter.getZ();
		final int cameraW = Vec4i.getW(this.cameraSectionCenter);
		return cameraX > minX - closeDistance
			&& cameraX < maxX + closeDistance
			&& cameraY > minY - closeDistance
			&& cameraY < maxY + closeDistance
			&& cameraZ > minZ - closeDistance
			&& cameraZ < maxZ + closeDistance
			&& cameraW > minW - closeDistance
			&& cameraW < maxW + closeDistance;
	}

	@SuppressWarnings("AddedEnumConstantsNamePattern")
	@Mixin(Octree.AxisSorting.class)
	private enum AxisSortingMixin implements Octree4.AxisSorting {
		XYWZ(8, 4, 1),
		XZWY(8, 1, 4),
		XWYZ(8, 2, 1),
		XWZY(8, 1, 2),
		YXWZ(4, 8, 1),
		YZWX(1, 8, 4),
		YWXZ(2, 8, 1),
		YWZX(1, 8, 2),
		ZXWY(4, 1, 8),
		ZYWX(1, 4, 8),
		ZWXY(2, 1, 8),
		ZWYX(1, 2, 8),
		WXYZ(4, 2, 1),
		WXZY(4, 1, 2),
		WYXZ(2, 4, 1),
		WYZX(1, 4, 2),
		WZXY(2, 1, 4),
		WZYX(1, 2, 4);

		@Unique private int wShift;

		@Shadow AxisSortingMixin(int xShift, int yShift, int zShift) {}

		@Shadow @Final private int xShift, yShift, zShift;

		@Inject(method = "<init>", at = @At("TAIL"))
		void init(String xShift, int yShift, int zShift, int par4, int par5, CallbackInfo ci) {
			this.wShift = 8+4+2+1 - this.xShift - this.yShift - this.zShift;
		}

		@Override
		public int wShift() {
			return this.wShift;
		}

		@Overwrite
		@Deprecated
		public static Octree.AxisSorting getAxisSorting(int absXDiff, int absYDiff, int absZDiff) {
			throw Err4.arguments3("Octree4.AxisSorting#getAxisSorting");
		}
	}

	@Mixin(Octree.Branch.class)
	private static final class BranchMixin {
		@Shadow @Final Octree this$0;
		@Shadow @Final private BoundingBox boundingBox;

		@Unique private int bbCenterW;
		@Unique private boolean cameraWDiffNegative;

		@ModifyConstant(method = "<init>", constant = @Constant(intValue = 8))
		private static int init_nodes(int childCount) {
			return childCount * 2;
		}
		@Redirect(method = "<init>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/Octree$AxisSorting;getAxisSorting(III)Lnet/minecraft/client/renderer/Octree$AxisSorting;"
		))
		Octree.AxisSorting init(int absXDiff, int absYDiff, int absZDiff) {
			final BoundingBox4 boundingBox4 = (BoundingBox4) this.boundingBox;
			this.bbCenterW = boundingBox4.minW() + boundingBox4.getWSpan() / 2;
			final int cameraWDiff = Vec4i.getW(this.this$0.cameraSectionCenter) - this.bbCenterW;
			this.cameraWDiffNegative = cameraWDiff < 0;
			return Octree4.AxisSorting.getAxisSorting(absXDiff, absYDiff, absZDiff, Math.abs(cameraWDiff));
		}

		@Redirect(method = "add", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/Octree$Branch;getNodeIndex(Lnet/minecraft/client/renderer/Octree$AxisSorting;ZZZ)I"
		))
		int add_getNodeIndex(
			Octree.AxisSorting sorting,
			boolean xDiffsOppositeSides, boolean yDiffsOppositeSides, boolean zDiffsOppositeSides,
			@Local(name = "sectionNode") long sectionNode,
			@Share("sectionWDiffNegative") LocalBooleanRef sectionWDiffNegative
		) {
			sectionWDiffNegative.set(SectionPos.sectionToBlockCoord(SectionPos4.w(sectionNode)) - this.bbCenterW < 0);
			final boolean wDiffsOppositeSides = sectionWDiffNegative.get() != this.cameraWDiffNegative;
			return getNodeIndex(sorting, xDiffsOppositeSides, yDiffsOppositeSides, zDiffsOppositeSides, wDiffsOppositeSides);
		}
		@Definition(id = "createChildBoundingBox", method = "Lnet/minecraft/client/renderer/Octree$Branch;createChildBoundingBox(ZZZ)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;")
		@Expression("this.createChildBoundingBox(?, ?, ?)")
		@Redirect(method = "add", at = @At("MIXINEXTRAS:EXPRESSION"))
		BoundingBox add_createChildBoundingBox(
			Octree.Branch instance,
			boolean sectionXDiffNegative, boolean sectionYDiffNegative, boolean sectionZDiffNegative,
			@Share("sectionWDiffNegative") LocalBooleanRef sectionWDiffNegative
		) {
			return this.createChildBoundingBox(sectionXDiffNegative, sectionYDiffNegative, sectionZDiffNegative, sectionWDiffNegative.get());
		}

		// Nah, no @Overwrite, private method copypasta.
		@Shadow
		private static int getNodeIndex(Octree.AxisSorting sorting, boolean xDiffsOppositeSides, boolean yDiffsOppositeSides, boolean zDiffsOppositeSides) {
			throw new UnsupportedOperationException("Implemented via mixin");
		}
		@Unique
		private static int getNodeIndex(
			Octree.AxisSorting sorting,
			boolean xDiffsOppositeSides, boolean yDiffsOppositeSides, boolean zDiffsOppositeSides, boolean wDiffsOppositeSides
		) {
			int index = BranchMixin.getNodeIndex(sorting, xDiffsOppositeSides, yDiffsOppositeSides, zDiffsOppositeSides);

			if (wDiffsOppositeSides) {
				index += Octree4.AxisSorting.as(sorting).wShift();
			}

			return index;
		}

		// Nah, no @Overwrite, private method copypasta.
		@Shadow
		private BoundingBox createChildBoundingBox(boolean sectionXDiffNegative, boolean sectionYDiffNegative, boolean sectionZDiffNegative) {
			throw new UnsupportedOperationException("Implemented via mixin");
		}
		@Unique
		private BoundingBox createChildBoundingBox(boolean sectionXDiffNegative, boolean sectionYDiffNegative, boolean sectionZDiffNegative, boolean sectionWDiffNegative) {
			final BoundingBox bb = this.createChildBoundingBox(sectionXDiffNegative, sectionYDiffNegative, sectionZDiffNegative);
			final BoundingBox4 bb4 = (BoundingBox4) bb;

			int minW;
			int maxW;
			if (sectionWDiffNegative) {
				minW = ((BoundingBox4) this.boundingBox).minW();
				maxW = this.bbCenterW - 1;
			} else {
				minW = this.bbCenterW;
				maxW = ((BoundingBox4) this.boundingBox).maxW();
			}
			bb4.setMinW(minW);
			bb4.setMaxW(maxW);

			return bb;
		}

		@Redirect(method = "visitNodes", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/Octree;isClose(DDDDDDI)Z"
		))
		boolean visitNodes(
			Octree instance,
			double minX, double minY, double minZ,
			double maxX, double maxY, double maxZ,
			int closeDistance
		) {
			final BoundingBox4 boundingBox4 = (BoundingBox4) this.boundingBox;
			return ((Octree4) instance).isClose(
				minX, minY, minZ, boundingBox4.minW(),
				maxX, maxY, maxZ, boundingBox4.maxW(),
				closeDistance
			);
		}

		@Redirect(method = "getAABB", at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"))
		AABB getAABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			final BoundingBox4 boundingBox4 = (BoundingBox4) this.boundingBox;
			return new AABB4(
				minX, minY, minZ, boundingBox4.minW(),
				maxX, maxY, maxZ, boundingBox4.maxW() + 1
			);
		}
	}

	@Mixin(targets = "net.minecraft.client.renderer.Octree$Leaf")
	private static final class LeafMixin {
		@Redirect(method = "visitNodes", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/Octree;isClose(DDDDDDI)Z"
		))
		boolean visitNodes(
			Octree instance,
			double minX, double minY, double minZ,
			double maxX, double maxY, double maxZ,
			int closeDistance,
			@Local(name = "boundingBox") AABB boundingBox
		) {
			final AABB4 boundingBox4 = (AABB4) boundingBox;
			return ((Octree4) instance).isClose(
				minX, minY, minZ, boundingBox4.minW,
				maxX, maxY, maxZ, boundingBox4.maxW,
				closeDistance
			);
		}
	}
}
