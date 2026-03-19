package net.fluxd.irradiated.handlers;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.items.Items;
import net.fluxd.irradiated.items.LugolsIodineItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Irradiated.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ItemColorHandler {
  @SubscribeEvent
  public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
    event.register((stack, tintIndex) -> tintIndex == 1 ? LugolsIodineItem.COLOR : -1,
        Items.LUGOLS_IODINE.get());
  }
}