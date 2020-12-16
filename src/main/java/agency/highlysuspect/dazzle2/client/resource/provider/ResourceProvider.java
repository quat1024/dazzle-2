package agency.highlysuspect.dazzle2.client.resource.provider;

import agency.highlysuspect.dazzle2.LampStyle;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ResourceProvider {
	Optional<Supplier<InputStream>> get(Identifier id);
	
	static InputStream stringInputStream(String s) {
		return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
	}
	
	class LampBlockstates implements ResourceProvider {
		static final Pattern PATTERN = Pattern.compile("^blockstates/([a-z_]+)\\.json$");
		
		@Override
		public Optional<Supplier<InputStream>> get(Identifier id) {
			Matcher matcher = PATTERN.matcher(id.getPath());
			if(!matcher.find()) return Optional.empty();
			
			String name = matcher.group(1);
			LampStyle style = LampStyle.fromName(name);
			if(style == null) return Optional.empty();
			
			//Hey quick TODO this is yucky and I should load this from some kind of "template" json file.
			//           The trick is that, as a resource pack, i can't easily load files from a resource manager.
			//           So i will probably need to delegate somehow, without creating infinite loops and shit
			
			else return Optional.of(() -> stringInputStream("{\n" +
				"\t\"variants\": {\n" +
				"\t\t\"\": {\n" +
				"\t\t\t\"model\": \"dazzle:block/" + name + "\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}"));
		}
	}
	
	class LampModels implements ResourceProvider {
		static final Pattern PATTERN = Pattern.compile("^models/block/([a-z_]+)\\.json$");
		
		@Override
		public Optional<Supplier<InputStream>> get(Identifier id) {
			Matcher matcher = PATTERN.matcher(id.getPath());
			if(!matcher.find()) return Optional.empty();
			
			String name = matcher.group(1);
			LampStyle style = LampStyle.fromName(name);
			if(style == null) return Optional.empty();
			
			//Oh god, the hacks are abysmal! Make it stop!
			if(style.theme == LampStyle.ThemeProp.PULSATING) {
				return Optional.of(() -> stringInputStream("{\n" +
					"\t\"parent\": \"dazzle:block/lamp_base_pulsating\",\n" +
					"\t\"textures\": {\n" +
					"\t\t\"base\": \"dazzle:block/lamp/" + style.theme.name + "/base\",\n" +
					"\t\t\"side\": \"dazzle:block/lamp/" + style.theme.name + "/side\",\n" +
					"\t\t\"end\": \"dazzle:block/lamp/" + style.theme.name + "/top\"\n" +
					"\t}\n" +
					"}"));
			} else {
				return Optional.of(() -> stringInputStream("{\n" +
					"\t\"parent\": \"dazzle:block/lamp_base\",\n" +
					"\t\"textures\": {\n" +
					"\t\t\"base\": \"dazzle:block/lamp/" + style.theme.name + "/base\",\n" +
					"\t\t\"overlay\": \"dazzle:block/lamp/" + style.theme.name + "/overlay\"\n" +
					"\t}\n" +
					"}"));
			}
		}
	}
	
	class LampItemModels implements ResourceProvider {
		static final Pattern PATTERN = Pattern.compile("^models/item/([a-z_]+)\\.json$");
		
		@Override
		public Optional<Supplier<InputStream>> get(Identifier id) {
			Matcher matcher = PATTERN.matcher(id.getPath());
			if(!matcher.find()) return Optional.empty();
			
			String name = matcher.group(1);
			LampStyle style = LampStyle.fromName(name);
			if(style == null) return Optional.empty();
			
			else return Optional.of(() -> stringInputStream("{\n" +
				"\t\"parent\": \"dazzle:block/" + name + "\"\n" +
				"}"));
		}
	}
}
