package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.world.phys.IAABB4;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import static com.iluha168.mc4d.world.phys.AABB4.EPSILON;

@Mixin(AABB.class)
public class AABBMixin implements IAABB4 {
	@Override
	public double minW() {
		return 0;
	}

	@Override
	public double maxW() {
		return EPSILON;
	}
}
