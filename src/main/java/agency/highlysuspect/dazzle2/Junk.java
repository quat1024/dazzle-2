package agency.highlysuspect.dazzle2;

import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class Junk {
	public static Identifier mapPath(Identifier id, UnaryOperator<String> mapper) {
		return new Identifier(id.getNamespace(), mapper.apply(id.getPath()));
	}
}
