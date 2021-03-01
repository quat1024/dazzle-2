package agency.highlysuspect.dazzle2.item;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class DazzleItems {
	public static final ItemGroup owo = FabricItemGroupBuilder.build(Init.id("group"), DazzleItems::icon);
	
	public static final List<BlockItem> LAMP_ITEMS = DazzleBlocks.LAMPS.stream().map(DazzleItems::blockItem).collect(Collectors.toList());
	public static final BlockItem LIGHT_SENSOR = blockItem(DazzleBlocks.LIGHT_SENSOR);
	public static final BlockItem HIDDEN_LIGHT = blockItem(DazzleBlocks.PLACEABLE_HIDDEN_LIGHT);
	public static final BlockItem PROJECTED_LIGHT_PANEL = blockItem(DazzleBlocks.PROJECTED_LIGHT_PANEL);
	
	public static void onInitialize() {
		for(BlockItem item : LAMP_ITEMS) {
			Identifier id = Registry.BLOCK.getId(item.getBlock());
			Registry.register(Registry.ITEM, id, item);
		}
		
		Registry.register(Registry.ITEM, Init.id("light_sensor"), LIGHT_SENSOR);
		Registry.register(Registry.ITEM, Init.id("hidden_light"), HIDDEN_LIGHT);
		Registry.register(Registry.ITEM, Init.id("projected_light_panel"), PROJECTED_LIGHT_PANEL);
	}
	
	private static BlockItem blockItem(Block b) {
		return new BlockItem(b, new Item.Settings().group(owo));
	}
	
	private static ItemStack icon() {
		//Oh Java, you and your "illegal forward reference"s.
		return new ItemStack(LIGHT_SENSOR);
	}
}
