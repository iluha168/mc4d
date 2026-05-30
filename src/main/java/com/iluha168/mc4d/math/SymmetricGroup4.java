package com.iluha168.mc4d.math;

import com.mojang.math.SymmetricGroup3;
import org.joml.Matrix4fc;
import org.joml.Vector4i;

/**
 * <b>All {@link com.mojang.math.SymmetricGroup3}</b> instances implement {@link SymmetricGroup4}.
 * Symmetric group basically means a shuffle of axes. There are n! shuffles in n dimensions.
 * Use {@link SymmetricGroup4#as} for type-casting.
 */
public interface SymmetricGroup4 {
	SymmetricGroup3 P1234 = SymmetricGroup3.P123;
	SymmetricGroup3 P2134 = SymmetricGroup3.P213;
	SymmetricGroup3 P1324 = SymmetricGroup3.P132;
	SymmetricGroup3 P3124 = SymmetricGroup3.P312;
	SymmetricGroup3 P2314 = SymmetricGroup3.P231;
	SymmetricGroup3 P3214 = SymmetricGroup3.P321;
	SymmetricGroup3 P1243 = SymmetricGroup3.valueOf("P1243");
	SymmetricGroup3 P2143 = SymmetricGroup3.valueOf("P2143");
	SymmetricGroup3 P1423 = SymmetricGroup3.valueOf("P1423");
	SymmetricGroup3 P4123 = SymmetricGroup3.valueOf("P4123");
	SymmetricGroup3 P2413 = SymmetricGroup3.valueOf("P2413");
	SymmetricGroup3 P4213 = SymmetricGroup3.valueOf("P4213");
	SymmetricGroup3 P1342 = SymmetricGroup3.valueOf("P1342");
	SymmetricGroup3 P3142 = SymmetricGroup3.valueOf("P3142");
	SymmetricGroup3 P1432 = SymmetricGroup3.valueOf("P1432");
	SymmetricGroup3 P4132 = SymmetricGroup3.valueOf("P4132");
	SymmetricGroup3 P3412 = SymmetricGroup3.valueOf("P3412");
	SymmetricGroup3 P4312 = SymmetricGroup3.valueOf("P4312");
	SymmetricGroup3 P2341 = SymmetricGroup3.valueOf("P2341");
	SymmetricGroup3 P3241 = SymmetricGroup3.valueOf("P3241");
	SymmetricGroup3 P2431 = SymmetricGroup3.valueOf("P2431");
	SymmetricGroup3 P4231 = SymmetricGroup3.valueOf("P4231");
	SymmetricGroup3 P3421 = SymmetricGroup3.valueOf("P3421");
	SymmetricGroup3 P4321 = SymmetricGroup3.valueOf("P4321");

	static SymmetricGroup4 as(SymmetricGroup3 group) {
		return (SymmetricGroup4) (Object) group;
	}

	void permuteVector(Vector4i v);

	Matrix4fc transformation4();
}
