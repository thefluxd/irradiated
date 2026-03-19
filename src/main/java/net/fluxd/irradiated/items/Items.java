package net.fluxd.irradiated.items;

import java.util.function.Supplier;

import net.fluxd.irradiated.Irradiated;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Irradiated.MODID);

  public static final RegistryObject<Item>//
  LUGOLS_IODINE = add("lugols_iodine", LugolsIodineItem::new);

  private static RegistryObject<Item> add(String id, Supplier<Item> sup) {
    return ITEMS.register(id, sup);
  }
}
