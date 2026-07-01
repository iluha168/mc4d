package com.iluha168.mc4d.mixin.net.minecraft.client.gui.components.debug;

import com.iluha168.mc4d.MC4DClient;
import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenEntries.class)
class DebugScreenEntriesMixin {
	@Shadow
	private static Identifier register(Identifier identifier, DebugScreenEntry entry) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void register_neighbouringSliceRenderer(CallbackInfo ci) {
		register(MC4DClient.NEIGHBOURING_SLICE_BLOCK_RENDERER, new DebugEntryNoop());
		register(MC4DClient.NO_BLOCK_MODEL_RENDERER, new DebugEntryNoop());
		register(MC4DClient.NEIGHBOURING_SLICE_PARTICLE_RENDERER, new DebugEntryNoop());
		register(MC4DClient.NEIGHBOURING_SLICE_ENTITY_RENDERER, new DebugEntryNoop());
	}
}
