package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.LampBlock;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class RecipeGen implements DataProvider {
	public RecipeGen(Path outRoot) {
		this.outRoot = outRoot;
	}
	
	private final Path outRoot;
	
	@Override
	public void run(DataCache cache) throws IOException {
		Consumer<RecipeJsonProvider> saver = r -> {
			try {
				Identifier recipeId = r.getRecipeId();
				Path recipePath = outRoot.resolve("data/" + recipeId.getNamespace() + "/recipes/" + recipeId.getPath() + ".json");
				
				DataProvider.writeToPath(GenInit.GSON, cache, r.toJson(), recipePath);
				
				JsonObject advancementJson = r.toAdvancementJson();
				if(advancementJson != null) {
					//Minecraft's recipe provider always puts recipe advancements in its own namespace. Let's fix that.
					advancementJson.remove("parent");
					advancementJson.addProperty("parent", "dazzle:recipes/root");
					
					//Recipe advancements, by default, go under a path defined by the creative tab name.
					//This doesn't make much sense outside the context of vanilla where items are categorized by genre, and when I have to namespace my tab name.
					//In dazzle the tab is named "dazzle.group" so they go under "dazzle/advancements/recipes/dazzle.group/___.json" and that's weird.
					//I'm just going to remove this extra path segment and hope everything works okay.
					String hoo = Objects.requireNonNull(r.getAdvancementId()).getPath().replace("dazzle.group/", "");
					
					Path advancementPath = outRoot.resolve("data/" + recipeId.getNamespace() + "/advancements/" + hoo + ".json");
					DataProvider.writeToPath(GenInit.GSON, cache, advancementJson, advancementPath);
				}
			} catch (IOException e) { //grumble grumble
				throw new RuntimeException(e);
			}
		};
		
		for(LampBlock lamp : DazzleBlocks.LAMPS) {
			LampStyle style = lamp.style;
			ShapelessRecipeJsonFactory recipe = ShapelessRecipeJsonFactory.create(lamp);
			
			if(style.mode == LampStyle.Mode.DIGITAL) {
				inputAndCriterion(recipe, "has_lamp", Blocks.REDSTONE_LAMP);
				inputAndCriterion(recipe, "has_dye", GenUtil.dyeForColor(style.color.color));
				
				Optional<Item> extra = style.theme.ingredient;
				extra.ifPresent(ex -> inputAndCriterion(recipe, "has_special", ex));
			} else {
				inputAndCriterion(recipe, "has_comparator", Blocks.COMPARATOR);
				inputAndCriterion(recipe, "has_lamp", style.withMode(LampStyle.Mode.DIGITAL).lookupBlock());
			}
			
			recipe.offerTo(saver, Init.id("lamps/" + style.toName())); //Keeping the old recipe ID around for legacy's sake
		}
		
		DazzleBlocks.FLARES.forEach((color, flare) -> {
			ShapelessRecipeJsonFactory recipe = ShapelessRecipeJsonFactory.create(flare);
			
			inputAndCriterion(recipe, "has_fruit", Items.POPPED_CHORUS_FRUIT);
			inputAndCriterion(recipe, "has_glowstone", Items.GLOWSTONE_DUST);
			inputAndCriterion(recipe, "has_dye", GenUtil.dyeForColor(color));
			
			recipe.offerTo(saver, Init.id("flare/" + color.getName() + "_flare")); //Keeping the old recipe ID around for legacy's sake
		});
	}
	
	//Adds an item to a shapeless recipe, and also adds "player has this item" as a prerequisite to unlock the recipe.
	//This is usually what you want in most recipes, and i'm kinda surprised mojang doesn't have a helper like this themselves
	private static void inputAndCriterion(ShapelessRecipeJsonFactory recipe, String name, ItemConvertible item) {
		recipe.input(item);
		recipe.criterion(name, new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[] {ItemPredicate.Builder.create().item(item).build()}));
	}
	
	@Override
	public String getName() {
		return "dazzle 2 recipes!!";
	}
}
