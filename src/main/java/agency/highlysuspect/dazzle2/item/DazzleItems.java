package agency.highlysuspect.dazzle2.item;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class DazzleItems {
	public static final ItemGroup owo = FabricItemGroupBuilder.build(Init.id("group"), DazzleItems::icon);
	
	public static final List<BlockItem> LAMP_ITEMS = DazzleBlocks.LAMPS.stream().map(DazzleItems::blockItem).collect(Collectors.toList());
	public static final BlockItem LIGHT_SENSOR = blockItem(DazzleBlocks.LIGHT_SENSOR);
	public static final BlockItem INVISIBLE_TORCH = blockItem(DazzleBlocks.INVISIBLE_TORCH);
	public static final BlockItem PROJECTED_LIGHT_PANEL = blockItem(DazzleBlocks.PROJECTED_LIGHT_PANEL);
	
	public static final WallStandingBlockItem DIM_REDSTONE_TORCH = new WallStandingBlockItem(DazzleBlocks.DIM_REDSTONE_TORCH, DazzleBlocks.DIM_REDSTONE_WALL_TORCH, settings());
	
	public static final EnumMap<DyeColor, BlockItem> FLARES = Util.make(new EnumMap<>(DyeColor.class), map -> {
		DazzleBlocks.FLARES.forEach((color, block) -> map.put(color, blockItem(block)));
	});
	
	public static void onInitialize() {
		for(BlockItem item : LAMP_ITEMS) {
			Identifier id = Registry.BLOCK.getId(item.getBlock());
			Registry.register(Registry.ITEM, id, item);
		}
		
		Registry.register(Registry.ITEM, Init.id("light_sensor"), LIGHT_SENSOR);
		Registry.register(Registry.ITEM, Init.id("invisible_torch"), INVISIBLE_TORCH);
		Registry.register(Registry.ITEM, Init.id("projected_light_panel"), PROJECTED_LIGHT_PANEL);
		
		Registry.register(Registry.ITEM, Init.id("dim_redstone_torch"), DIM_REDSTONE_TORCH);
		
		for(BlockItem item : FLARES.values()) {
			Identifier id = Registry.BLOCK.getId(item.getBlock());
			Registry.register(Registry.ITEM, id, item);
		}
	}
	
	private static Item.Settings settings() {
		return new Item.Settings().group(owo);
	}
	
	private static BlockItem blockItem(Block b) {
		return new BlockItem(b, settings());
	}
	
	private static ItemStack icon() {
		//Oh Java, you and your "illegal forward reference"s.
		return new ItemStack(LIGHT_SENSOR);
	}
}
