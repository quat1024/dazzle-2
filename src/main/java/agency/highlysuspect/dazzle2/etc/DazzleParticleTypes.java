package agency.highlysuspect.dazzle2.etc;

import agency.highlysuspect.dazzle2.Init;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class DazzleParticleTypes {
	public static final ParticleType<FlareParticleEffect> FLARE = FabricParticleTypes.complex(FlareParticleEffect.FactoryThingie.INSTANCE);
	
	public static void onInitialize() {
		Registry.register(Registry.PARTICLE_TYPE, Init.id("flare"), FLARE);
	}
}
