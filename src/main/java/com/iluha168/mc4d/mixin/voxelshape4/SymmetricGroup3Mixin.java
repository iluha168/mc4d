package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.math.SymmetricGroup4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.math.SymmetricGroup3;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@SuppressWarnings("AddedEnumConstantsNamePattern")
@Mixin(SymmetricGroup3.class)
public enum SymmetricGroup3Mixin implements SymmetricGroup4 {
	// p3 = 2
	P1243(0, 1, 3),
	P2143(1, 0, 3),
	P1423(0, 3, 1),
	P4123(3, 0, 1),
	P2413(1, 3, 0),
	P4213(3, 1, 0),
	// p3 = 1
	P1342(0, 2, 3),
	P3142(2, 0, 3),
	P1432(0, 3, 2),
	P4132(3, 0, 2),
	P3412(2, 3, 0),
	P4312(3, 2, 0),
	// p3 = 0
	P2341(1, 2, 3),
	P3241(2, 1, 3),
	P2431(1, 3, 2),
	P4231(3, 1, 2),
	P3421(2, 3, 1),
	P4321(3, 2, 1);

	@Final @Shadow private int p0;
	@Final @Shadow private int p1;
	@Final @Shadow private int p2;
	@Unique private int p3;
	@Unique	private Matrix4f transformation4;

	@Shadow
	SymmetricGroup3Mixin(int p0, int p1, int p2) {}

	@Shadow
	public int permute(int i) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true)
	private static void addP3AndProperCayleyTable(CallbackInfoReturnable<SymmetricGroup3[][]> cir) {
		SymmetricGroup3Mixin[] values = values();
		// Adding p3 to all enum members.
		for (SymmetricGroup3Mixin group : values) {
			group.p3 = 3; // 3 is the default for vanilla and other mods
		}
		for (SymmetricGroup3Mixin group : new SymmetricGroup3Mixin[] { P1243, P2143, P1423, P4123, P2413, P4213 }) {
			group.p3 = 2;
		}
		for (SymmetricGroup3Mixin group : new SymmetricGroup3Mixin[] { P1342, P3142, P1432, P4132, P3412, P4312 }) {
			group.p3 = 1;
		}
		for (SymmetricGroup3Mixin group : new SymmetricGroup3Mixin[] { P2341, P3241, P2431, P4231, P3421, P4321 }) {
			group.p3 = 0;
		}

		// Actually doing what the lambda is supposed to do - generating CAYLEY_TABLE
		SymmetricGroup3[][] table = new SymmetricGroup3[values.length][values.length];

		for (SymmetricGroup3Mixin first : values) {
			// This is where transformation4 is first assigned too
			first.transformation4 = new Matrix4f()
				.zero()
				.set(first.permute(0), 0, 1.0F)
				.set(first.permute(1), 1, 1.0F)
				.set(first.permute(2), 2, 1.0F)
				.set(first.permute(3), 3, 1.0F);

			for (SymmetricGroup3Mixin second : values) {
				int p0 = first.permute(second.p0);
				int p1 = first.permute(second.p1);
				int p2 = first.permute(second.p2);
				int p3 = first.permute(second.p3); // <- this is the change
				SymmetricGroup3Mixin result = Arrays
					.stream(values)
					.filter(p -> p.p0 == p0 && p.p1 == p1 && p.p2 == p2 && p.p3 == p3) // also filter change
					.findFirst()
					.get();
				table[first.ordinal()][second.ordinal()] = (SymmetricGroup3) (Object) result;
			}
		}

		cir.setReturnValue(table);
	}

	@WrapMethod(method = "permute")
	int permute(int i, Operation<Integer> original) {
		return i == 3 ? this.p3 : original.call(i);
	}

	@Overwrite
	public Vector3f permuteVector(Vector3f v) {
		throw Err4.return3(null);
	}

	@Overwrite
	public Vector3i permuteVector(Vector3i v) {
		throw Err4.arguments3("SymmetricGroup4#permutedVector");
	}

	@Override
	public void permuteVector(Vector4i v) {
		int v0 = v.get(this.p0);
		int v1 = v.get(this.p1);
		int v2 = v.get(this.p2);
		int v3 = v.get(this.p3);
		v.set(v0, v1, v2, v3);
	}

	@Overwrite
	public Matrix3fc transformation() {
		throw Err4.return3("SymmetricGroup4#transformation4");
	}

	@Override
	public Matrix4fc transformation4() {
		return this.transformation4;
	}
}
