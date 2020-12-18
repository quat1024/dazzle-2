package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ResourceProvider {
	public ResourceProvider(ResourceManager mgr) throws IOException {
		//I didn't know you could make constructors with a throws clause, actually
	}
	
	public abstract Optional<Supplier<InputStream>> get(Identifier id);
	
	protected static InputStream writeString(String s) {
		return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
	}
	
	protected static String readResource(Resource in) throws IOException {
		return IOUtils.toString(in.getInputStream(), StandardCharsets.UTF_8);
	}
	
	public static abstract class IdPathRegexMatch extends ResourceProvider {
		public IdPathRegexMatch(ResourceManager mgr, Pattern pattern) throws IOException {
			super(mgr);
			this.pattern = pattern;
		}
		
		private final Pattern pattern;
		
		@Override
		public Optional<Supplier<InputStream>> get(Identifier id) {
			Matcher matcher = pattern.matcher(id.getPath());
			if(!matcher.find()) return Optional.empty();
			else return getMatcher(id, matcher);
		}
		
		protected abstract Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher);
	}
	
	public static class LampBlockstates extends IdPathRegexMatch {
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
	
	public static class LampItemModels extends IdPathRegexMatch {
		public LampItemModels(ResourceManager mgr) throws IOException {
			super(mgr, Pattern.compile("^models/item/([a-z_]+)\\.json$"));
			template = readResource(mgr.getResource(Init.id("templates/lamp_item_model.json")));
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
	
	public static class LampLootTables extends IdPathRegexMatch {
		public LampLootTables(ResourceManager mgr) throws IOException {
			super(mgr, Pattern.compile("^loot_tables/blocks/([a-z_]+)\\.json$"));
			template = readResource(mgr.getResource(Init.id("templates/loot_table_template.json")));
		}
		
		private final String template;
		
		@Override
		protected Optional<Supplier<InputStream>> getMatcher(Identifier id, Matcher matcher) {
			String name = matcher.group(1);
			LampStyle style = LampStyle.fromName(name);
			if(style == null) return Optional.empty();
			else return Optional.of(() -> writeString(template.replaceAll("\\{}", style.toIdentifier().toString())));
		}
	}
}