package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.Junk;
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

public class FlareRecipes extends ResourceProvider.IdPathRegexMatch {
	public FlareRecipes(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^recipes/flare/([a-z_]+)_flare\\.json$"));
		
		template = readResourceAsString(mgr.getResource(Init.id("templates/recipe/flare.json")));
	}
	
	private final String template;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		DyeColor color = DyeColor.byName(name, null);
		if(color == null) return Optional.empty();
		
		Optional<Identifier> uwu = Junk.itemIdForDye(color);
		//noinspection OptionalIsPresent (makes it more confusing imo)
		if(!uwu.isPresent()) return Optional.empty();
		
		return Optional.of(() -> {
			String dyeItem = uwu.get().toString();
			String result = Init.id(name + "_flare").toString();
			return writeString(template.replaceAll("DYE", dyeItem).replaceAll("RESULT", result));
		});
	}
	
	@Override
	public Stream<String> enumerate(String prefix) {
		if(prefix.equals("recipes")) return Arrays.stream(DyeColor.values())
			.map(DyeColor::getName)
			.map(name -> "dazzle:recipes/flare/" + name + "_flare.json");
		else return Stream.empty();
	}
}
