package agency.highlysuspect.dazzle2.client;

import agency.highlysuspect.dazzle2.etc.DyedEndRodParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;

//Based on a copy-and-paste of EndRodParticle because lol private constructor.
//Added an extra parameter for setTargetColor.
@Environment(EnvType.CLIENT)
public class DyedEndRodParticle extends AnimatedParticle {
	private DyedEndRodParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider, int tint) {
		super(world, x, y, z, spriteProvider, -5.0E-4F);
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.scale *= 0.75F;
		this.maxAge = 60 + this.random.nextInt(12);
		
		setColor(tint);
		
		float magicNumber = tint == DyeColor.WHITE.getMapColor().color ? 0.9f : 0.6f;
		
		int r = (tint & 0xFF0000) >> 16;
		int g = (tint & 0x00FF00) >> 8;
		int b = tint & 0x0000FF;
		
		r *= magicNumber;
		r = MathHelper.clamp(r, 0, 255);
		g *= magicNumber;
		g = MathHelper.clamp(g, 0, 255);
		b *= magicNumber;
		b = MathHelper.clamp(b, 0, 255);
		
		setTargetColor((r << 16) | (g << 8) | b);
		
		this.setSpriteForAge(spriteProvider);
	}
	
	public void move(double dx, double dy, double dz) {
		this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
		this.repositionFromBoundingBox();
	}
	
	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DyedEndRodParticleEffect> {
		private final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}
		
		public Particle createParticle(DyedEndRodParticleEffect ef, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new DyedEndRodParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider, ef.getColor());
		}
	}
}
