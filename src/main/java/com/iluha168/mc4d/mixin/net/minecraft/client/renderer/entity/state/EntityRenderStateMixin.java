package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.entity.state;

import com.iluha168.mc4d.client.renderer.entity.state.EntityRenderState4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(EntityRenderState.class)
class EntityRenderStateMixin implements EntityRenderState4 {
	@Unique	private double w;
	@Unique public boolean wNotSet;

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(CallbackInfo ci) {
		this.wNotSet = true;
	}

	@Override
	public double w() {
		if (this.wNotSet) throw Err4.field4missing("w");
		return this.w;
	}
	@Override
	public void setW(double w) {
		this.w = w;
		this.wNotSet = false;
	}

	@Redirect(method = "fillCrashReportCategory", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
	))
	String fillCrashReportCategory(Locale l, String format, Object[] args) {
		return String.format(l, format + ", %.2f", ArrayUtils.add(args, this.w()));
	}

	// TODO ShadowPiece when 4D renderer
}
