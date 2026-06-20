package com.iluha168.mc4d.client.particle;

import org.apache.logging.log4j.util.InternalApi;

/**
 * Implemented by {@link net.minecraft.client.particle.Particle}.
 */
public interface Particle4 {
	@InternalApi
	void init_finish(double w);
	@InternalApi
	void init_finish(double w, double wa);

	double wo();
	double w();
	double wd();

	void setParticleSpeed(double xd, double yd, double zd, double wd);

	void setPos(double x, double y, double z, double w);

	void move(double xa, double ya, double za, double wa);
}
