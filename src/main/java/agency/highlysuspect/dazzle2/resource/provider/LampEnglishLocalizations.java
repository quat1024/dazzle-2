package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class LampEnglishLocalizations extends ResourceProvider {
	public LampEnglishLocalizations(ResourceManager mgr) throws IOException {
		super(mgr);
	}
	
	private static final Set<Identifier> ENGLISH_DIALECTS = new HashSet<>();
	
	static {
		ENGLISH_DIALECTS.add(Init.id("lang/en_us.json"));
		ENGLISH_DIALECTS.add(Init.id("lang/en_au.json"));
		ENGLISH_DIALECTS.add(Init.id("lang/en_ca.json"));
		ENGLISH_DIALECTS.add(Init.id("lang/en_gb.json"));
		ENGLISH_DIALECTS.add(Init.id("lang/en_nz.json"));
	}
	
	@Override
	public Optional<Supplier<InputStream>> get(Identifier id) {
		if(!ENGLISH_DIALECTS.contains(id)) return Optional.empty();
		
		//Originally I kept two sets, for dialects that spell gray with an A and those that spell it with an E
		//Apparently the united states is the only oddball, though, I actually didn't know that lol
		boolean murica = id.getPath().endsWith("en_us.json");
		
		return Optional.of(() -> {
			JsonObject lang = new JsonObject();
			for(LampStyle style : LampStyle.ALL) {
				lang.addProperty("block.dazzle." + style.toName(), style.englishLocalization(murica));
			}
			return writeString(lang.toString());
		});
	}
}
