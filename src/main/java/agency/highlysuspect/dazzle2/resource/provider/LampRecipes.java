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
			digitalRecipeTemplates.put(theme, readResourceAsString(mgr.getResource(Init.id("templates/recipe/lamps/" + theme.name + ".json"))));
		}
		
		convertToAnalogTemplate = readResourceAsString(mgr.getResource(Init.id("templates/recipe/lamps/convert_to_analog.json")));
	}
	
	private final Map<LampStyle.Theme, String> digitalRecipeTemplates = new HashMap<>();
	private final String convertToAnalogTemplate;
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String name = matcher.group(1);
		LampStyle style = LampStyle.fromName(name);
		if(style == null) return Optional.empty();
		
		String template;
		String in;
		String groupSuffix;
		if(style.mode == LampStyle.Mode.DIGITAL) {
			template = digitalRecipeTemplates.get(style.theme);
			
			Optional<Identifier> in_ = style.color.findItemId();
			if(!in_.isPresent()) return Optional.empty();
			in = in_.get().toString();
			
			groupSuffix = "";
		} else if(style.mode == LampStyle.Mode.ANALOG) {
			template = convertToAnalogTemplate;
			
			in = style.withMode(LampStyle.Mode.DIGITAL).toIdentifier().toString();
			groupSuffix = "_analog_convert";
		} else {
			throw new IllegalStateException("Imagine if Java had exhaustiveness checking, lmao, anyway unknown lamp mode " + style.mode + " go hiss at quat");
		}
		
		return Optional.of(() -> {
			String out = style.toIdentifier().toString();
			String group = style.theme.name + '_' + style.mode.name + groupSuffix;
			return writeString(template.replaceAll("IN", in).replaceAll("OUT", out).replaceAll("GROUP", group));
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
