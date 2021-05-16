package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.FlareBlock;
import agency.highlysuspect.dazzle2.block.LampBlock;
import agency.highlysuspect.dazzle2.item.DazzleItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TagGen implements DataProvider {
	public TagGen(Path outRoot) {
		this.outRoot = outRoot;
	}
	
	private final Path outRoot;
	
	@Override
	public void run(DataCache cache) throws IOException {
		Map<LampStyle.Color, BlockAndItemTagBuilderWrapper> lampsByColor = new HashMap<>();
		Map<LampStyle.Mode, BlockAndItemTagBuilderWrapper> lampsByMode = new HashMap<>();
		Map<LampStyle.Theme, BlockAndItemTagBuilderWrapper> lampsByTheme = new HashMap<>();
		
		for(LampBlock lamp : DazzleBlocks.LAMPS) {
			lampsByColor.computeIfAbsent(lamp.style.color, x -> BlockAndItemTagBuilderWrapper.create(outRoot, Init.id("lamps/by_color/" + x.getName()))).add(lamp);
			lampsByMode.computeIfAbsent(lamp.style.mode, x -> BlockAndItemTagBuilderWrapper.create(outRoot, Init.id("lamps/by_mode/" + x.getName()))).add(lamp);
			lampsByTheme.computeIfAbsent(lamp.style.theme, x -> BlockAndItemTagBuilderWrapper.create(outRoot, Init.id("lamps/by_theme/" + x.getName()))).add(lamp);
		}
		
		for(BlockAndItemTagBuilderWrapper a : lampsByColor.values()) { a.save(cache); }
		for(BlockAndItemTagBuilderWrapper a : lampsByMode.values())  { a.save(cache); }
		for(BlockAndItemTagBuilderWrapper a : lampsByTheme.values()) { a.save(cache); }
		
		BlockAndItemTagBuilderWrapper.create(outRoot, Init.id("lamps/all"))
			.addAll(DazzleBlocks.LAMPS)
			.save(cache);
		
		//With that out of the way
		
		BlockAndItemTagBuilderWrapper.create(outRoot, Init.id("flares"))
			.addAll(DazzleBlocks.FLARES.values())
			.save(cache);
		
		BlockAndItemTagBuilderWrapper.create(outRoot, new Identifier("c", "redstone_lamps"))
			.addOptionalTag(Init.id("lamps/all"))
			.save(cache);
		
		//Worth noting that adding two of the same thing to a tag (here, "wall" and "standing" torch blocks map to the same item) is okay, it all gets flattened.
		BlockAndItemTagBuilderWrapper.create(outRoot, new Identifier("c", "torches"))
			.add(Blocks.TORCH, Blocks.WALL_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH)
			.add(DazzleBlocks.INVISIBLE_TORCH, DazzleBlocks.DIM_REDSTONE_TORCH, DazzleBlocks.DIM_REDSTONE_WALL_TORCH)
			.save(cache);
		
		TagBuilderWrapper.items(outRoot, Init.id("wrenches"))
			.addOptionalTag(new Identifier("c", "wrenches"))
			.add(Items.REDSTONE_TORCH, DazzleItems.INVISIBLE_TORCH, DazzleItems.DIM_REDSTONE_TORCH)
			.save(cache);
		
		TagBuilderWrapper.blocks(outRoot, Init.id("make_invisible_torches"))
			.addOptionalTag(new Identifier("c", "torches"))
			.add(Blocks.TORCH, Blocks.WALL_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH)
			.save(cache);
	}
	
	//Based on a copy-paste of abstract class AbstractTagProvider.ObjectBuilder<T>.
	//It has a private constructor so I couldn't use it anyway.
	//Swaps out an interface to the weird "source" system on tag.builder (which seems to go unused) with the ID of the tag you're building.
	//Also added a method that uses that ID to save the tag.
	public static class TagBuilderWrapper<T> {
		public TagBuilderWrapper(Registry<T> registry, Path outRoot, String classifier, Identifier tagId) {
			this.tagBuilder = new Tag.Builder();
			this.reg = registry;
			this.outRoot = outRoot;
			this.classifier = classifier;
			this.tagId = tagId;
		}
		
		public static TagBuilderWrapper<Block> blocks(Path outRoot, Identifier tagId) {
			return new TagBuilderWrapper<>(Registry.BLOCK, outRoot, "blocks", tagId);
		}
		
		public static TagBuilderWrapper<Item> items(Path outRoot, Identifier tagId) {
			return new TagBuilderWrapper<>(Registry.ITEM, outRoot, "items", tagId);
		}
		
		public final Tag.Builder tagBuilder;
		public final Registry<T> reg;
		public final Path outRoot;
		public final String classifier;
		public final Identifier tagId;
		
		public TagBuilderWrapper<T> add(T element) {
			this.tagBuilder.add(this.reg.getId(element), "Dazzle 2");
			return this;
		}
		
		public TagBuilderWrapper<T> addTag(Identifier id) {
			this.tagBuilder.addTag(id, "Dazzle 2");
			return this;
		}
		
		public TagBuilderWrapper<T> addOptionalTag(Identifier id) {
			this.tagBuilder.add(new Tag.OptionalTagEntry(id), "Dazzle 2");
			return this;
		}
		
		@SafeVarargs
		public final TagBuilderWrapper<T> add(T... objects) {
			Stream.of(objects).map(this.reg::getId).forEach(id -> this.tagBuilder.add(id, "Dazzle 2"));
			return this;
		}
		
		public void save(DataCache cache) throws IOException {
			DataProvider.writeToPath(GenInit.GSON, cache, tagBuilder.toJson(), getPath());
		}
		
		public Path getPath() {
			return outRoot.resolve("data/" + tagId.getNamespace() + "/tags/" + classifier + "/" + tagId.getPath() + ".json");
		}
	}
	
	//A further wrapper to make sibling tags for blocks and items, when you want them to both be the same.
	public static class BlockAndItemTagBuilderWrapper {
		public BlockAndItemTagBuilderWrapper(Path outRoot, Identifier tagId) {
			blocks = TagBuilderWrapper.blocks(outRoot, tagId);
			items = TagBuilderWrapper.items(outRoot, tagId);
		}
		
		public static BlockAndItemTagBuilderWrapper create(Path outRoot, Identifier tagId) {
			return new BlockAndItemTagBuilderWrapper(outRoot, tagId);
		}
		
		public final TagBuilderWrapper<Block> blocks;
		public final TagBuilderWrapper<Item> items;
		
		public BlockAndItemTagBuilderWrapper add(Block b) {
			blocks.add(b);
			items.add(b.asItem());
			return this;
		}
		
		public BlockAndItemTagBuilderWrapper addOptionalTag(Identifier id) {
			blocks.addOptionalTag(id);
			items.addOptionalTag(id);
			return this;
		}
		
		public BlockAndItemTagBuilderWrapper add(Block... b) {
			blocks.add(b);
			for(Block bb : b) {
				items.add(bb.asItem());
			}
			return this;
		}
		
		public BlockAndItemTagBuilderWrapper addAll(Collection<? extends Block> b) {
			for(Block bb : b) {
				add(bb);
			}
			return this;
		}
		
		public void save(DataCache cache) throws IOException {
			blocks.save(cache);
			items.save(cache);
		}
	}
	
	@Override
	public String getName() {
		return "dazzle tags!!";
	}
}
