# The 4th Dimension
A Vanilla+ Minecraft mod that Adds a 4th spatial dimension.
The goal of this mod is to make that dimension act identical to the existing 3.

> [!CAUTION]
> 3D worlds are not compatible with 4D worlds, in both directions.
> Create a new world after installing the mod.

## Features
- The 4th axis, labeled "W". It is the 3rd **horizontal** axis.
- 2 new `Direction`s: "Ana" for positive W and "kata" for negative W.
- All entities have 4D positions.

## Mod compatibility
If a mod uses vanilla dimension abstractions like `Vec3`, `AABB`, etc., and does not operate on each individual dimension, it should work fine.
For example, creating them from individual components - `new Vec(X, Y, Z)`, `new AABB(minX, minY, minZ, maxX, maxY, maxZ)` - bad, using `vec.with(Axis.Z, 2)`, `aabb.getSize()` - safe.

Iterating over all axes, all directions, all blocks in a radius, etc., and using their methods - also ok.
However, pulling out a specific dimension value is a bad idea - the code could end up treating all W as W=0.

> [!CAUTION]
> If a vanilla method has 3 arguments for spatial dimensions, e.g. `method(..., double x, double y, double z, ...)`, assume that it will always throw.
> Use vanilla methods that take aforementioned dimension abstractions.
> If those do not exist, there is an alternative in the MC4D API, or the method does not throw.