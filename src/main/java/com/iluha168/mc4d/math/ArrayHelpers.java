package com.iluha168.mc4d.math;

import org.jspecify.annotations.NonNull;

public interface ArrayHelpers {
	static double[] addAll(double @NonNull[] array1, double @NonNull ... array2) {
		double[] joinedArray = new double[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
}
