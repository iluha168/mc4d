package com.iluha168.mc4d.world.entity;

import com.iluha168.mc4d.world.phys.Vec4;

public interface Entity4 {
	void setPosRaw(Vec4 newPos);

	void absSnapTo(Vec4 newPos);
	void absSnapTo(Vec4 newPos, float yRot, float xRot);

	double getW();
}
