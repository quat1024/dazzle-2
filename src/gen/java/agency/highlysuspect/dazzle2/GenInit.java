package agency.highlysuspect.dazzle2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.DyeColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class GenInit implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger("dazzle2-gen");
	
	public static final Gson GSON = new GsonBuilder().create(); //No pretty-printing, why not cut the filesize down.
	public static final Gson PRETTY_PRINT = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public void onInitialize() {
		// current directory atm is the `run` directory, containing all the game data
		Path output = Paths.get("../src/gen_out/resources").toAbsolutePath().normalize();
		LOG.info("Path: {}", output);
		
		//handrolledCrap(output);
		
		try {
			DataGenerator dataGen = new DataGenerator(output, Collections.emptyList());
			
			//assets
			dataGen.install(new BlockStateGen(output));
			dataGen.install(new ItemModelGen(output));
			
			//data
			dataGen.install(new BlockDropGen(output));
			dataGen.install(new RecipeGen(output));
			
			dataGen.run();
			
			LOG.warn("Data generation success. Exiting game");
			System.exit(0);
		} catch (Exception e) {
			LOG.fatal(e);
			System.exit(420);
		}
	}
	
	public static void handrolledCrap(Path output) {
		String muricaLang = generateLang(true);
		String otherLang = generateLang(false);
		
		//Can't really merge the en_us contents into the existing en_us lang file.
		write(output, "en_us_include.json", muricaLang);
		
		write(output, "assets/dazzle/lang/en_au.json", otherLang);
		write(output, "assets/dazzle/lang/en_ca.json", otherLang);
		write(output, "assets/dazzle/lang/en_gb.json", otherLang);
		write(output, "assets/dazzle/lang/en_nz.json", otherLang);
		
		LOG.warn("Don't forget to copy en_us_include.json into en_us.json!!!!!!!!!!!!!!");
	}
	
	private static void write(Path root, String next, String contents) {
		Path target = root.resolve(next);
		LOG.info("Writing to " + target);
		
		try {
			Files.createDirectories(target.getParent());
			Files.write(target, contents.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String generateLang(boolean murica) {
		JsonObject lang = new JsonObject();
		
		if(murica) {
			for(LampStyle style : LampStyle.ALL) {
				lang.addProperty("block.dazzle." + style.toName(), style.englishLocalization(true));
			}
			
			for(DyeColor color : DyeColor.values()) {
				lang.addProperty("block.dazzle." + color.getName() + "_flare", Junk.prettyPrintDyeColor(color, true) + " Flare");
			}
		} else {
			for(LampStyle style : LampStyle.ALL) {
				String a = style.englishLocalization(true);
				String b = style.englishLocalization(false);
				if(!a.equals(b)) {
					lang.addProperty("block.dazzle." + style.toName(), b);
				}
			}
			
			for(DyeColor color : DyeColor.values()) {
				String a = Junk.prettyPrintDyeColor(color, true);
				String b = Junk.prettyPrintDyeColor(color, false);
				if(!a.equals(b)) {
					lang.addProperty("block.dazzle." + color.getName() + "_flare", b + " Flare");
				}
			}
		}
		
		return lang.toString();
	}
}
