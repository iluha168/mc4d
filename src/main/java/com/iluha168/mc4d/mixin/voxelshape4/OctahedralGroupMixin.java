package com.iluha168.mc4d.mixin.voxelshape4;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.math.OctahedralGroup4;
import com.iluha168.mc4d.math.SymmetricGroup4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.math.OctahedralGroup;
import com.mojang.math.SymmetricGroup3;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("AddedEnumConstantsNamePattern")
@Mixin(OctahedralGroup.class)
public enum OctahedralGroupMixin implements OctahedralGroup4 {
	// IDENTITY
	// INVERT_Z
	// INVERT_Y
	// ROT_180_FACE_YZ
	// INVERT_X
	// ROT_180_FACE_XZ
	// ROT_180_FACE_XY
	// INVERSION
	// SWAP_XY
	// ROT_180_EDGE_XY_POS
	// ROT_90_Z_NEG
	// ROT_90_REF_Z_NEG
	// ROT_90_Z_POS
	// ROT_90_REF_Z_POS
	// SWAP_NEG_XY
	// ROT_180_EDGE_XY_NEG
	// SWAP_YZ
	// ROT_90_X_NEG
	// ROT_90_X_POS
	// SWAP_NEG_YZ
	// ROT_180_EDGE_YZ_POS
	// ROT_90_REF_X_NEG
	// ROT_90_REF_X_POS
	// ROT_180_EDGE_YZ_NEG
	// ROT_120_PPP
	// ROT_60_REF_NPP
	// ROT_60_REF_PPN
	// ROT_120_NPN
	// ROT_60_REF_PNP
	// ROT_120_NNP
	// ROT_120_PNN
	// ROT_60_REF_NNN
	// ROT_120_NNN
	// ROT_60_REF_NPN
	// ROT_60_REF_PNN
	// ROT_120_PPN
	// ROT_60_REF_NNP
	// ROT_120_NPP
	// ROT_120_PNP
	// ROT_60_REF_PPP
	// SWAP_XZ
	// ROT_90_Y_POS
	// ROT_180_EDGE_XZ_POS
	// ROT_90_REF_Y_POS
	// ROT_90_Y_NEG
	// SWAP_NEG_XZ
	// ROT_90_REF_Y_NEG
	// ROT_180_EDGE_XZ_NEG
	P1243_INVERT_____("p1243_invert_____", SymmetricGroup4.P1243, false, false, false),
	P1243_INVERT___Z_("p1243_invert___z_", SymmetricGroup4.P1243, false, false, true ),
	P1243_INVERT__Y__("p1243_invert__y__", SymmetricGroup4.P1243, false, true , false),
	P1243_INVERT__YZ_("p1243_invert__yz_", SymmetricGroup4.P1243, false, true , true ),
	P1243_INVERT_X___("p1243_invert_x___", SymmetricGroup4.P1243, true , false, false),
	P1243_INVERT_X_Z_("p1243_invert_x_z_", SymmetricGroup4.P1243, true , false, true ),
	P1243_INVERT_XY__("p1243_invert_xy__", SymmetricGroup4.P1243, true , true , false),
	P1243_INVERT_XYZ_("p1243_invert_xyz_", SymmetricGroup4.P1243, true , true , true ),
	P2143_INVERT_____("p2143_invert_____", SymmetricGroup4.P2143, false, false, false),
	P2143_INVERT___Z_("p2143_invert___z_", SymmetricGroup4.P2143, false, false, true ),
	P2143_INVERT__Y__("p2143_invert__y__", SymmetricGroup4.P2143, false, true , false),
	P2143_INVERT__YZ_("p2143_invert__yz_", SymmetricGroup4.P2143, false, true , true ),
	P2143_INVERT_X___("p2143_invert_x___", SymmetricGroup4.P2143, true , false, false),
	P2143_INVERT_X_Z_("p2143_invert_x_z_", SymmetricGroup4.P2143, true , false, true ),
	P2143_INVERT_XY__("p2143_invert_xy__", SymmetricGroup4.P2143, true , true , false),
	P2143_INVERT_XYZ_("p2143_invert_xyz_", SymmetricGroup4.P2143, true , true , true ),
	P1423_INVERT_____("p1423_invert_____", SymmetricGroup4.P1423, false, false, false),
	P1423_INVERT___Z_("p1423_invert___z_", SymmetricGroup4.P1423, false, false, true ),
	P1423_INVERT__Y__("p1423_invert__y__", SymmetricGroup4.P1423, false, true , false),
	P1423_INVERT__YZ_("p1423_invert__yz_", SymmetricGroup4.P1423, false, true , true ),
	P1423_INVERT_X___("p1423_invert_x___", SymmetricGroup4.P1423, true , false, false),
	P1423_INVERT_X_Z_("p1423_invert_x_z_", SymmetricGroup4.P1423, true , false, true ),
	P1423_INVERT_XY__("p1423_invert_xy__", SymmetricGroup4.P1423, true , true , false),
	P1423_INVERT_XYZ_("p1423_invert_xyz_", SymmetricGroup4.P1423, true , true , true ),
	P4123_INVERT_____("p4123_invert_____", SymmetricGroup4.P4123, false, false, false),
	P4123_INVERT___Z_("p4123_invert___z_", SymmetricGroup4.P4123, false, false, true ),
	P4123_INVERT__Y__("p4123_invert__y__", SymmetricGroup4.P4123, false, true , false),
	P4123_INVERT__YZ_("p4123_invert__yz_", SymmetricGroup4.P4123, false, true , true ),
	P4123_INVERT_X___("p4123_invert_x___", SymmetricGroup4.P4123, true , false, false),
	P4123_INVERT_X_Z_("p4123_invert_x_z_", SymmetricGroup4.P4123, true , false, true ),
	P4123_INVERT_XY__("p4123_invert_xy__", SymmetricGroup4.P4123, true , true , false),
	P4123_INVERT_XYZ_("p4123_invert_xyz_", SymmetricGroup4.P4123, true , true , true ),
	P2413_INVERT_____("p2413_invert_____", SymmetricGroup4.P2413, false, false, false),
	P2413_INVERT___Z_("p2413_invert___z_", SymmetricGroup4.P2413, false, false, true ),
	P2413_INVERT__Y__("p2413_invert__y__", SymmetricGroup4.P2413, false, true , false),
	P2413_INVERT__YZ_("p2413_invert__yz_", SymmetricGroup4.P2413, false, true , true ),
	P2413_INVERT_X___("p2413_invert_x___", SymmetricGroup4.P2413, true , false, false),
	P2413_INVERT_X_Z_("p2413_invert_x_z_", SymmetricGroup4.P2413, true , false, true ),
	P2413_INVERT_XY__("p2413_invert_xy__", SymmetricGroup4.P2413, true , true , false),
	P2413_INVERT_XYZ_("p2413_invert_xyz_", SymmetricGroup4.P2413, true , true , true ),
	P4213_INVERT_____("p4213_invert_____", SymmetricGroup4.P4213, false, false, false),
	P4213_INVERT___Z_("p4213_invert___z_", SymmetricGroup4.P4213, false, false, true ),
	P4213_INVERT__Y__("p4213_invert__y__", SymmetricGroup4.P4213, false, true , false),
	P4213_INVERT__YZ_("p4213_invert__yz_", SymmetricGroup4.P4213, false, true , true ),
	P4213_INVERT_X___("p4213_invert_x___", SymmetricGroup4.P4213, true , false, false),
	P4213_INVERT_X_Z_("p4213_invert_x_z_", SymmetricGroup4.P4213, true , false, true ),
	P4213_INVERT_XY__("p4213_invert_xy__", SymmetricGroup4.P4213, true , true , false),
	P4213_INVERT_XYZ_("p4213_invert_xyz_", SymmetricGroup4.P4213, true , true , true ),
	P1342_INVERT_____("p1342_invert_____", SymmetricGroup4.P1342, false, false, false),
	P1342_INVERT___Z_("p1342_invert___z_", SymmetricGroup4.P1342, false, false, true ),
	P1342_INVERT__Y__("p1342_invert__y__", SymmetricGroup4.P1342, false, true , false),
	P1342_INVERT__YZ_("p1342_invert__yz_", SymmetricGroup4.P1342, false, true , true ),
	P1342_INVERT_X___("p1342_invert_x___", SymmetricGroup4.P1342, true , false, false),
	P1342_INVERT_X_Z_("p1342_invert_x_z_", SymmetricGroup4.P1342, true , false, true ),
	P1342_INVERT_XY__("p1342_invert_xy__", SymmetricGroup4.P1342, true , true , false),
	P1342_INVERT_XYZ_("p1342_invert_xyz_", SymmetricGroup4.P1342, true , true , true ),
	P3142_INVERT_____("p3142_invert_____", SymmetricGroup4.P3142, false, false, false),
	P3142_INVERT___Z_("p3142_invert___z_", SymmetricGroup4.P3142, false, false, true ),
	P3142_INVERT__Y__("p3142_invert__y__", SymmetricGroup4.P3142, false, true , false),
	P3142_INVERT__YZ_("p3142_invert__yz_", SymmetricGroup4.P3142, false, true , true ),
	P3142_INVERT_X___("p3142_invert_x___", SymmetricGroup4.P3142, true , false, false),
	P3142_INVERT_X_Z_("p3142_invert_x_z_", SymmetricGroup4.P3142, true , false, true ),
	P3142_INVERT_XY__("p3142_invert_xy__", SymmetricGroup4.P3142, true , true , false),
	P3142_INVERT_XYZ_("p3142_invert_xyz_", SymmetricGroup4.P3142, true , true , true ),
	P1432_INVERT_____("p1432_invert_____", SymmetricGroup4.P1432, false, false, false),
	P1432_INVERT___Z_("p1432_invert___z_", SymmetricGroup4.P1432, false, false, true ),
	P1432_INVERT__Y__("p1432_invert__y__", SymmetricGroup4.P1432, false, true , false),
	P1432_INVERT__YZ_("p1432_invert__yz_", SymmetricGroup4.P1432, false, true , true ),
	P1432_INVERT_X___("p1432_invert_x___", SymmetricGroup4.P1432, true , false, false),
	P1432_INVERT_X_Z_("p1432_invert_x_z_", SymmetricGroup4.P1432, true , false, true ),
	P1432_INVERT_XY__("p1432_invert_xy__", SymmetricGroup4.P1432, true , true , false),
	P1432_INVERT_XYZ_("p1432_invert_xyz_", SymmetricGroup4.P1432, true , true , true ),
	P4132_INVERT_____("p4132_invert_____", SymmetricGroup4.P4132, false, false, false),
	P4132_INVERT___Z_("p4132_invert___z_", SymmetricGroup4.P4132, false, false, true ),
	P4132_INVERT__Y__("p4132_invert__y__", SymmetricGroup4.P4132, false, true , false),
	P4132_INVERT__YZ_("p4132_invert__yz_", SymmetricGroup4.P4132, false, true , true ),
	P4132_INVERT_X___("p4132_invert_x___", SymmetricGroup4.P4132, true , false, false),
	P4132_INVERT_X_Z_("p4132_invert_x_z_", SymmetricGroup4.P4132, true , false, true ),
	P4132_INVERT_XY__("p4132_invert_xy__", SymmetricGroup4.P4132, true , true , false),
	P4132_INVERT_XYZ_("p4132_invert_xyz_", SymmetricGroup4.P4132, true , true , true ),
	P3412_INVERT_____("p3412_invert_____", SymmetricGroup4.P3412, false, false, false),
	P3412_INVERT___Z_("p3412_invert___z_", SymmetricGroup4.P3412, false, false, true ),
	P3412_INVERT__Y__("p3412_invert__y__", SymmetricGroup4.P3412, false, true , false),
	P3412_INVERT__YZ_("p3412_invert__yz_", SymmetricGroup4.P3412, false, true , true ),
	P3412_INVERT_X___("p3412_invert_x___", SymmetricGroup4.P3412, true , false, false),
	P3412_INVERT_X_Z_("p3412_invert_x_z_", SymmetricGroup4.P3412, true , false, true ),
	P3412_INVERT_XY__("p3412_invert_xy__", SymmetricGroup4.P3412, true , true , false),
	P3412_INVERT_XYZ_("p3412_invert_xyz_", SymmetricGroup4.P3412, true , true , true ),
	P4312_INVERT_____("p4312_invert_____", SymmetricGroup4.P4312, false, false, false),
	P4312_INVERT___Z_("p4312_invert___z_", SymmetricGroup4.P4312, false, false, true ),
	P4312_INVERT__Y__("p4312_invert__y__", SymmetricGroup4.P4312, false, true , false),
	P4312_INVERT__YZ_("p4312_invert__yz_", SymmetricGroup4.P4312, false, true , true ),
	P4312_INVERT_X___("p4312_invert_x___", SymmetricGroup4.P4312, true , false, false),
	P4312_INVERT_X_Z_("p4312_invert_x_z_", SymmetricGroup4.P4312, true , false, true ),
	P4312_INVERT_XY__("p4312_invert_xy__", SymmetricGroup4.P4312, true , true , false),
	P4312_INVERT_XYZ_("p4312_invert_xyz_", SymmetricGroup4.P4312, true , true , true ),
	P2341_INVERT_____("p2341_invert_____", SymmetricGroup4.P2341, false, false, false),
	P2341_INVERT___Z_("p2341_invert___z_", SymmetricGroup4.P2341, false, false, true ),
	P2341_INVERT__Y__("p2341_invert__y__", SymmetricGroup4.P2341, false, true , false),
	P2341_INVERT__YZ_("p2341_invert__yz_", SymmetricGroup4.P2341, false, true , true ),
	P2341_INVERT_X___("p2341_invert_x___", SymmetricGroup4.P2341, true , false, false),
	P2341_INVERT_X_Z_("p2341_invert_x_z_", SymmetricGroup4.P2341, true , false, true ),
	P2341_INVERT_XY__("p2341_invert_xy__", SymmetricGroup4.P2341, true , true , false),
	P2341_INVERT_XYZ_("p2341_invert_xyz_", SymmetricGroup4.P2341, true , true , true ),
	P3241_INVERT_____("p3241_invert_____", SymmetricGroup4.P3241, false, false, false),
	P3241_INVERT___Z_("p3241_invert___z_", SymmetricGroup4.P3241, false, false, true ),
	P3241_INVERT__Y__("p3241_invert__y__", SymmetricGroup4.P3241, false, true , false),
	P3241_INVERT__YZ_("p3241_invert__yz_", SymmetricGroup4.P3241, false, true , true ),
	P3241_INVERT_X___("p3241_invert_x___", SymmetricGroup4.P3241, true , false, false),
	P3241_INVERT_X_Z_("p3241_invert_x_z_", SymmetricGroup4.P3241, true , false, true ),
	P3241_INVERT_XY__("p3241_invert_xy__", SymmetricGroup4.P3241, true , true , false),
	P3241_INVERT_XYZ_("p3241_invert_xyz_", SymmetricGroup4.P3241, true , true , true ),
	P2431_INVERT_____("p2431_invert_____", SymmetricGroup4.P2431, false, false, false),
	P2431_INVERT___Z_("p2431_invert___z_", SymmetricGroup4.P2431, false, false, true ),
	P2431_INVERT__Y__("p2431_invert__y__", SymmetricGroup4.P2431, false, true , false),
	P2431_INVERT__YZ_("p2431_invert__yz_", SymmetricGroup4.P2431, false, true , true ),
	P2431_INVERT_X___("p2431_invert_x___", SymmetricGroup4.P2431, true , false, false),
	P2431_INVERT_X_Z_("p2431_invert_x_z_", SymmetricGroup4.P2431, true , false, true ),
	P2431_INVERT_XY__("p2431_invert_xy__", SymmetricGroup4.P2431, true , true , false),
	P2431_INVERT_XYZ_("p2431_invert_xyz_", SymmetricGroup4.P2431, true , true , true ),
	P4231_INVERT_____("p4231_invert_____", SymmetricGroup4.P4231, false, false, false),
	P4231_INVERT___Z_("p4231_invert___z_", SymmetricGroup4.P4231, false, false, true ),
	P4231_INVERT__Y__("p4231_invert__y__", SymmetricGroup4.P4231, false, true , false),
	P4231_INVERT__YZ_("p4231_invert__yz_", SymmetricGroup4.P4231, false, true , true ),
	P4231_INVERT_X___("p4231_invert_x___", SymmetricGroup4.P4231, true , false, false),
	P4231_INVERT_X_Z_("p4231_invert_x_z_", SymmetricGroup4.P4231, true , false, true ),
	P4231_INVERT_XY__("p4231_invert_xy__", SymmetricGroup4.P4231, true , true , false),
	P4231_INVERT_XYZ_("p4231_invert_xyz_", SymmetricGroup4.P4231, true , true , true ),
	P3421_INVERT_____("p3421_invert_____", SymmetricGroup4.P3421, false, false, false),
	P3421_INVERT___Z_("p3421_invert___z_", SymmetricGroup4.P3421, false, false, true ),
	P3421_INVERT__Y__("p3421_invert__y__", SymmetricGroup4.P3421, false, true , false),
	P3421_INVERT__YZ_("p3421_invert__yz_", SymmetricGroup4.P3421, false, true , true ),
	P3421_INVERT_X___("p3421_invert_x___", SymmetricGroup4.P3421, true , false, false),
	P3421_INVERT_X_Z_("p3421_invert_x_z_", SymmetricGroup4.P3421, true , false, true ),
	P3421_INVERT_XY__("p3421_invert_xy__", SymmetricGroup4.P3421, true , true , false),
	P3421_INVERT_XYZ_("p3421_invert_xyz_", SymmetricGroup4.P3421, true , true , true ),
	P4321_INVERT_____("p4321_invert_____", SymmetricGroup4.P4321, false, false, false),
	P4321_INVERT___Z_("p4321_invert___z_", SymmetricGroup4.P4321, false, false, true ),
	P4321_INVERT__Y__("p4321_invert__y__", SymmetricGroup4.P4321, false, true , false),
	P4321_INVERT__YZ_("p4321_invert__yz_", SymmetricGroup4.P4321, false, true , true ),
	P4321_INVERT_X___("p4321_invert_x___", SymmetricGroup4.P4321, true , false, false),
	P4321_INVERT_X_Z_("p4321_invert_x_z_", SymmetricGroup4.P4321, true , false, true ),
	P4321_INVERT_XY__("p4321_invert_xy__", SymmetricGroup4.P4321, true , true , false),
	P4321_INVERT_XYZ_("p4321_invert_xyz_", SymmetricGroup4.P4321, true , true , true ),
	P1234_INVERT____W("p1234_invert____w", SymmetricGroup4.P1234, false, false, false),
	P1234_INVERT___ZW("p1234_invert___zw", SymmetricGroup4.P1234, false, false, true ),
	P1234_INVERT__Y_W("p1234_invert__y_w", SymmetricGroup4.P1234, false, true , false),
	P1234_INVERT__YZW("p1234_invert__yzw", SymmetricGroup4.P1234, false, true , true ),
	P1234_INVERT_X__W("p1234_invert_x__w", SymmetricGroup4.P1234, true , false, false),
	P1234_INVERT_X_ZW("p1234_invert_x_zw", SymmetricGroup4.P1234, true , false, true ),
	P1234_INVERT_XY_W("p1234_invert_xy_w", SymmetricGroup4.P1234, true , true , false),
	P1234_INVERT_XYZW("p1234_invert_xyzw", SymmetricGroup4.P1234, true , true , true ),
	P2134_INVERT____W("p2134_invert____w", SymmetricGroup4.P2134, false, false, false),
	P2134_INVERT___ZW("p2134_invert___zw", SymmetricGroup4.P2134, false, false, true ),
	P2134_INVERT__Y_W("p2134_invert__y_w", SymmetricGroup4.P2134, false, true , false),
	P2134_INVERT__YZW("p2134_invert__yzw", SymmetricGroup4.P2134, false, true , true ),
	P2134_INVERT_X__W("p2134_invert_x__w", SymmetricGroup4.P2134, true , false, false),
	P2134_INVERT_X_ZW("p2134_invert_x_zw", SymmetricGroup4.P2134, true , false, true ),
	P2134_INVERT_XY_W("p2134_invert_xy_w", SymmetricGroup4.P2134, true , true , false),
	P2134_INVERT_XYZW("p2134_invert_xyzw", SymmetricGroup4.P2134, true , true , true ),
	P1324_INVERT____W("p1324_invert____w", SymmetricGroup4.P1324, false, false, false),
	P1324_INVERT___ZW("p1324_invert___zw", SymmetricGroup4.P1324, false, false, true ),
	P1324_INVERT__Y_W("p1324_invert__y_w", SymmetricGroup4.P1324, false, true , false),
	P1324_INVERT__YZW("p1324_invert__yzw", SymmetricGroup4.P1324, false, true , true ),
	P1324_INVERT_X__W("p1324_invert_x__w", SymmetricGroup4.P1324, true , false, false),
	P1324_INVERT_X_ZW("p1324_invert_x_zw", SymmetricGroup4.P1324, true , false, true ),
	P1324_INVERT_XY_W("p1324_invert_xy_w", SymmetricGroup4.P1324, true , true , false),
	P1324_INVERT_XYZW("p1324_invert_xyzw", SymmetricGroup4.P1324, true , true , true ),
	P3124_INVERT____W("p3124_invert____w", SymmetricGroup4.P3124, false, false, false),
	P3124_INVERT___ZW("p3124_invert___zw", SymmetricGroup4.P3124, false, false, true ),
	P3124_INVERT__Y_W("p3124_invert__y_w", SymmetricGroup4.P3124, false, true , false),
	P3124_INVERT__YZW("p3124_invert__yzw", SymmetricGroup4.P3124, false, true , true ),
	P3124_INVERT_X__W("p3124_invert_x__w", SymmetricGroup4.P3124, true , false, false),
	P3124_INVERT_X_ZW("p3124_invert_x_zw", SymmetricGroup4.P3124, true , false, true ),
	P3124_INVERT_XY_W("p3124_invert_xy_w", SymmetricGroup4.P3124, true , true , false),
	P3124_INVERT_XYZW("p3124_invert_xyzw", SymmetricGroup4.P3124, true , true , true ),
	P2314_INVERT____W("p2314_invert____w", SymmetricGroup4.P2314, false, false, false),
	P2314_INVERT___ZW("p2314_invert___zw", SymmetricGroup4.P2314, false, false, true ),
	P2314_INVERT__Y_W("p2314_invert__y_w", SymmetricGroup4.P2314, false, true , false),
	P2314_INVERT__YZW("p2314_invert__yzw", SymmetricGroup4.P2314, false, true , true ),
	P2314_INVERT_X__W("p2314_invert_x__w", SymmetricGroup4.P2314, true , false, false),
	P2314_INVERT_X_ZW("p2314_invert_x_zw", SymmetricGroup4.P2314, true , false, true ),
	P2314_INVERT_XY_W("p2314_invert_xy_w", SymmetricGroup4.P2314, true , true , false),
	P2314_INVERT_XYZW("p2314_invert_xyzw", SymmetricGroup4.P2314, true , true , true ),
	P3214_INVERT____W("p3214_invert____w", SymmetricGroup4.P3214, false, false, false),
	P3214_INVERT___ZW("p3214_invert___zw", SymmetricGroup4.P3214, false, false, true ),
	P3214_INVERT__Y_W("p3214_invert__y_w", SymmetricGroup4.P3214, false, true , false),
	P3214_INVERT__YZW("p3214_invert__yzw", SymmetricGroup4.P3214, false, true , true ),
	P3214_INVERT_X__W("p3214_invert_x__w", SymmetricGroup4.P3214, true , false, false),
	P3214_INVERT_X_ZW("p3214_invert_x_zw", SymmetricGroup4.P3214, true , false, true ),
	P3214_INVERT_XY_W("p3214_invert_xy_w", SymmetricGroup4.P3214, true , true , false),
	P3214_INVERT_XYZW("p3214_invert_xyzw", SymmetricGroup4.P3214, true , true , true ),
	P1243_INVERT____W("p1243_invert____w", SymmetricGroup4.P1243, false, false, false),
	P1243_INVERT___ZW("p1243_invert___zw", SymmetricGroup4.P1243, false, false, true ),
	P1243_INVERT__Y_W("p1243_invert__y_w", SymmetricGroup4.P1243, false, true , false),
	P1243_INVERT__YZW("p1243_invert__yzw", SymmetricGroup4.P1243, false, true , true ),
	P1243_INVERT_X__W("p1243_invert_x__w", SymmetricGroup4.P1243, true , false, false),
	P1243_INVERT_X_ZW("p1243_invert_x_zw", SymmetricGroup4.P1243, true , false, true ),
	P1243_INVERT_XY_W("p1243_invert_xy_w", SymmetricGroup4.P1243, true , true , false),
	P1243_INVERT_XYZW("p1243_invert_xyzw", SymmetricGroup4.P1243, true , true , true ),
	P2143_INVERT____W("p2143_invert____w", SymmetricGroup4.P2143, false, false, false),
	P2143_INVERT___ZW("p2143_invert___zw", SymmetricGroup4.P2143, false, false, true ),
	P2143_INVERT__Y_W("p2143_invert__y_w", SymmetricGroup4.P2143, false, true , false),
	P2143_INVERT__YZW("p2143_invert__yzw", SymmetricGroup4.P2143, false, true , true ),
	P2143_INVERT_X__W("p2143_invert_x__w", SymmetricGroup4.P2143, true , false, false),
	P2143_INVERT_X_ZW("p2143_invert_x_zw", SymmetricGroup4.P2143, true , false, true ),
	P2143_INVERT_XY_W("p2143_invert_xy_w", SymmetricGroup4.P2143, true , true , false),
	P2143_INVERT_XYZW("p2143_invert_xyzw", SymmetricGroup4.P2143, true , true , true ),
	P1423_INVERT____W("p1423_invert____w", SymmetricGroup4.P1423, false, false, false),
	P1423_INVERT___ZW("p1423_invert___zw", SymmetricGroup4.P1423, false, false, true ),
	P1423_INVERT__Y_W("p1423_invert__y_w", SymmetricGroup4.P1423, false, true , false),
	P1423_INVERT__YZW("p1423_invert__yzw", SymmetricGroup4.P1423, false, true , true ),
	P1423_INVERT_X__W("p1423_invert_x__w", SymmetricGroup4.P1423, true , false, false),
	P1423_INVERT_X_ZW("p1423_invert_x_zw", SymmetricGroup4.P1423, true , false, true ),
	P1423_INVERT_XY_W("p1423_invert_xy_w", SymmetricGroup4.P1423, true , true , false),
	P1423_INVERT_XYZW("p1423_invert_xyzw", SymmetricGroup4.P1423, true , true , true ),
	P4123_INVERT____W("p4123_invert____w", SymmetricGroup4.P4123, false, false, false),
	P4123_INVERT___ZW("p4123_invert___zw", SymmetricGroup4.P4123, false, false, true ),
	P4123_INVERT__Y_W("p4123_invert__y_w", SymmetricGroup4.P4123, false, true , false),
	P4123_INVERT__YZW("p4123_invert__yzw", SymmetricGroup4.P4123, false, true , true ),
	P4123_INVERT_X__W("p4123_invert_x__w", SymmetricGroup4.P4123, true , false, false),
	P4123_INVERT_X_ZW("p4123_invert_x_zw", SymmetricGroup4.P4123, true , false, true ),
	P4123_INVERT_XY_W("p4123_invert_xy_w", SymmetricGroup4.P4123, true , true , false),
	P4123_INVERT_XYZW("p4123_invert_xyzw", SymmetricGroup4.P4123, true , true , true ),
	P2413_INVERT____W("p2413_invert____w", SymmetricGroup4.P2413, false, false, false),
	P2413_INVERT___ZW("p2413_invert___zw", SymmetricGroup4.P2413, false, false, true ),
	P2413_INVERT__Y_W("p2413_invert__y_w", SymmetricGroup4.P2413, false, true , false),
	P2413_INVERT__YZW("p2413_invert__yzw", SymmetricGroup4.P2413, false, true , true ),
	P2413_INVERT_X__W("p2413_invert_x__w", SymmetricGroup4.P2413, true , false, false),
	P2413_INVERT_X_ZW("p2413_invert_x_zw", SymmetricGroup4.P2413, true , false, true ),
	P2413_INVERT_XY_W("p2413_invert_xy_w", SymmetricGroup4.P2413, true , true , false),
	P2413_INVERT_XYZW("p2413_invert_xyzw", SymmetricGroup4.P2413, true , true , true ),
	P4213_INVERT____W("p4213_invert____w", SymmetricGroup4.P4213, false, false, false),
	P4213_INVERT___ZW("p4213_invert___zw", SymmetricGroup4.P4213, false, false, true ),
	P4213_INVERT__Y_W("p4213_invert__y_w", SymmetricGroup4.P4213, false, true , false),
	P4213_INVERT__YZW("p4213_invert__yzw", SymmetricGroup4.P4213, false, true , true ),
	P4213_INVERT_X__W("p4213_invert_x__w", SymmetricGroup4.P4213, true , false, false),
	P4213_INVERT_X_ZW("p4213_invert_x_zw", SymmetricGroup4.P4213, true , false, true ),
	P4213_INVERT_XY_W("p4213_invert_xy_w", SymmetricGroup4.P4213, true , true , false),
	P4213_INVERT_XYZW("p4213_invert_xyzw", SymmetricGroup4.P4213, true , true , true ),
	P1342_INVERT____W("p1342_invert____w", SymmetricGroup4.P1342, false, false, false),
	P1342_INVERT___ZW("p1342_invert___zw", SymmetricGroup4.P1342, false, false, true ),
	P1342_INVERT__Y_W("p1342_invert__y_w", SymmetricGroup4.P1342, false, true , false),
	P1342_INVERT__YZW("p1342_invert__yzw", SymmetricGroup4.P1342, false, true , true ),
	P1342_INVERT_X__W("p1342_invert_x__w", SymmetricGroup4.P1342, true , false, false),
	P1342_INVERT_X_ZW("p1342_invert_x_zw", SymmetricGroup4.P1342, true , false, true ),
	P1342_INVERT_XY_W("p1342_invert_xy_w", SymmetricGroup4.P1342, true , true , false),
	P1342_INVERT_XYZW("p1342_invert_xyzw", SymmetricGroup4.P1342, true , true , true ),
	P3142_INVERT____W("p3142_invert____w", SymmetricGroup4.P3142, false, false, false),
	P3142_INVERT___ZW("p3142_invert___zw", SymmetricGroup4.P3142, false, false, true ),
	P3142_INVERT__Y_W("p3142_invert__y_w", SymmetricGroup4.P3142, false, true , false),
	P3142_INVERT__YZW("p3142_invert__yzw", SymmetricGroup4.P3142, false, true , true ),
	P3142_INVERT_X__W("p3142_invert_x__w", SymmetricGroup4.P3142, true , false, false),
	P3142_INVERT_X_ZW("p3142_invert_x_zw", SymmetricGroup4.P3142, true , false, true ),
	P3142_INVERT_XY_W("p3142_invert_xy_w", SymmetricGroup4.P3142, true , true , false),
	P3142_INVERT_XYZW("p3142_invert_xyzw", SymmetricGroup4.P3142, true , true , true ),
	P1432_INVERT____W("p1432_invert____w", SymmetricGroup4.P1432, false, false, false),
	P1432_INVERT___ZW("p1432_invert___zw", SymmetricGroup4.P1432, false, false, true ),
	P1432_INVERT__Y_W("p1432_invert__y_w", SymmetricGroup4.P1432, false, true , false),
	P1432_INVERT__YZW("p1432_invert__yzw", SymmetricGroup4.P1432, false, true , true ),
	P1432_INVERT_X__W("p1432_invert_x__w", SymmetricGroup4.P1432, true , false, false),
	P1432_INVERT_X_ZW("p1432_invert_x_zw", SymmetricGroup4.P1432, true , false, true ),
	P1432_INVERT_XY_W("p1432_invert_xy_w", SymmetricGroup4.P1432, true , true , false),
	P1432_INVERT_XYZW("p1432_invert_xyzw", SymmetricGroup4.P1432, true , true , true ),
	P4132_INVERT____W("p4132_invert____w", SymmetricGroup4.P4132, false, false, false),
	P4132_INVERT___ZW("p4132_invert___zw", SymmetricGroup4.P4132, false, false, true ),
	P4132_INVERT__Y_W("p4132_invert__y_w", SymmetricGroup4.P4132, false, true , false),
	P4132_INVERT__YZW("p4132_invert__yzw", SymmetricGroup4.P4132, false, true , true ),
	P4132_INVERT_X__W("p4132_invert_x__w", SymmetricGroup4.P4132, true , false, false),
	P4132_INVERT_X_ZW("p4132_invert_x_zw", SymmetricGroup4.P4132, true , false, true ),
	P4132_INVERT_XY_W("p4132_invert_xy_w", SymmetricGroup4.P4132, true , true , false),
	P4132_INVERT_XYZW("p4132_invert_xyzw", SymmetricGroup4.P4132, true , true , true ),
	P3412_INVERT____W("p3412_invert____w", SymmetricGroup4.P3412, false, false, false),
	P3412_INVERT___ZW("p3412_invert___zw", SymmetricGroup4.P3412, false, false, true ),
	P3412_INVERT__Y_W("p3412_invert__y_w", SymmetricGroup4.P3412, false, true , false),
	P3412_INVERT__YZW("p3412_invert__yzw", SymmetricGroup4.P3412, false, true , true ),
	P3412_INVERT_X__W("p3412_invert_x__w", SymmetricGroup4.P3412, true , false, false),
	P3412_INVERT_X_ZW("p3412_invert_x_zw", SymmetricGroup4.P3412, true , false, true ),
	P3412_INVERT_XY_W("p3412_invert_xy_w", SymmetricGroup4.P3412, true , true , false),
	P3412_INVERT_XYZW("p3412_invert_xyzw", SymmetricGroup4.P3412, true , true , true ),
	P4312_INVERT____W("p4312_invert____w", SymmetricGroup4.P4312, false, false, false),
	P4312_INVERT___ZW("p4312_invert___zw", SymmetricGroup4.P4312, false, false, true ),
	P4312_INVERT__Y_W("p4312_invert__y_w", SymmetricGroup4.P4312, false, true , false),
	P4312_INVERT__YZW("p4312_invert__yzw", SymmetricGroup4.P4312, false, true , true ),
	P4312_INVERT_X__W("p4312_invert_x__w", SymmetricGroup4.P4312, true , false, false),
	P4312_INVERT_X_ZW("p4312_invert_x_zw", SymmetricGroup4.P4312, true , false, true ),
	P4312_INVERT_XY_W("p4312_invert_xy_w", SymmetricGroup4.P4312, true , true , false),
	P4312_INVERT_XYZW("p4312_invert_xyzw", SymmetricGroup4.P4312, true , true , true ),
	P2341_INVERT____W("p2341_invert____w", SymmetricGroup4.P2341, false, false, false),
	P2341_INVERT___ZW("p2341_invert___zw", SymmetricGroup4.P2341, false, false, true ),
	P2341_INVERT__Y_W("p2341_invert__y_w", SymmetricGroup4.P2341, false, true , false),
	P2341_INVERT__YZW("p2341_invert__yzw", SymmetricGroup4.P2341, false, true , true ),
	P2341_INVERT_X__W("p2341_invert_x__w", SymmetricGroup4.P2341, true , false, false),
	P2341_INVERT_X_ZW("p2341_invert_x_zw", SymmetricGroup4.P2341, true , false, true ),
	P2341_INVERT_XY_W("p2341_invert_xy_w", SymmetricGroup4.P2341, true , true , false),
	P2341_INVERT_XYZW("p2341_invert_xyzw", SymmetricGroup4.P2341, true , true , true ),
	P3241_INVERT____W("p3241_invert____w", SymmetricGroup4.P3241, false, false, false),
	P3241_INVERT___ZW("p3241_invert___zw", SymmetricGroup4.P3241, false, false, true ),
	P3241_INVERT__Y_W("p3241_invert__y_w", SymmetricGroup4.P3241, false, true , false),
	P3241_INVERT__YZW("p3241_invert__yzw", SymmetricGroup4.P3241, false, true , true ),
	P3241_INVERT_X__W("p3241_invert_x__w", SymmetricGroup4.P3241, true , false, false),
	P3241_INVERT_X_ZW("p3241_invert_x_zw", SymmetricGroup4.P3241, true , false, true ),
	P3241_INVERT_XY_W("p3241_invert_xy_w", SymmetricGroup4.P3241, true , true , false),
	P3241_INVERT_XYZW("p3241_invert_xyzw", SymmetricGroup4.P3241, true , true , true ),
	P2431_INVERT____W("p2431_invert____w", SymmetricGroup4.P2431, false, false, false),
	P2431_INVERT___ZW("p2431_invert___zw", SymmetricGroup4.P2431, false, false, true ),
	P2431_INVERT__Y_W("p2431_invert__y_w", SymmetricGroup4.P2431, false, true , false),
	P2431_INVERT__YZW("p2431_invert__yzw", SymmetricGroup4.P2431, false, true , true ),
	P2431_INVERT_X__W("p2431_invert_x__w", SymmetricGroup4.P2431, true , false, false),
	P2431_INVERT_X_ZW("p2431_invert_x_zw", SymmetricGroup4.P2431, true , false, true ),
	P2431_INVERT_XY_W("p2431_invert_xy_w", SymmetricGroup4.P2431, true , true , false),
	P2431_INVERT_XYZW("p2431_invert_xyzw", SymmetricGroup4.P2431, true , true , true ),
	P4231_INVERT____W("p4231_invert____w", SymmetricGroup4.P4231, false, false, false),
	P4231_INVERT___ZW("p4231_invert___zw", SymmetricGroup4.P4231, false, false, true ),
	P4231_INVERT__Y_W("p4231_invert__y_w", SymmetricGroup4.P4231, false, true , false),
	P4231_INVERT__YZW("p4231_invert__yzw", SymmetricGroup4.P4231, false, true , true ),
	P4231_INVERT_X__W("p4231_invert_x__w", SymmetricGroup4.P4231, true , false, false),
	P4231_INVERT_X_ZW("p4231_invert_x_zw", SymmetricGroup4.P4231, true , false, true ),
	P4231_INVERT_XY_W("p4231_invert_xy_w", SymmetricGroup4.P4231, true , true , false),
	P4231_INVERT_XYZW("p4231_invert_xyzw", SymmetricGroup4.P4231, true , true , true ),
	P3421_INVERT____W("p3421_invert____w", SymmetricGroup4.P3421, false, false, false),
	P3421_INVERT___ZW("p3421_invert___zw", SymmetricGroup4.P3421, false, false, true ),
	P3421_INVERT__Y_W("p3421_invert__y_w", SymmetricGroup4.P3421, false, true , false),
	P3421_INVERT__YZW("p3421_invert__yzw", SymmetricGroup4.P3421, false, true , true ),
	P3421_INVERT_X__W("p3421_invert_x__w", SymmetricGroup4.P3421, true , false, false),
	P3421_INVERT_X_ZW("p3421_invert_x_zw", SymmetricGroup4.P3421, true , false, true ),
	P3421_INVERT_XY_W("p3421_invert_xy_w", SymmetricGroup4.P3421, true , true , false),
	P3421_INVERT_XYZW("p3421_invert_xyzw", SymmetricGroup4.P3421, true , true , true ),
	P4321_INVERT____W("p4321_invert____w", SymmetricGroup4.P4321, false, false, false),
	P4321_INVERT___ZW("p4321_invert___zw", SymmetricGroup4.P4321, false, false, true ),
	P4321_INVERT__Y_W("p4321_invert__y_w", SymmetricGroup4.P4321, false, true , false),
	P4321_INVERT__YZW("p4321_invert__yzw", SymmetricGroup4.P4321, false, true , true ),
	P4321_INVERT_X__W("p4321_invert_x__w", SymmetricGroup4.P4321, true , false, false),
	P4321_INVERT_X_ZW("p4321_invert_x_zw", SymmetricGroup4.P4321, true , false, true ),
	P4321_INVERT_XY_W("p4321_invert_xy_w", SymmetricGroup4.P4321, true , true , false),
	P4321_INVERT_XYZW("p4321_invert_xyzw", SymmetricGroup4.P4321, true , true , true );

