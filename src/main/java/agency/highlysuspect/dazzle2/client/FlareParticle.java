package agency.highlysuspect.dazzle2.client;

import agency.highlysuspect.dazzle2.Junk;
import agency.highlysuspect.dazzle2.etc.FlareParticleEffect;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

public class FlareParticle extends AnimatedParticle {
	public FlareParticle(ClientWorld world, double x, double y, double z, double vx, double vy, double vz, int color, SpriteProvider spriteProvider) {
		super(world, x, y, z, spriteProvider, 0);
		
		velocityX = Junk.rangeRemap(random.nextFloat(), 0, 1, -0.01f, 0.01f) + vx;
		velocityY = Junk.rangeRemap(random.nextFloat(), 0, 1, 0.05f, 0.1f) + vy;
		velocityZ = Junk.rangeRemap(random.nextFloat(), 0, 1, -0.01f, 0.01f) + vz;
		maxAge = random.nextInt(10) + 20;
		
		setColor(color);
		
		setSpriteForAge(spriteProvider);
	}
	
	public void move(double dx, double dy, double dz) {
		this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
		this.repositionFromBoundingBox();
	}
	
	@Override
	public float getSize(float tickDelta) {
		if(age < maxAge / 2) {
			return Junk.rangeRemap(age + tickDelta, 0, maxAge / 2f, 0, 1/6f);
		} else {
			return Junk.rangeRemap(age + tickDelta, maxAge / 2f, maxAge, 1/6f, 0);
		}
	}
	
	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public static final class Factory implements ParticleFactory<FlareParticleEffect> {
		public Factory(SpriteProvider hoo) {
			this.hoo = hoo;
		}
		
		private final SpriteProvider hoo;
		
		@Override
		public Particle createParticle(FlareParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new FlareParticle(world, x, y, z, velocityX, velocityY, velocityZ, parameters.color, hoo);
		}
	}
}
