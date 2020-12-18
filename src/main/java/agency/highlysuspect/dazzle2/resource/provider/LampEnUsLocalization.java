package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

public class LampEnUsLocalization extends ResourceProvider {
	public LampEnUsLocalization(ResourceManager mgr) throws IOException {
		super(mgr);
	}
	
	public static final Identifier EN_US_LANG = Init.id("lang/en_us.json");
	
	@Override
	public Optional<Supplier<InputStream>> get(Identifier id) {
		if(!EN_US_LANG.equals(id)) return Optional.empty();
		else return Optional.of(() -> {
			JsonObject lang = new JsonObject();
			for(LampStyle style : LampStyle.ALL) {
				lang.addProperty("block.dazzle." + style.toName(), style.toEnUsLocalization());
			}
			return writeString(lang.toString());
		});
	}
}