	@Shadow @Final private boolean invertX;
	@Shadow @Final private boolean invertY;
	@Shadow @Final private boolean invertZ;
	@Unique private boolean invertW;
	@Unique	private Matrix4fc transformation4;

	@Shadow
	OctahedralGroupMixin(String name, SymmetricGroup3 permutation, boolean invertX, boolean invertY, boolean invertZ) {}

	@Shadow
	@Final
	private SymmetricGroup3 permutation;

	@Shadow
	public boolean inverts(Direction.Axis axis) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Override
	public Matrix4fc transformation4() {
		return this.transformation4;
	}

	@Unique
	private static int trace(boolean invertX, boolean invertY, boolean invertZ, boolean invertW, SymmetricGroup3 permutation) {
		int inversionIndex = (invertW ? 8 : 0) + (invertZ ? 4 : 0) + (invertY ? 2 : 0) + (invertX ? 1 : 0);
		return permutation.ordinal() << 4 | inversionIndex;
	}

	@Unique
	private int trace() {
		return trace(this.invertX, this.invertY, this.invertZ, this.invertW, this.permutation);
	}

	@Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true)
	private static void addInvertWAndProperCayleyTable(CallbackInfoReturnable<OctahedralGroup[][]> cir) {
		OctahedralGroupMixin[] values = values();
		for (OctahedralGroupMixin group : values) {
			// Adding invertW to all enum members.
			group.invertW = false; // false is the default for vanilla and other mods. This is already set by JVM, but just to make sure.
			// Initializing
			group.transformation4 = new Matrix4f()
				.scaling(
					group.invertX ? -1F : 1F,
					group.invertY ? -1F : 1F,
					group.invertZ ? -1F : 1F
				)
				.m33(group.invertW ? -1F : 1F)
				.mul(SymmetricGroup4.as(group.permutation).transformation4());
		}
		for (OctahedralGroupMixin group : new OctahedralGroupMixin[] { P1234_INVERT____W, P1234_INVERT___ZW, P1234_INVERT__Y_W, P1234_INVERT__YZW, P1234_INVERT_X__W, P1234_INVERT_X_ZW, P1234_INVERT_XY_W, P1234_INVERT_XYZW, P2134_INVERT____W, P2134_INVERT___ZW, P2134_INVERT__Y_W, P2134_INVERT__YZW, P2134_INVERT_X__W, P2134_INVERT_X_ZW, P2134_INVERT_XY_W, P2134_INVERT_XYZW, P1324_INVERT____W, P1324_INVERT___ZW, P1324_INVERT__Y_W, P1324_INVERT__YZW, P1324_INVERT_X__W, P1324_INVERT_X_ZW, P1324_INVERT_XY_W, P1324_INVERT_XYZW, P3124_INVERT____W, P3124_INVERT___ZW, P3124_INVERT__Y_W, P3124_INVERT__YZW, P3124_INVERT_X__W, P3124_INVERT_X_ZW, P3124_INVERT_XY_W, P3124_INVERT_XYZW, P2314_INVERT____W, P2314_INVERT___ZW, P2314_INVERT__Y_W, P2314_INVERT__YZW, P2314_INVERT_X__W, P2314_INVERT_X_ZW, P2314_INVERT_XY_W, P2314_INVERT_XYZW, P3214_INVERT____W, P3214_INVERT___ZW, P3214_INVERT__Y_W, P3214_INVERT__YZW, P3214_INVERT_X__W, P3214_INVERT_X_ZW, P3214_INVERT_XY_W, P3214_INVERT_XYZW, P1243_INVERT____W, P1243_INVERT___ZW, P1243_INVERT__Y_W, P1243_INVERT__YZW, P1243_INVERT_X__W, P1243_INVERT_X_ZW, P1243_INVERT_XY_W, P1243_INVERT_XYZW, P2143_INVERT____W, P2143_INVERT___ZW, P2143_INVERT__Y_W, P2143_INVERT__YZW, P2143_INVERT_X__W, P2143_INVERT_X_ZW, P2143_INVERT_XY_W, P2143_INVERT_XYZW, P1423_INVERT____W, P1423_INVERT___ZW, P1423_INVERT__Y_W, P1423_INVERT__YZW, P1423_INVERT_X__W, P1423_INVERT_X_ZW, P1423_INVERT_XY_W, P1423_INVERT_XYZW, P4123_INVERT____W, P4123_INVERT___ZW, P4123_INVERT__Y_W, P4123_INVERT__YZW, P4123_INVERT_X__W, P4123_INVERT_X_ZW, P4123_INVERT_XY_W, P4123_INVERT_XYZW, P2413_INVERT____W, P2413_INVERT___ZW, P2413_INVERT__Y_W, P2413_INVERT__YZW, P2413_INVERT_X__W, P2413_INVERT_X_ZW, P2413_INVERT_XY_W, P2413_INVERT_XYZW, P4213_INVERT____W, P4213_INVERT___ZW, P4213_INVERT__Y_W, P4213_INVERT__YZW, P4213_INVERT_X__W, P4213_INVERT_X_ZW, P4213_INVERT_XY_W, P4213_INVERT_XYZW, P1342_INVERT____W, P1342_INVERT___ZW, P1342_INVERT__Y_W, P1342_INVERT__YZW, P1342_INVERT_X__W, P1342_INVERT_X_ZW, P1342_INVERT_XY_W, P1342_INVERT_XYZW, P3142_INVERT____W, P3142_INVERT___ZW, P3142_INVERT__Y_W, P3142_INVERT__YZW, P3142_INVERT_X__W, P3142_INVERT_X_ZW, P3142_INVERT_XY_W, P3142_INVERT_XYZW, P1432_INVERT____W, P1432_INVERT___ZW, P1432_INVERT__Y_W, P1432_INVERT__YZW, P1432_INVERT_X__W, P1432_INVERT_X_ZW, P1432_INVERT_XY_W, P1432_INVERT_XYZW, P4132_INVERT____W, P4132_INVERT___ZW, P4132_INVERT__Y_W, P4132_INVERT__YZW, P4132_INVERT_X__W, P4132_INVERT_X_ZW, P4132_INVERT_XY_W, P4132_INVERT_XYZW, P3412_INVERT____W, P3412_INVERT___ZW, P3412_INVERT__Y_W, P3412_INVERT__YZW, P3412_INVERT_X__W, P3412_INVERT_X_ZW, P3412_INVERT_XY_W, P3412_INVERT_XYZW, P4312_INVERT____W, P4312_INVERT___ZW, P4312_INVERT__Y_W, P4312_INVERT__YZW, P4312_INVERT_X__W, P4312_INVERT_X_ZW, P4312_INVERT_XY_W, P4312_INVERT_XYZW, P2341_INVERT____W, P2341_INVERT___ZW, P2341_INVERT__Y_W, P2341_INVERT__YZW, P2341_INVERT_X__W, P2341_INVERT_X_ZW, P2341_INVERT_XY_W, P2341_INVERT_XYZW, P3241_INVERT____W, P3241_INVERT___ZW, P3241_INVERT__Y_W, P3241_INVERT__YZW, P3241_INVERT_X__W, P3241_INVERT_X_ZW, P3241_INVERT_XY_W, P3241_INVERT_XYZW, P2431_INVERT____W, P2431_INVERT___ZW, P2431_INVERT__Y_W, P2431_INVERT__YZW, P2431_INVERT_X__W, P2431_INVERT_X_ZW, P2431_INVERT_XY_W, P2431_INVERT_XYZW, P4231_INVERT____W, P4231_INVERT___ZW, P4231_INVERT__Y_W, P4231_INVERT__YZW, P4231_INVERT_X__W, P4231_INVERT_X_ZW, P4231_INVERT_XY_W, P4231_INVERT_XYZW, P3421_INVERT____W, P3421_INVERT___ZW, P3421_INVERT__Y_W, P3421_INVERT__YZW, P3421_INVERT_X__W, P3421_INVERT_X_ZW, P3421_INVERT_XY_W, P3421_INVERT_XYZW, P4321_INVERT____W, P4321_INVERT___ZW, P4321_INVERT__Y_W, P4321_INVERT__YZW, P4321_INVERT_X__W, P4321_INVERT_X_ZW, P4321_INVERT_XY_W, P4321_INVERT_XYZW }) {
			group.invertW = true;
		}

		OctahedralGroup[][] table = new OctahedralGroup[values.length][values.length];
		Map<Integer, OctahedralGroupMixin> fingerprints = Arrays
			.stream(values)
			.collect(Collectors.toMap(OctahedralGroupMixin::trace, Function.identity()));

		for (OctahedralGroupMixin first : values) {
			for (OctahedralGroupMixin second : values) {
				SymmetricGroup3 composedPermutation = second.permutation.compose(first.permutation);
				boolean composedInvertX = first.inverts(Direction .Axis.X) ^ second.inverts(first.permutation.permuteAxis(Direction .Axis.X));
				boolean composedInvertY = first.inverts(Direction .Axis.Y) ^ second.inverts(first.permutation.permuteAxis(Direction .Axis.Y));
				boolean composedInvertZ = first.inverts(Direction .Axis.Z) ^ second.inverts(first.permutation.permuteAxis(Direction .Axis.Z));
				boolean composedInvertW = first.inverts(Direction4.Axis.W) ^ second.inverts(first.permutation.permuteAxis(Direction4.Axis.W));
				table[first.ordinal()][second.ordinal()] = (OctahedralGroup) (Object) fingerprints.get(
					trace(composedInvertX, composedInvertY, composedInvertZ, composedInvertW, composedPermutation)
				);
			}
		}

		cir.setReturnValue(table);
	}


	// TODO: re-enable
