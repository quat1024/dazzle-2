package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.FlareBlock;
import agency.highlysuspect.dazzle2.block.LampBlock;
import agency.highlysuspect.dazzle2.item.DazzleItems;
import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.model.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModelGen implements DataProvider {
	public ModelGen(Path outRoot) {
		this.outRoot = outRoot;
	}
	
	private final Path outRoot;
	
	@Override
	public void run(DataCache cache) throws IOException {
		//Model gens have kind of a weird "out-param" interface.
		//Muddied further by Java checked exceptions being poot.
		BiConsumer<Identifier, Supplier<JsonElement>> consumer = (id, jsonSupplier) -> {
			try {
				DataProvider.writeToPath(GenInit.GSON, cache, jsonSupplier.get(), outPath(id));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
		
		//This class is a little bit messy btw.
		//"blockModelParent" uses SimpleModelSupplier and I have to manually write the json.
		//Others use Model#upload which puts them in a different path.
		//This is probably not correct.
		
		//Lamps
		for(LampBlock lamp : DazzleBlocks.LAMPS) {
			blockModelParent(lamp, Init.id("block/lamp/" + lamp.style.theme.name), cache);
		}
		
		//Flare model, flares
		GENERATED_TWOLAYER.upload(Init.id("item/flare"), new Texture()
				.put(TextureKey.LAYER0, Init.id("item/flare/base"))
				.put(           LAYER1, Init.id("item/flare/color")),
			consumer);
		
		for(FlareBlock flare : DazzleBlocks.FLARES.values()) {
			blockModelParent(flare, Init.id("item/flare"), cache);
		}
		
		//Couple more boring models
		layer0(DazzleItems.DIM_REDSTONE_TORCH, Init.id("block/dim_torch/on"), consumer);
		layer0(DazzleItems.INVISIBLE_TORCH, consumer);
		blockModelParent(DazzleBlocks.LIGHT_SENSOR, cache);
		blockModelParent(DazzleBlocks.PROJECTED_LIGHT_PANEL, cache);
		
		for(Block shroom : DazzleBlocks.DYED_SHROOMLIGHTS.values()) {
			blockModelParent(shroom, Init.id("block/shroom/dyed"), cache);
		}
		
		blockModelParent(DazzleBlocks.POLISHED_SHROOMLIGHT, Init.id("block/shroom/polished"), cache);
		
		for(Block shroom : DazzleBlocks.DYED_POLISHED_SHROOMLIGHTS.values()) {
			blockModelParent(shroom, Init.id("block/shroom/dyed_polished"), cache);
		}
	}
	
	@SuppressWarnings("SameParameterValue")
	private static void layer0(ItemConvertible item, BiConsumer<Identifier, Supplier<JsonElement>> consumer) {
		Item i = item.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(i), Texture.layer0(i), consumer);
	}
	
	@SuppressWarnings("SameParameterValue")
	private static void layer0(ItemConvertible item, Identifier tex, BiConsumer<Identifier, Supplier<JsonElement>> consumer) {
		Item i = item.asItem();
		Models.GENERATED.upload(ModelIds.getItemModelId(i), Texture.layer0(tex), consumer);
	}
	
	private void blockModelParent(Block b, DataCache cache) throws IOException {
		blockModelParent(b, ModelIds.getBlockModelId(b), cache);
	}
	
	private void blockModelParent(Block b, Identifier modelId, DataCache cache) throws IOException {
		Identifier id = Registry.BLOCK.getId(b);
		JsonElement modelJson = new SimpleModelSupplier(modelId).get();
		DataProvider.writeToPath(GenInit.GSON, cache, modelJson, itemModelOutPath(id));
	}
	
	private Path outPath(Identifier id) {
		return outRoot.resolve("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json");
	}
	
	private Path itemModelOutPath(Identifier id) {
		return outRoot.resolve("assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json");
	}
	
	public static final TextureKey LAYER1 = reflectTextureKey("layer1");
	public static final Model GENERATED_TWOLAYER = new Model(Optional.of(new Identifier("minecraft", "item/generated")), Optional.empty(), TextureKey.LAYER0, LAYER1);
	
	//Annoyingly this is a private constructor
	@SuppressWarnings("SameParameterValue")
	private static TextureKey reflectTextureKey(String name) {
		try {
			Method xd = TextureKey.class.getDeclaredMethod("method_27043", String.class); //It's not mapped
			xd.setAccessible(true);
			return (TextureKey) xd.invoke(null, name);
		} catch(Exception yooo) {
			throw new RuntimeException(yooo);
		}
	}
	
	@Override
	public String getName() {
		return "dazzle 2 item models!!";
	}
}
