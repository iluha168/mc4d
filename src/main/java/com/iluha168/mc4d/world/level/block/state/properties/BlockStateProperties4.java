package com.iluha168.mc4d.world.level.block.state.properties;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

/**
 * Implemented by {@link net.minecraft.world.level.block.state.properties.BlockStateProperties}.
 */
public interface BlockStateProperties4 {
	BooleanProperty ANA = BooleanProperty.create("ana");
	BooleanProperty KATA = BooleanProperty.create("kata");

	EnumProperty<RedstoneSide> ANA_REDSTONE = EnumProperty.create("ana", RedstoneSide.class);
	EnumProperty<RedstoneSide> KATA_REDSTONE = EnumProperty.create("kata", RedstoneSide.class);
}
