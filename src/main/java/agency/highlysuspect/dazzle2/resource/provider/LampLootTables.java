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
import java.util.stream.Stream;

public class LampLootTables extends ResourceProvider.IdPathRegexMatch {
	public LampLootTables(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^loot_tables/blocks/([a-z_]+)\\.json$"));
		template = readResource(mgr.getResource(Init.id("templates/lamp_loot_table_template.json")));
	}
	
	private final String template;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		LampStyle style = LampStyle.fromName(name);
		if(style == null) return Optional.empty();
		else return Optional.of(() -> writeString(template.replaceAll("\\{}", style.toIdentifier().toString())));
	}
	
	@Override
	public Stream<String> enumerate(String prefix) {
		if(prefix.equals("loot_tables")) return LampStyle.ALL.stream()
			.map(LampStyle::toName)
			.map(name -> "dazzle:loot_tables/blocks/" + name + ".json");
		else return Stream.empty();
	}
}
