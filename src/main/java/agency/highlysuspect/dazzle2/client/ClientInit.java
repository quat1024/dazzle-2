package agency.highlysuspect.dazzle2.client;

import agency.highlysuspect.dazzle2.block.ColorHolderBlock;
import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.LampBlock;
import agency.highlysuspect.dazzle2.etc.DazzleParticleTypes;
import agency.highlysuspect.dazzle2.item.DazzleItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

public class ClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		assignBlockLayers();
		createColorProviders();
		registerParticles();
	}
	
	private static void assignBlockLayers() {
		final RenderLayer cutout = RenderLayer.getCutout();
		final RenderLayer cutoutMipped = RenderLayer.getCutoutMipped();
		final RenderLayer translucent = RenderLayer.getTranslucent();
		
		//need at least cutoutmipped so color providers work
		DazzleBlocks.LAMPS.forEach(b -> BlockRenderLayerMap.INSTANCE.putBlock(b, b.style.theme.isTransparent ? translucent : cutoutMipped));
		
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.INVISIBLE_TORCH, cutout);
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.LIGHT_AIR, cutout);
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.PROJECTED_LIGHT_PANEL, cutout);
		
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.DIM_REDSTONE_TORCH, cutout);
		BlockRenderLayerMap.INSTANCE.putBlock(DazzleBlocks.DIM_REDSTONE_WALL_TORCH, cutout);
		
		//need at least cutoutmipped so color providers work
		//these use very odd opacity blending so i'll try the translucent layer
		DazzleBlocks.DYED_SHROOMLIGHTS.values().forEach(b -> BlockRenderLayerMap.INSTANCE.putBlock(b, translucent));
		DazzleBlocks.DYED_POLISHED_SHROOMLIGHTS.values().forEach(b -> BlockRenderLayerMap.INSTANCE.putBlock(b, translucent));
	}
	
	private static void createColorProviders() {
		//Redstone lamps
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			LampBlock lamp = (LampBlock) state.getBlock();
			return lamp(tintIndex, lamp.getColor(), lamp.lightFromState(state));
		}, blocks(DazzleBlocks.LAMPS));
		
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			LampBlock lamp = (LampBlock) ((BlockItem) stack.getItem()).getBlock();
			return lamp(tintIndex, lamp.getColor(), 15);
		}, items(DazzleItems.LAMPS));
		
		//Flares
		//Even though the block model itself is invisible, this is visible on blockcrack particles.
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> ((ColorHolderBlock) state.getBlock()).getColor().getMaterialColor().color,
			blocks(DazzleBlocks.FLARES.values()));
		
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if(tintIndex == 1) {
				return ((ColorHolderBlock) ((BlockItem) stack.getItem()).getBlock()).getColor().getMaterialColor().color; //color
			} else return 0xFFFFFF;
		}, items(DazzleItems.FLARES.values()));
		
		//Dyed shroomlights
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> shroom(tintIndex, ((ColorHolderBlock) state.getBlock()).getColor()),
			blocks(DazzleBlocks.DYED_SHROOMLIGHTS.values(), DazzleBlocks.DYED_POLISHED_SHROOMLIGHTS.values()));
		
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> shroom(tintIndex, ((ColorHolderBlock) ((BlockItem) stack.getItem()).getBlock()).getColor()),
			items(DazzleItems.DYED_SHROOMLIGHTS.values(), DazzleItems.DYED_POLISHED_SHROOMLIGHTS.values()));
	}
	
	private static int multiplyAll(int color, float mult) {
		return multiplyRgb(color, mult, mult, mult);
	}
	
	private static int multiplyRgb(int color, float multR, float multG, float multB) {
		int r = (color & 0xFF0000) >> 16;
		int g = (color & 0x00FF00) >> 8;
		int b = color & 0x0000FF;
		
		r *= multR;
		r = MathHelper.clamp(r, 0, 255);
		g *= multG;
		g = MathHelper.clamp(g, 0, 255);
		b *= multB;
		b = MathHelper.clamp(b, 0, 255);
		
		return (r << 16) | (g << 8) | b;
	}
	
	private static int lamp(int tintIndex, DyeColor color, int power) {
		if(tintIndex == 0) return multiplyAll(color.getMaterialColor().color, power / 15f * 0.8f + 0.2f);
		else return 0xFFFFFF;
	}
	
	private static int shroom(int tintIndex, DyeColor color) {
		if(tintIndex == 0) return shroomColorTable[color.ordinal()];
		else return 0xFFFFFF;
	}
	
	private static final int[] shroomColorTable;
	
	static {
		shroomColorTable = new int[DyeColor.values().length];
		for(DyeColor color : DyeColor.values()) {
			shroomColorTable[color.ordinal()] = multiplyRgb(color.getMaterialColor().color, 1.7f, 1.5f, 1.4f);
		}
		
		//Orange looks kinda yellowish so uhh poke it back down a bit
		int orange = DyeColor.ORANGE.ordinal();
		shroomColorTable[orange] = multiplyRgb(shroomColorTable[orange], 0.9f, 0.85f, 1f);
	}
	
	//Quick functions because java varargs are so unergonomic w/ collections
	@SafeVarargs
	private static <T> T[] conv(Class<T> javaSucks, Collection<? extends T>... listOfLists) {
		//noinspection unchecked
		return Arrays.stream(listOfLists).flatMap(Collection::stream).toArray(i -> (T[]) Array.newInstance(javaSucks, i));
	}
	
	@SafeVarargs
	private static Block[] blocks(Collection<? extends Block>... listOfLists) {
		return conv(Block.class, listOfLists);
	}
	
	@SafeVarargs
	private static Item[] items(Collection<? extends Item>... listOfLists) {
		return conv(Item.class, listOfLists);
	}
	
	private static void registerParticles() {
		ParticleFactoryRegistry.getInstance().register(DazzleParticleTypes.FLARE, FlareParticle.Factory::new);
	}
}