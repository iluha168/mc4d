package com.iluha168.mc4d.mixin.net.neoforged.neoforge.transfer.item;

import com.iluha168.mc4d.net.neoforged.neoforge.transfer.item.VanillaInventoryCodeHooks4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ContainerOrHandler;
import net.neoforged.neoforge.transfer.item.VanillaInventoryCodeHooks;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SuppressWarnings("UnstableApiUsage")
@Mixin(VanillaInventoryCodeHooks.class)
class VanillaInventoryCodeHooksMixin implements VanillaInventoryCodeHooks4 {
	@Overwrite
	@Deprecated
	public static ContainerOrHandler getEntityContainerOrHandler(Level level, double x, double y, double z, @Nullable Direction side) {
		throw Err4.arguments3("VanillaInventoryCodeHooks4#getEntityContainerOrHandler");
	}
}
