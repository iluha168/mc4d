package com.iluha168.mc4d.mixin.net.minecraft.client.gui.screens.inventory;

import com.iluha168.mc4d.core.Vec4i;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockEditScreen.class)
abstract class StructureBlockEditScreenMixin extends Screen {
	@Unique private static final int COORD_EDIT_WIDTH = 240 / 4;

	@Shadow
	protected abstract int parseCoordinate(String value);

	protected StructureBlockEditScreenMixin(Component title) {
		super(title);
	}

	@Shadow @Final private StructureBlockEntity structure;
	@Shadow private EditBox posXEdit, posYEdit, posZEdit;
	@Unique private EditBox posWEdit;
	@Shadow private EditBox sizeXEdit, sizeYEdit, sizeZEdit;
	@Unique private EditBox sizeWEdit;

	// TODO buttons for more Rotation and Mirror variants

	@Unique
	private EditBox addWEdit(EditBox xEdit, EditBox yEdit, EditBox zEdit, String narrationKey, Vec3i pos) {
		// Make vanilla boxes smaller and closer to the left
		int left = this.width / 2 - 152;
		xEdit.setX(left)                    ; xEdit.setWidth(COORD_EDIT_WIDTH);
		yEdit.setX(left += COORD_EDIT_WIDTH); yEdit.setWidth(COORD_EDIT_WIDTH);
		zEdit.setX(left += COORD_EDIT_WIDTH); zEdit.setWidth(COORD_EDIT_WIDTH);
		final EditBox wEdit = new EditBox(
			this.font,
			left + COORD_EDIT_WIDTH, zEdit.getY(),
			COORD_EDIT_WIDTH, zEdit.getHeight(),
			Component.translatable(narrationKey)
		);
		wEdit.setMaxLength(15);
		wEdit.setValue(Integer.toString(Vec4i.getW(pos)));
		return this.addWidget(wEdit);
	}
	@Definition(id = "addWidget", method = "Lnet/minecraft/client/gui/screens/inventory/StructureBlockEditScreen;addWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;")
	@Definition(id = "posZEdit", field = "Lnet/minecraft/client/gui/screens/inventory/StructureBlockEditScreen;posZEdit:Lnet/minecraft/client/gui/components/EditBox;")
	@Expression("this.addWidget(this.posZEdit)")
	@Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
	void init_posWEdit(CallbackInfo ci) {
		this.posWEdit = this.addWEdit(
			this.posXEdit, this.posYEdit, this.posZEdit,
			"structure_block.position.w",
			this.structure.getStructurePos()
		);
	}
	@Definition(id = "addWidget", method = "Lnet/minecraft/client/gui/screens/inventory/StructureBlockEditScreen;addWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;")
	@Definition(id = "sizeZEdit", field = "Lnet/minecraft/client/gui/screens/inventory/StructureBlockEditScreen;sizeZEdit:Lnet/minecraft/client/gui/components/EditBox;")
	@Expression("this.addWidget(this.sizeZEdit)")
	@Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
	void init_sizeWEdit(CallbackInfo ci) {
		this.sizeWEdit = this.addWEdit(
			this.sizeXEdit, this.sizeYEdit, this.sizeZEdit,
			"structure_block.size.w",
			this.structure.getStructureSize()
		);
	}

	@Inject(method = "resize", at = @At("HEAD"))
	void resize_saveW(
		int width, int height, CallbackInfo ci,
		@Share("oldPosWEdit") LocalRef<String> oldPosWEdit,
		@Share("oldSizeWEdit") LocalRef<String> oldSizeWEdit
	) {
		oldPosWEdit.set(this.posWEdit.getValue());
		oldSizeWEdit.set(this.sizeWEdit.getValue());
	}
	@Inject(method = "resize", at = @At("TAIL"))
	void resize_restoreW(
		int width, int height, CallbackInfo ci,
		@Share("oldPosWEdit") LocalRef<String> oldPosWEdit,
		@Share("oldSizeWEdit") LocalRef<String> oldSizeWEdit
	) {
		this.posWEdit.setValue(oldPosWEdit.get());
		this.sizeWEdit.setValue(oldSizeWEdit.get());
	}

	// TODO updateDirectionButtons

	@Inject(method = "updateMode", at = @At("TAIL"))
	void updateMode(StructureMode mode, CallbackInfo ci) {
		this.posWEdit.setVisible(this.posZEdit.isVisible());
		this.sizeWEdit.setVisible(this.sizeZEdit.isVisible());
	}

	@ModifyExpressionValue(method = "sendToServer", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos sendToServer_offset(BlockPos original) {
		Vec4i.setW(original, this.parseCoordinate(this.posWEdit.getValue()));
		return original;
	}
	@ModifyExpressionValue(method = "sendToServer", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/Vec3i;"
	))
	Vec3i sendToServer_size(Vec3i original) {
		Vec4i.setW(original, this.parseCoordinate(this.sizeWEdit.getValue()));
		return original;
	}

	@Definition(id = "posZEdit", field = "Lnet/minecraft/client/gui/screens/inventory/StructureBlockEditScreen;posZEdit:Lnet/minecraft/client/gui/components/EditBox;")
	@Expression("this.posZEdit.?(?, ?, ?, ?)")
	@Inject(method = "extractRenderState", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
	void extractRenderState_posW(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		this.posWEdit.extractRenderState(graphics, mouseX, mouseY, a);
	}
	@Definition(id = "sizeZEdit", field = "Lnet/minecraft/client/gui/screens/inventory/StructureBlockEditScreen;sizeZEdit:Lnet/minecraft/client/gui/components/EditBox;")
	@Expression("this.sizeZEdit.?(?, ?, ?, ?)")
	@Inject(method = "extractRenderState", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
	void extractRenderState_sizeW(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		this.sizeWEdit.extractRenderState(graphics, mouseX, mouseY, a);
	}
}
