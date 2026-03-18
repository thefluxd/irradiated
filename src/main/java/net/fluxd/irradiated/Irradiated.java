package net.fluxd.irradiated;

import com.mojang.logging.LogUtils;

import net.fluxd.irradiated.commands.AreaCommand;
import net.fluxd.irradiated.commands.ReloadCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);

    // Register our mod's ForgeConfigSpec so that Forge can create and load the
    // config file for us
    context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    LOGGER.info("HELLO FROM COMMON SETUP");
  }

  @SubscribeEvent
  public void onRegisterCommands(RegisterCommandsEvent event) {
    new AreaCommand(event.getDispatcher());
    new ReloadCommand(event.getDispatcher());
  }
}
