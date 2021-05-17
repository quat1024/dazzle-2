package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.FlareBlock;
import agency.highlysuspect.dazzle2.block.LampBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.model.*;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
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
			write(cache, model(lamp, Init.id("block/lamp/" + lamp.style.theme.name)));
		}
		
		for(FlareBlock flare : DazzleBlocks.FLARES.values()) {
			write(cache, model(flare, Init.id("block/flare")));
		}
		
		write(cache, empty(DazzleBlocks.INVISIBLE_TORCH));
		write(cache, empty(DazzleBlocks.LIGHT_AIR));
		
		write(cache, model(DazzleBlocks.LIGHT_SENSOR).coordinate(northDefaultSixWayRotation()));
		write(cache, model(DazzleBlocks.PROJECTED_LIGHT_PANEL).coordinate(upDefaultSixWayRotation()));
		
		write(cache, VariantsBlockStateSupplier.create(DazzleBlocks.DIM_REDSTONE_TORCH)
			.coordinate(booleanModelMap(Properties.LIT, Init.id("block/dim_torch/on"), Init.id("block/dim_torch/off"))));
		
		write(cache, VariantsBlockStateSupplier.create(DazzleBlocks.DIM_REDSTONE_WALL_TORCH)
			.coordinate(eastDefaultFourWayRotation())
			.coordinate(booleanModelMap(Properties.LIT, Init.id("block/dim_torch/wall_on"), Init.id("block/dim_torch/wall_off"))));
		
		for(Block shroom : DazzleBlocks.DYED_SHROOMLIGHTS.values()) {
			write(cache, model(shroom, Init.id("block/shroom/dyed")));
		}
		
		write(cache, model(DazzleBlocks.POLISHED_SHROOMLIGHT, Init.id("block/shroom/polished")));
		
		for(Block shroom : DazzleBlocks.DYED_POLISHED_SHROOMLIGHTS.values()) {
			write(cache, model(shroom, Init.id("block/shroom/dyed_polished")));
		}
		
		for(Block rod : DazzleBlocks.DYED_END_RODS.values()) {
			write(cache, model(rod, Init.id("block/end_rod")).coordinate(upDefaultSixWayRotation()));
		}
	}
	
	//All blockstates map to this block model.
	private VariantsBlockStateSupplier model(Block b, Identifier modelPath) {
		return VariantsBlockStateSupplier.create(b, BlockStateVariant.create().put(VariantSettings.MODEL, modelPath));
	}
	
	//All blockstates map to the item model named after the block.
	//e.g. dazzle:example maps to the model dazzle:block/example.json
	private VariantsBlockStateSupplier model(Block b) {
		return model(b, ModelIds.getBlockModelId(b));
	}
	
	//All blockstates map to the `dazzle:empty` model.
	private VariantsBlockStateSupplier empty(Block b) {
		return model(b, Init.id("empty"));
	}
	
	private void write(DataCache cache, VariantsBlockStateSupplier v) throws IOException {
		Identifier id = Registry.BLOCK.getId(v.getBlock());
		DataProvider.writeToPath(GenInit.GSON, cache, v.get(), outPath(id));
	}
	
	//Copy and paste from BlockStateModelGenerator, it's private.
	private static BlockStateVariantMap northDefaultSixWayRotation() {
		return BlockStateVariantMap.create(Properties.FACING)
			.register(Direction.NORTH, BlockStateVariant.create())
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
			.register(Direction.UP, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
			.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90));
	}
	
	//Also copy and paste
	private static BlockStateVariantMap upDefaultSixWayRotation() {
		return BlockStateVariantMap.create(Properties.FACING)
			.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180))
			.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
			.register(Direction.UP, BlockStateVariant.create())
			.register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180));
	}
	
	//Yet another copy and paste.
	public static BlockStateVariantMap eastDefaultFourWayRotation() { //for torches
		return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
			.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
			.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
			.register(Direction.EAST, BlockStateVariant.create())
			.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180));
	}
	
	//Another copy and paste
	@SuppressWarnings("SameParameterValue")
	private static BlockStateVariantMap booleanModelMap(BooleanProperty prop, Identifier on, Identifier off) {
		return BlockStateVariantMap.create(prop)
			.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, on))
			.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, off));
	}
	
	private Path outPath(Identifier id) {
		return outRoot.resolve("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
	}
	
	@Override
	public String getName() {
		return "dazzle 2 blockstate definitions!!";
	}
}
