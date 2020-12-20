package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
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

public class LampRecipes extends ResourceProvider.IdPathRegexMatch {
	public LampRecipes(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^recipes/lamps/([a-z_]+)\\.json$"));
		
		for(LampStyle.Theme theme : LampStyle.Theme.ALL) {
			digitalRecipeTemplates.put(theme, readResource(mgr.getResource(Init.id("templates/recipe/" + theme.name + ".json"))));
		}
		
		convertToAnalogTemplate = readResource(mgr.getResource(Init.id("templates/recipe/convert_to_analog.json")));
	}
	
	private final Map<LampStyle.Theme, String> digitalRecipeTemplates = new HashMap<>();
	private final String convertToAnalogTemplate;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		LampStyle style = LampStyle.fromName(name);
		if(style == null) return Optional.empty();
		else return Optional.of(() -> {
			if(style.mode == LampStyle.Mode.DIGITAL) {
				String dyeId = style.color.itemId().toString();
				String out = style.toIdentifier().toString();
				String group = style.theme.name + '_' + style.mode.name;
				
				return writeString(digitalRecipeTemplates.get(style.theme)
					.replaceAll("DYE", dyeId)
					.replaceAll("OUT", out)
					.replaceAll("GROUP", group)
				);
			} else {
				String in = style.withMode(LampStyle.Mode.DIGITAL).toIdentifier().toString();
				String out = style.toIdentifier().toString();
				String group = style.theme.name + '_' + style.mode.name + "_analog_convert";
				
				return writeString(convertToAnalogTemplate
					.replaceAll("IN", in)
					.replaceAll("OUT", out)
					.replaceAll("GROUP", group)
				);
			}
		});
	}
	
	@Override
	public Stream<String> enumerate(String prefix) {
		if(prefix.equals("recipes")) return LampStyle.ALL.stream()
			.map(LampStyle::toName)
			.map(name -> "dazzle:recipes/lamps/" + name + ".json");
		else return Stream.empty();
	}
}
