package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.core.Position4i;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3i.class)
public abstract class Vec3iMixin implements Position4i {
	@Override
	@Shadow
	public abstract int getX();

	@Override
	@Shadow
	public abstract int getY();

	@Override
	@Shadow
	public abstract int getZ();

	@Override
	public int getW() {
		return 0;
	}
}
