package agency.highlysuspect.dazzle2.etc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class FlareParticleEffect implements ParticleEffect {
	public FlareParticleEffect(int color) {
		this.color = color;
	}
	
	public final int color;
	
	@Override
	public ParticleType<?> getType() {
		return DazzleParticleTypes.FLARE;
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(color);
	}
	
	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(DazzleParticleTypes.FLARE).toString();
	}
	
	public static class FactoryThingie implements ParticleEffect.Factory<FlareParticleEffect> {
		public static final FactoryThingie INSTANCE = new FactoryThingie();
		
		@Override
		public FlareParticleEffect read(ParticleType<FlareParticleEffect> type, StringReader reader) throws CommandSyntaxException {
			//TODO
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("i dont wanna write a command parser lol");
		}
		
		@Override
		public FlareParticleEffect read(ParticleType<FlareParticleEffect> type, PacketByteBuf buf) {
			int color = buf.readInt();
			return new FlareParticleEffect(color);
		}
	}
}
