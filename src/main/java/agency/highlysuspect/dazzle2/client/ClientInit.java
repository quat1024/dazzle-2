package agency.highlysuspect.dazzle2.client;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.LampBlock;
import agency.highlysuspect.dazzle2.item.DazzleItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;

public class ClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		DazzleBlocks.LAMPS.forEach(b -> {
			BlockRenderLayerMap.INSTANCE.putBlock(b, b.style.theme.isTransparent ? RenderLayer.getTranslucent() : RenderLayer.getCutoutMipped());
		});
		
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			if(tintIndex == 0) {
				//TODO factor in the light level here
				LampBlock b = (LampBlock) state.getBlock();
				return b.style.color.color.getMaterialColor().color; //color
			} else return 0xFFFFFF;
		}, DazzleBlocks.LAMPS.toArray(new LampBlock[0]));
		
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if(tintIndex == 0) {
				LampBlock b = (LampBlock) ((BlockItem) stack.getItem()).getBlock();
				return b.style.color.color.getMaterialColor().color; //color
			} else return 0xFFFFFF;
		}, DazzleItems.LAMP_ITEMS.toArray(new BlockItem[0]));
	}
}