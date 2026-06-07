package com.iluha168.mc4d.world.level.block.state.properties;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public class BlockStateProperties4 {
	public static final BooleanProperty ANA = BooleanProperty.create("ana");
	public static final BooleanProperty KATA = BooleanProperty.create("kata");

	public static final EnumProperty<RedstoneSide> ANA_REDSTONE = EnumProperty.create("ana", RedstoneSide.class);
	public static final EnumProperty<RedstoneSide> KATA_REDSTONE = EnumProperty.create("kata", RedstoneSide.class);
}
