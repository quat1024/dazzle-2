package agency.highlysuspect.dazzle2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class GenInit implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger("dazzle2-gen");
	
	public static final Gson GSON = new GsonBuilder().create(); //No pretty-printing, why not cut the filesize down.
	public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public void onInitialize() {
		// current directory atm is the `run` directory, containing all the game data
		Path output = Paths.get("../src/gen_out/resources").toAbsolutePath().normalize();
		Path en_usJson = Paths.get("../src/main/resources/assets/dazzle/lang/en_us.json").toAbsolutePath().normalize();
		LOG.info("Main output path: {}, en_us.json: {}", output, en_usJson);
		
		try {
			DataGenerator dataGen = new DataGenerator(output, Collections.emptyList());
			
			//assets
			dataGen.install(new BlockStateGen(output));
			dataGen.install(new ItemModelGen(output));
			dataGen.install(new LangGen(output, en_usJson));
			
			//data
			dataGen.install(new BlockDropGen(output));
			dataGen.install(new RecipeGen(output));
			dataGen.install(new TagGen(output));
			
			dataGen.run();
			
			LOG.warn("Data generation success. Exiting game");
			System.exit(0);
		} catch (Exception e) {
			LOG.fatal(e);
			System.exit(420);
		}
	}
}
