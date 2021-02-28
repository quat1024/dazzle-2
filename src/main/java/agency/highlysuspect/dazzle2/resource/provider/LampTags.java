package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LampTags extends ResourceProvider.IdPathRegexMatch {
	public LampTags(ResourceManager mgr) throws IOException {
		super(mgr, Pattern.compile("^tags/(blocks|items)/lamps_by_(color|theme|mode)/([a-z_]+)\\.json$"));
	}
	
	@Override
	protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
		String tagType = matcher.group(2);
		String tagValue = matcher.group(3);
		
		final Function<LampStyle, String> extractor;
		switch(tagType) {
			case "color": extractor = style -> style.color.color.getName(); break;
			case "theme": extractor = style -> style.theme.name; break;
			case "mode": extractor = style -> style.mode.name; break;
			default: return Optional.empty();
		}
		
		return Optional.of(() -> {
			JsonArray arr = new JsonArray();
			for(LampStyle style : LampStyle.ALL) {
				if(extractor.apply(style).equals(tagValue)) {
					arr.add(style.toIdentifier().toString());
				}
			}
			
			JsonObject obj = new JsonObject();
			obj.addProperty("replace", false);
			obj.add("values", arr);
			return writeString(obj.toString());
		});
	}
	
	@Override
	public Stream<String> enumerate(String prefix) {
		//TODO: Why does this not always..... work?????????????
		// It's not that this method fails, but if you println "prefix", sometimes the game literally isn't even asking to load item tags??
		// What??
		
		if(prefix.equals("tags/items") || prefix.equals("tags/blocks")) {
			prefix = "dazzle:" + prefix;
			List<String> goodStream = new ArrayList<>();
			for(LampStyle.Color c : LampStyle.Color.ALL) {
				goodStream.add(prefix + "/lamps_by_color/" + c.color.getName() + ".json");
			}
			for(LampStyle.Theme t : LampStyle.Theme.ALL) {
				goodStream.add(prefix + "/lamps_by_theme/" + t.name + ".json");
			}
			for(LampStyle.Mode m : LampStyle.Mode.ALL) {
				goodStream.add(prefix + "/lamps_by_mode/" + m.name + ".json");
			}
			return goodStream.stream();
		} else return Stream.empty();
	}
}
