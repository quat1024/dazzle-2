package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlareItemModels extends ResourceProvider.IdPathRegexMatch {
	public FlareItemModels(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^models/item/([a-z_]+)_flare\\.json$"));
		template = readResourceAsString(mgr.getResource(Init.id("templates/flare_item_model.json")));
	}
	
	private final String template;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		return Optional.of(() -> writeString(template));
	}
}
