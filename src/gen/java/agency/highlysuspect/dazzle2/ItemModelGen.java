package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.FlareBlock;
import agency.highlysuspect.dazzle2.block.LampBlock;
import com.google.gson.JsonElement;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.model.SimpleModelSupplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;

public class ItemModelGen implements DataProvider {
	public ItemModelGen(Path outRoot) {
		this.outRoot = outRoot;
	}
	
	private final Path outRoot;
	
	@Override
	public void run(DataCache cache) throws IOException {
		//Model gens have kind of a weird "out-param" interface.
		//Muddied further by Java checked exceptions being poot.
		
//		BiConsumer<Identifier, Supplier<JsonElement>> consumer = (id, jsonSupplier) -> {
//			try {
//				DataProvider.writeToPath(GenInit.GSON, cache, jsonSupplier.get(), outPath(id));
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//		};
		//..is what i would do if i wanted to generate some item/generated models or whatnot.
		//In this case, all I need to do is write some "parent": "other thing" models.
		
		for(LampBlock lamp : DazzleBlocks.LAMPS) {
			Identifier id = Registry.BLOCK.getId(lamp);
			JsonElement modelJson = new SimpleModelSupplier(Init.id("block/lamp/" + lamp.style.theme.name)).get();
			DataProvider.writeToPath(GenInit.GSON, cache, modelJson, outPath(id));
		}
		
		//originally (with my janky string-replacement datagens) flare item models were done by copypasting the contents of
		//what is now `dazzle/models/item/flare.json` into each flare's individual item model json.
		//I think having that file exist one time, in non-generated assets, and pointing to it 16 times is more appropriate.
		for(FlareBlock flare : DazzleBlocks.FLARES.values()) {
			Identifier id = Registry.BLOCK.getId(flare);
			JsonElement modelJson = new SimpleModelSupplier(Init.id("item/flare")).get();
			DataProvider.writeToPath(GenInit.GSON, cache, modelJson, outPath(id));
		}
	}
	
	private Path outPath(Identifier id) {
		return outRoot.resolve("assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json");
	}
	
	@Override
	public String getName() {
		return "dazzle 2 item models!!";
	}
}
