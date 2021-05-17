package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;

public class BlockDropGen implements DataProvider {
	public BlockDropGen(Path outRoot) {
		this.outRoot = outRoot;
	}
	
	private final Path outRoot;
	
	@Override
	public void run(DataCache cache) throws IOException {
		doItAll(cache,
			DazzleBlocks.LAMPS,
			
			ImmutableList.of(
				DazzleBlocks.DIM_REDSTONE_TORCH,
				DazzleBlocks.INVISIBLE_TORCH,
				DazzleBlocks.LIGHT_SENSOR,
				DazzleBlocks.PROJECTED_LIGHT_PANEL
			),
			
			DazzleBlocks.FLARES.values(),
			
			DazzleBlocks.DYED_SHROOMLIGHTS.values(),
			ImmutableList.of(
				DazzleBlocks.POLISHED_SHROOMLIGHT
			),
			DazzleBlocks.DYED_POLISHED_SHROOMLIGHTS.values(),
			DazzleBlocks.DYED_END_RODS.values()
		);
	}
	
	@SafeVarargs
	private final void doItAll(DataCache cache, Iterable<? extends Block>... listOfLists) throws IOException {
		for(Iterable<? extends Block> list : listOfLists) {
			for(Block b : list) {
				Identifier id = Registry.BLOCK.getId(b);
				LootTable dropTable = drops(b);
				DataProvider.writeToPath(GenInit.GSON, cache, LootManager.toJson(dropTable), outPath(id));
			}
		}
	}
	
	private Path outPath(Identifier id) {
		return outRoot.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}
	
	private LootTable drops(ItemConvertible drop) {
		//Copy and paste of private "drops(ItemConvertible drop)" from BlockLootTableGenerator.
		//Also from there, private method addSurvivesExplosionCondition(ItemConvertible, LootConditionConsumingBuilder<T>) pasted into this class and inlined with intellij.
		//Im sorry
		return LootTable.builder().pool(
			LootPool.builder().rolls(ConstantLootTableRange.create(1))
				.with(ItemEntry.builder(drop))
				.conditionally(SurvivesExplosionLootCondition.builder()))
			.type(LootContextTypes.BLOCK)
			.build();
	}
	
	@Override
	public String getName() {
		return "dazzle 2 block drops!!";
	}
}
