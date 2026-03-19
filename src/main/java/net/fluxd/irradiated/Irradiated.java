package net.fluxd.irradiated;

import com.mojang.logging.LogUtils;

import net.fluxd.irradiated.effects.Effects;
import net.fluxd.irradiated.items.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(Irradiated.MODID)
public class Irradiated {
  public static final String MODID = "irradiated";
  private static final Logger LOGGER = LogUtils.getLogger();

  public Irradiated(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();
    modEventBus.addListener(this::commonSetup);

    // Register the Deferred Registers
    Items.ITEMS.register(modEventBus);
    Effects.MOB_EFFECTS.register(modEventBus);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);

    // Register our mod's ForgeConfigSpec so that Forge can create and load the
    // config file for us
    context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // LOGGER.info("HELLO FROM COMMON SETUP");
  }
}
