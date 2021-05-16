package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.FlareBlock;
import agency.highlysuspect.dazzle2.block.LampBlock;
import com.google.gson.JsonElement;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.VariantSettings;
import net.minecraft.data.client.model.VariantsBlockStateSupplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;

public class BlockStateGen implements DataProvider {
	public BlockStateGen(Path outRoot) {
		this.outRoot = outRoot;
	}
	
	private final Path outRoot;
	
	@Override
	public void run(DataCache cache) throws IOException {
		for(LampBlock lamp : DazzleBlocks.LAMPS) {
			Identifier id = Registry.BLOCK.getId(lamp);
			
			JsonElement json = VariantsBlockStateSupplier.create(lamp,
				BlockStateVariant.create() //the empty blockstate ""
					.put(VariantSettings.MODEL, Init.id("block/lamp/" + lamp.style.theme.name)))
				.get(); //as json
			
			DataProvider.writeToPath(GenInit.GSON, cache, json, outPath(id));
		}
		
		for(FlareBlock flare : DazzleBlocks.FLARES.values()) {
			Identifier id = Registry.BLOCK.getId(flare);
			
			JsonElement json = VariantsBlockStateSupplier.create(flare,
				BlockStateVariant.create() //the empty blockstate ""
					.put(VariantSettings.MODEL, Init.id("block/flare")))
				.get(); //as json
			
			DataProvider.writeToPath(GenInit.GSON, cache, json, outPath(id));
		}
	}
	
	private Path outPath(Identifier id) {
		return outRoot.resolve("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
	}
	
	@Override
	public String getName() {
		return "dazzle 2 blockstate definitions!!";
	}
}
