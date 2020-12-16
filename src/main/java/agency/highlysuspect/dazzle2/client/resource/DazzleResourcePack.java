package agency.highlysuspect.dazzle2.client.resource;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.client.resource.provider.ResourceProvider;
import com.google.common.collect.ImmutableList;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DazzleResourcePack implements ResourcePack {
	private static final Set<String> NAMESPACE = Util.make(new HashSet<>(), s -> s.add(Init.MODID));
	
	private static final List<ResourceProvider> PROVIDERS = ImmutableList.of(
		new ResourceProvider.LampBlockstates(),
		new ResourceProvider.LampModels(),
		new ResourceProvider.LampItemModels()
	);
	
	@Override
	public InputStream openRoot(String fileName) throws IOException {
		//TODO make an abstraction for providing openRoot files? dont think it's needed here
		Init.log("DazzleResourcePack#openRoot " + fileName);
		return null;
	}
	
	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		if(!id.getNamespace().equals(Init.MODID)) return null;
		
		for(ResourceProvider p : PROVIDERS) {
			Optional<Supplier<InputStream>> input = p.get(id);
			if(input.isPresent()) return input.get().get();
		}
		
		return null;
	}
	
	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		//TODO make an abstraction for providing entries to pass to findResources
		// this is needed for things like recipes im pretty sure, which there is no fixed set of
		Init.log("DazzleResourcePack#findResources " + prefix);
		return Collections.emptyList();
	}
	
	@Override
	public boolean contains(ResourceType type, Identifier id) {
		if(!id.getNamespace().equals(Init.MODID)) return false;
		
		for(ResourceProvider p : PROVIDERS) {
			if(p.get(id).isPresent()) return true;
		}
		
		return false;
	}
	
	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return NAMESPACE;
	}
	
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return null;
	}
	
	@Override
	public String getName() {
		return "Dazzle 2 - Generated resources";
	}
	
	@Override
	public void close() {
		//Nothing to do
	}
}
