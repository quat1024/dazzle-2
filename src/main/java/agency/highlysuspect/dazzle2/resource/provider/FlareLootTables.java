package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FlareLootTables extends ResourceProvider.IdPathRegexMatch {
	public FlareLootTables(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^loot_tables/blocks/([a-z_]+)_flare\\.json$"));
		template = readResourceAsString(mgr.getResource(Init.id("templates/simple_loot_table.json")));
	}
	
	private final String template;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		DyeColor color = DyeColor.byName(name, null);
		if(color == null) return Optional.empty();
		else return Optional.of(() -> {
			Identifier itemId = Init.id(color.getName() + "_flare");
			return writeString(template.replaceAll("\\{}", itemId.toString()));
		});
	}
	
	@Override
	public Stream<String> enumerate(String prefix) {
		if(prefix.equals("loot_tables")) return Arrays.stream(DyeColor.values())
			.map(DyeColor::getName)
			.map(name -> "dazzle:loot_tables/blocks/" + name + "_flare.json");
		else return Stream.empty();
	}
}