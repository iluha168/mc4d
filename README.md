# The 4th Dimension
A Vanilla+ Minecraft mod that Adds a 4th spatial dimension.
The goal of this mod is to make that dimension act identical to the existing 3.
<details>

> [!NOTE]
> **Not** to be confused with dimensions like **the End and the Nether** (_Levels_).
> We are talking about a [mathematical dimension](https://en.wikipedia.org/wiki/Dimension) here, as a degree of freedom.

> [!NOTE]
> **Not** to be confused with **time** from [General Relativity](https://en.wikipedia.org/wiki/General_relativity).
> "Spatial" means a dimension of space. I hate that I have to say this.

</details>

### "How do you move in the 4th dimension??"
Set keybinds to "Strafe Ana" and "Strafe Kata". They behave exactly as "Strafe Left" and "Strafe Right" except the direction.

I got asked this so often, which is why this text is at the top.
The entire mod's philosophy is that there is nothing special about the 4th dimension - you move along W exactly the same way as X or Z.

## 4D Features
- The **4th axis**, labeled "**W**". It is the 3rd **horizontal** axis.
- 2 new directions: "**ana**" for W+ and "**kata**" for W-. Remember the south-east rule? It is _anth_-south-east now.
- All entities, blocks and chunk sections have **4D positions**.
- All entities have 2 more rotations. See [alternative mouse look keybind](#alternative-mouse-look-keybind).
- **Horizontal** sizes are capped at `±100000` in **all 3 directions**, compared to vanilla's `±30000000` in 2 directions.
  - This means vanilla world has area of `36e14` square meters, but in this mod the area is `8e15` cubic meters.
  - The horizontal world limits are enforced by a **4D world border**.
- Vertical axis has been reduced from `[-4064; 4062]` to `[-992; 990]` in size. Vanilla does not use the entirety of this huge limit, but mods and datapacks that do will break.
- Chunks are **16x16x16 in horizontal size**, instead of 16x16. Regions are 32x32x32. 
- The changes affect Overworld, Nether, etc., **all _Levels_**.
- All entries in the **F3 debug HUD** show 4 dimensions instead of 3.
- A **4D particle engine**. It is rare when a particle intersects your camera's 3D world slice, use **debug renderers in F3+F6** to see 1 slice further.
- A **4D sound engine**. Sounds outside your 3D slice do not have a definite "left" or "right", they are played as always-centered stereo audio with a variable angle (spatial spread) instead.
- **4D block models**. Kind of. Chooses different 3D models based on camera's current location inside the slice.

> [!CAUTION]  
> 3D saves are not compatible with 4D saves, in both directions. Create a new world after installing the mod.

## Alternative mouse look keybind

Entities have a full **4D orientation** described by 4 angles (2 in addition to vanilla's yaw and pitch):
- A 3rd **look** angle, a "second pitch", which tilts the horizontal facing toward ana/kata, similar to the regular pitch which tilts up/down.
  Like pitch, it is clamped to `[-90; 90]` - at `+90` you look straight ana, at `-90` straight kata.
  
  As a look direction, it is also responsible for the initial momentum of thrown items and shot projectiles.
- A 4th angle, a "second yaw", which does **not** change where you look. It spins your **local coordinate frame**: it continuously trades your "left/right" for "ana/kata".
  It wraps around just like vanilla yaw - full circle `[-180; 180]`.
  <details>

  Imagine yourself as a **3D** entity, whose "up" and "forward" are fixed in place. Those 2 directions are enough to immobilize you.
  Cross product of "up" and "forward" is always an unambiguous "right" to you. 
  If "forward" is unlocked, you can freely rotate around the "up" axis - it *leaves a plane of freedom*.

  However, in **4D**, locking "up" and "forward" still *leaves a plane of freedom*.
  "right" now needs to be fixed in place as well, in order to immobilize you,
  and only at that point do you have a definite "anth".
  Rotation along this plane is what the 4th angle describes. It does not affect your look direction, i.e. your "forward".
  </details>

While the keybind is held, mouse movement drives the two new angles instead of the vanilla ones.
<details>

Your client's movement inputs ignore these additional rotations, because the world you see is not correct at any non-zero 3rd and 4th rotations.

Your server-side, mathematically correct, local coordinates point all over the place compared to the 90-degree locked slice of the 4D world a 3D renderer could display.

Believe me, you do not want to be moving A parts south, B parts east, and C parts ana whenever you press <kbd>D</kbd>. You will be moving towards your 3D camera's right, instead of the logical right.
Same logic applies to block and entity selection. In fact, I had to change vanilla's mouse movement handling to ignore 3rd and 4th angles too!
</details>

## F3+F6
Your client renders a 2D image of a 3D slice of the 4D world.

To see 1 block further along the axis perpendicular to that 3D slice, scroll down in the _Debug Options_ menu for `mc4d:neighbouring_slice` entries.
You can either enable them to render while F3 is open, or always.

## Mod compatibility
TL;DR: **None.** Compatibility with Minecraft itself is in the works.

For mod developers:

If a mod uses vanilla dimension abstractions like `BlockPos`, `ChunkPos`, etc., and does not operate on each individual dimension, it should work fine.
For example, creating them from individual components - `new Vec3(X, Y, Z)`, `new SectionPos(X, Y, Z)` - bad, using `vec.with(Axis.Y, 2)`, `aabb.getSize()` - safe.

Iterating over all axes, all directions, etc., and using their methods - also ok.
However, pulling out a specific dimension value is a bad idea - the code could end up treating all W as W=0.

If a vanilla method has 3 arguments for spatial dimensions, e.g. `method(..., double x, double y, double z, ...)`, assume that it will always throw.
Use vanilla methods that take aforementioned dimension abstractions.
If those do not exist, there is an alternative in the MC4D API, or the method does not throw.