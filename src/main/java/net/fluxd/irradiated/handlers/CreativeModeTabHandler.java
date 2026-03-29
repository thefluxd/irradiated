package net.fluxd.irradiated.handlers;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.items.Items;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Irradiated.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeModeTabHandler {
  @SubscribeEvent
  public static void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
      event.accept(Items.LUGOLS_IODINE);
    }
  }
}
