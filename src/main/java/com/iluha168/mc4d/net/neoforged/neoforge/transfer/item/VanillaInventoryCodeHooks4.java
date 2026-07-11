package com.iluha168.mc4d.net.neoforged.neoforge.transfer.item;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ContainerOrHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Implemented by {@link net.neoforged.neoforge.transfer.item.VanillaInventoryCodeHooks}.
 */
@SuppressWarnings("UnstableApiUsage")
public interface VanillaInventoryCodeHooks4 {
	static ContainerOrHandler getEntityContainerOrHandler(Level level, double x, double y, double z, double w, @Nullable Direction side) {
		List<Entity> list = level.getEntities(
			(Entity) null,
			new AABB4(
				x - 0.5D, y - 0.5D, z - 0.5D, w - 0.5D,
				x + 0.5D, y + 0.5D, z + 0.5D, w + 0.5D
			),
			entity -> {
				// Note: the isAlive check matches what vanilla does for hoppers in EntitySelector.CONTAINER_ENTITY_SELECTOR
				if (!entity.isAlive()) {
					return false;
				}
				return entity instanceof Container || entity.getCapability(Capabilities.Item.ENTITY_AUTOMATION, side) != null;
			});
		if (!list.isEmpty()) {
			var entity = list.get(level.getRandom().nextInt(list.size()));
			if (entity instanceof Container container) {
				return new ContainerOrHandler(container, null);
			}
			ResourceHandler<ItemResource> entityCap = entity.getCapability(Capabilities.Item.ENTITY_AUTOMATION, side);
			if (entityCap != null) { // Could be null even if it wasn't in the entity predicate above.
				return new ContainerOrHandler(null, entityCap);
			}
		}
		return ContainerOrHandler.EMPTY;
	}
}
