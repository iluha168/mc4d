TODO: write other types of contributing :P

## Assets
### 4D Block models

The renderer is 3D as of now, and so are models.
The JSON files under `assets/minecraft/blockstate/` choose the right 3D _model_ for a blockstate, and now for a block-relative camera coordinate too.

The block-relative camera position is scaled by 16 in blockstate files:
```
"facing=south": {
  "model": {
    "w=6;10": "minecraft:block/anvil",
    "w=4;12": "mc4d:block/anvil_dw_2",
    "w=3;13": "mc4d:block/anvil_dw_3",
    "w=2;14": "mc4d:block/anvil_dw_4",
    "w=0;16": "mc4d:block/anvil_empty"
  }
},
```
The format is `w=from inclusive;until exclusive`, floating point numbers are supported,
but not really needed - block hitboxes (voxel shapes) have 1/16 precision in vanilla as far as I am aware.
The order matters, and the first line wins in overlapping ranges.

Look for "3D-only model" or "Missing model for variant" in client logs to find models to work on. The models must match the hitbox!
> [!TIP]
> 
> Use the "no_block_model_renderer" debug option (F3+F6).
> When enabled, it makes all block "textures" go away, and you can clearly see the block's hitbox.