//	/**
//	 * @author iluha168
//	 * @reason Returns a 3x3 matrix. Replacing with a method for a 4x4 matrix.
//	 */
//	@Overwrite
//	public Matrix3fc transformation() {
//		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use OctahedralGroup4#transformation4 instead."));
//	}

	/**
	 * @author iluha168
	 * @reason Returns a 3D vector. Replacing with a method for a 4D vector.
	 */
	@Overwrite
	public Vector3i rotate(Vector3i v) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use OctahedralGroup4#rotate instead."));
	}

	@Override
	public Vector4i rotate(Vector4i v) {
		SymmetricGroup4.as(this.permutation).permuteVector(v);
		v.x = v.x * (this.invertX ? -1 : 1);
		v.y = v.y * (this.invertY ? -1 : 1);
		v.z = v.z * (this.invertZ ? -1 : 1);
		v.w = v.w * (this.invertW ? -1 : 1);
		return v;
	}

	@WrapMethod(method = "inverts")
	boolean inverts(Direction.Axis axis, Operation<Boolean> original) {
		return axis == Direction4.Axis.W ? this.invertW : original.call(axis);
	}

	@Redirect(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Lcom/mojang/math/SymmetricGroup3;transformation()Lorg/joml/Matrix3fc;"
	))
	Matrix3fc preventCrash(SymmetricGroup3 instance) {
		// Removes call to SymmetricGroup3#transformation.
		return new Matrix3f();
	}
}
