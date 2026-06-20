# The 4th Dimension
A Vanilla+ Minecraft mod that Adds a 4th spatial dimension.
The goal of this mod is to make that dimension act identical to the existing 3.

> [!NOTE]
> **Not** to be confused with dimensions like **the End and the Nether** (_Levels_).
> We are talking about a [mathematical dimension](https://en.wikipedia.org/wiki/Dimension) here, as a degree of freedom.

> [!NOTE]
> **Not** to be confused with **time** from [General Relativity](https://en.wikipedia.org/wiki/General_relativity).
> "Spatial" means a dimension of space. I hate that I have to say this.

## Features
- The **4th axis**, labeled "**W**". It is the 3rd **horizontal** axis.
- 2 new directions: "**ana**" for W+ and "**kata**" for W-. Remember the south-east rule? It is _ana_-south-east now.
- All entities, blocks and chunk sections have **4D positions**.
- **Horizontal** sizes are capped at `±100000` in **all 3 directions**, compared to vanilla's `±30000000` in 2 directions.
  - This means vanilla world has area of `36e14` square meters, but in this mod the area is `8e15` cubic meters.
  - The horizontal world limits are enforced by a **4D world border**.
- Vertical axis has been reduced from `[-4064; 4062]` to `[-992; 990]` in size. Vanilla does not use the entirety of this huge limit, but mods and datapacks that do will break.
- Chunks are **16x16x16 in horizontal size**, instead of 16x16. Regions are 32x32x32. 
- The changes affect Overworld, Nether, etc., **all _Levels_**.
- All entries in the **F3 debug HUD** show 4 dimensions instead of 3.
- A **4D particle engine**. It is rare when a particle intersects your camera's 3D world slice, use **debug renderers in F3+F6** to see 1 slice further.

> [!CAUTION]
> 3D saves are not compatible with 4D saves, in both directions. Create a new world after installing the mod.

### F3+F6
Your client renders a 2D image of a 3D slice of the 4D world.

To see 1 block further along the axis perpendicular to that 3D slice, scroll down in the _Debug Options_ menu for `mc4d:neighbouring_slice` entries.
You can either enable them to render while F3 is open, or always.

## Mod compatibility
TL;DR: **None.** Compatibility with Minecraft itself is in the works.

For mod developers:

If a mod uses vanilla dimension abstractions like `Vec3`, `AABB`, etc., and does not operate on each individual dimension, it should work fine.
For example, creating them from individual components - `new Vec(X, Y, Z)`, `new AABB(minX, minY, minZ, maxX, maxY, maxZ)` - bad, using `vec.with(Axis.Z, 2)`, `aabb.getSize()` - safe.

Iterating over all axes, all directions, all blocks in a radius, etc., and using their methods - also ok.
However, pulling out a specific dimension value is a bad idea - the code could end up treating all W as W=0.

If a vanilla method has 3 arguments for spatial dimensions, e.g. `method(..., double x, double y, double z, ...)`, assume that it will always throw.
Use vanilla methods that take aforementioned dimension abstractions.
If those do not exist, there is an alternative in the MC4D API, or the method does not throw.