package agency.highlysuspect.dazzle2.etc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

//Copypaste of FlareParticleEffect
//Don't learn how to write particles from this. I'm just slapping stuff together.
public class DyedEndRodParticleEffect implements ParticleEffect {
	public DyedEndRodParticleEffect(int color) {
		this.color = color;
	}
	
	public final int color;
	
	@Override
	public ParticleType<?> getType() {
		return DazzleParticleTypes.DYED_END_ROD;
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(color);
	}
	
	@SuppressWarnings("ConstantConditions") //The particle was registered.
	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(DazzleParticleTypes.DYED_END_ROD).toString();
	}
	
	public static class FactoryThingie implements Factory<DyedEndRodParticleEffect> {
		public static final FactoryThingie INSTANCE = new FactoryThingie();
		
		@Override
		public DyedEndRodParticleEffect read(ParticleType<DyedEndRodParticleEffect> type, StringReader reader) throws CommandSyntaxException {
			//TODO
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("i dont wanna write a command parser lol");
		}
		
		@Override
		public DyedEndRodParticleEffect read(ParticleType<DyedEndRodParticleEffect> type, PacketByteBuf buf) {
			int color = buf.readInt();
			return new DyedEndRodParticleEffect(color);
		}
	}
}
