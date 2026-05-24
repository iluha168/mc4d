package com.iluha168.mc4d.mixin.position4;

import com.iluha168.mc4d.world.entity.Relative4;
import net.minecraft.world.entity.Relative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Relative.class)
public enum RelativeMixin implements Relative4 {
	// Surely no other mod would modify this, right? Sorry
	@SuppressWarnings("AddedEnumConstantsNamePattern") W(9);

	@Shadow
	RelativeMixin(int bit) {}
}
