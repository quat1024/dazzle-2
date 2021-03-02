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
		
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.INVISIBLE_TORCH, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.LIGHT_AIR, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.PROJECTED_LIGHT_PANEL, RenderLayer.getCutout());
		
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			if(tintIndex == 0) {
				LampBlock lamp = (LampBlock) state.getBlock();
				
				float level = lamp.lightFromState(state) / 15f * 0.8f + 0.2f;
				
				int baseColor = lamp.style.color.color.getMaterialColor().color; //color 
				int r = (baseColor & 0xFF0000) >> 16;
				int g = (baseColor & 0x00FF00) >> 8;
				int b =  baseColor & 0x0000FF;
				
				r *= level; r &= 0xFF;
				g *= level; g &= 0xFF;
				b *= level; b &= 0xFF;
				
				return (r << 16) | (g << 8) | b;
			} else return 0xFFFFFF;
		}, DazzleBlocks.LAMPS.toArray(new LampBlock[0]));
		
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if(tintIndex == 0) {
				LampBlock lamp = (LampBlock) ((BlockItem) stack.getItem()).getBlock();
				return lamp.style.color.color.getMaterialColor().color; //color
			} else return 0xFFFFFF;
		}, DazzleItems.LAMP_ITEMS.toArray(new BlockItem[0]));
	}
}