package com.iluha168.mc4d.world.phys;

import org.jetbrains.annotations.Contract;

public interface IAABB4 {
	@Contract(pure = true)
	double minW();
	@Contract(pure = true)
	double maxW();
}
