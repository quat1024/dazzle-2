package agency.highlysuspect.dazzle2.item;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class DazzleItems {
	public static final ItemGroup owo = FabricItemGroupBuilder.build(Init.id("group"), () -> new ItemStack(Items.REDSTONE_LAMP)); //TODO
	
	public static final List<BlockItem> LAMP_ITEMS = DazzleBlocks.LAMPS.stream().map(lb -> new BlockItem(lb, new Item.Settings().group(owo))).collect(Collectors.toList());;
	
	public static void onInitialize() {
		for(BlockItem item : LAMP_ITEMS) {
			Identifier id = Registry.BLOCK.getId(item.getBlock());
			Registry.register(Registry.ITEM, id, item);
		}
	}
}
