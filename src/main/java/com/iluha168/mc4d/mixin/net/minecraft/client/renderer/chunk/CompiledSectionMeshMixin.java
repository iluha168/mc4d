package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.client.renderer.chunk.CompiledSectionMesh4;
import com.iluha168.mc4d.client.renderer.chunk.SectionCompiler4;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.TranslucencyPointOfView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompiledSectionMesh.class)
class CompiledSectionMeshMixin implements CompiledSectionMesh4 {
	@Unique private float[] wBoundaries = NO_W_BOUNDARIES;

	@Override
	public float[] wBoundaries() {
		return this.wBoundaries;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(TranslucencyPointOfView translucencyPointOfView, SectionCompiler.Results results, CallbackInfo ci) {
		this.wBoundaries = SectionCompiler4.Results.as(results).wBoundaries();
	}
}
