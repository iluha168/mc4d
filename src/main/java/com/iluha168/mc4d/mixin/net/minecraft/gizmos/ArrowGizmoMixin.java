package com.iluha168.mc4d.mixin.net.minecraft.gizmos;

import net.minecraft.gizmos.ArrowGizmo;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowGizmo.class)
class ArrowGizmoMixin {
	@Mutable
	@Shadow
	@Final
	private Vec3 start;

	@Mutable
	@Shadow
	@Final
	private Vec3 end;

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(Vec3 start, Vec3 end, int color, float width, CallbackInfo ci) {
		// TODO Remove when 4D renderer
		this.start = new Vec3(start.x, start.y, start.z);
		this.end = new Vec3(end.x, end.y, end.z);
	}
}
