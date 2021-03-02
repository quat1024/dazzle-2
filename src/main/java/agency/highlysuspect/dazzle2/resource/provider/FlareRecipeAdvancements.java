package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.Junk;
import agency.highlysuspect.dazzle2.LampStyle;
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

public class FlareRecipeAdvancements extends ResourceProvider.IdPathRegexMatch {
	public FlareRecipeAdvancements(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^advancements/recipes/flare/([a-z_]+)_flare\\.json$"));
		template = readResourceAsString(mgr.getResource(Init.id("templates/advancement/flare.json")));
	}
	
	private final String template;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		DyeColor color = DyeColor.byName(name, null);
		if(color == null) return Optional.empty();
		
		Optional<Identifier> dyeItem_ = Junk.itemIdForDye(color);
		if(!dyeItem_.isPresent()) return Optional.empty();
		String dyeItem = dyeItem_.get().toString();
		
		return Optional.of(() -> {
			String unlockedRecipeId = Init.id("recipes/flare/" + name + "_flare.json").toString();
			return writeString(template.replaceAll("DYE", dyeItem).replaceAll("OUT", unlockedRecipeId));
		});
	}
	
	@Override
	public Stream<String> enumerate(String prefix) {
		if(prefix.equals("advancements")) return Arrays.stream(DyeColor.values())
			.map(DyeColor::getName)
			.map(name -> "dazzle:advancements/recipes/flare/" + name + "_flare.json");
		else return Stream.empty();
	}
}
