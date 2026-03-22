package net.fluxd.irradiated;

import com.mojang.logging.LogUtils;

import net.fluxd.irradiated.config.Config;
import net.fluxd.irradiated.effects.Effects;
import net.fluxd.irradiated.items.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

import org.slf4j.Logger;

@Mod(Irradiated.MODID)
public class Irradiated {
  public static final String MODID = "irradiated";
  public static final String CONFIG_FILE = "irradiated-config.json";
  private static final Logger LOGGER = LogUtils.getLogger();

  public Irradiated(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();
    modEventBus.addListener(this::commonSetup);

    // Register the Deferred Registers
    Items.ITEMS.register(modEventBus);
    Effects.MOB_EFFECTS.register(modEventBus);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    List<String> configErrors = Config.load();

    if (configErrors.isEmpty()) {
      LOGGER.info("{} configuration loaded successfully.", MODID);
    } else {
      LOGGER.error("====================================================");
      LOGGER.error("CONFIG VALIDATION FAILED for {}:", MODID);
      LOGGER.error("{} error(s) detected in {}", configErrors.size(), CONFIG_FILE);

      for (String error : configErrors) {
        LOGGER.error("  -> {}", error);
      }

      LOGGER.error("Using default/previous valid values for invalid entries.");
      LOGGER.error("====================================================");
    }
  }
}
