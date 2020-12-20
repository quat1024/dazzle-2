package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.Junk;
import agency.highlysuspect.dazzle2.LampStyle;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LampRecipeAdvancements extends ResourceProvider.IdPathRegexMatch {
	public LampRecipeAdvancements(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^advancements/recipes/lamps/([a-z_]+)\\.json$"));
		
		for(LampStyle.Theme theme : LampStyle.Theme.ALL) {
			recipeAdv.put(theme, readResource(mgr.getResource(Init.id("templates/advancement/" + theme.name + ".json"))));
		}
		
		recipeAnalogAdv = readResource(mgr.getResource(Init.id("templates/advancement/convert_to_analog.json")));
	}
	
	private final Map<LampStyle.Theme, String> recipeAdv = new HashMap<>();
	private final String recipeAnalogAdv;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		LampStyle style = LampStyle.fromName(name);
		if(style == null) return Optional.empty();
		
		else return Optional.of(() -> {
			if(style.mode == LampStyle.Mode.DIGITAL) {
				String dyeId = style.color.itemId().toString();
				//This is really fucking bad lmao
				String unlockedRecipeId = Junk.mapPath(style.toIdentifier(), LampRecipeAdvancements::toRecipeId).toString();
				return writeString(recipeAdv.get(style.theme)
					.replaceAll("IN", dyeId)
					.replaceAll("OUT", unlockedRecipeId)
				);
			} else {
				String baseItemId = style.withMode(LampStyle.Mode.DIGITAL).toIdentifier().toString();
				String unlockedRecipeId = Junk.mapPath(style.toIdentifier(), LampRecipeAdvancements::toRecipeId).toString();
				return writeString(recipeAnalogAdv
					.replaceAll("IN", baseItemId)
					.replaceAll("OUT", unlockedRecipeId)
				);
			}
		});
	}
	
	private static String toRecipeId(String s) {
		//crosscheck LampRecipes - yeah i know this is spaghetti
		return "lamps/" + s;
	}
	
	@Override
	public Stream<String> enumerate(String prefix) {
		if(prefix.equals("advancements")) return LampStyle.ALL.stream()
			.map(LampStyle::toName)
			.map(name -> "dazzle:advancements/recipes/lamps/" + name + ".json");
		else return Stream.empty();
	}
}
