package agency.highlysuspect.dazzle2.resource.provider;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.LampStyle;
import com.google.common.collect.Iterators;
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
import java.util.stream.Stream;

public abstract class ResourceProvider {
	public ResourceProvider(ResourceManager mgr) throws IOException {
		//I didn't know you could make constructors with a throws clause, actually
	}
	
	public abstract Optional<Supplier<InputStream>> get(Identifier id);
	
	public Stream<String> enumerate(String prefix) {
		return Stream.empty();
	}
	
	protected static InputStream writeString(String s) {
		return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
	}
	
	protected static String readResourceAsString(Resource in) throws IOException {
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
	
}