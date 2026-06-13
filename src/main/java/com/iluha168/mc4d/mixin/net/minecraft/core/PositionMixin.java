package com.iluha168.mc4d.mixin.net.minecraft.core;

import com.iluha168.mc4d.core.Position4;
import net.minecraft.core.Position;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Position.class)
public interface PositionMixin extends Position4 {
	default double w() {
		return 0;
	}
}
