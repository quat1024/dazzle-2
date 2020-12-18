package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LampBlockstates extends ResourceProvider.IdPathRegexMatch {
	public LampBlockstates(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^blockstates/([a-z_]+)\\.json$"));
		template = readResource(mgr.getResource(Init.id("templates/lamp_blockstate.json")));
	}
	
	private final String template;
	
	@Override
	public Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		LampStyle style = LampStyle.fromName(name);
		if(style == null) return Optional.empty();
		else return Optional.of(() -> writeString(template.replaceAll("\\{}", style.theme.name)));
	}
}
