package com.iluha168.mc4d.core;

import org.jetbrains.annotations.Contract;

public interface Position4i {
	int getX();
	int getY();
	int getZ();
	@Contract(pure = true)
	int getW();
}
