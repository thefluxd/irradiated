package net.fluxd.irradiated;

import net.fluxd.irradiated.core.AreaManager;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Irradiated.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> AREA_PAIRS = BUILDER
      .comment("Define pairs of radius (integer) and name (text). Format: 'integer:text'",
          "Example: ['100:Safety', '100:Close to danger']")
      .defineList("areaPairs",
          List.of("100:Safety"),
          Config::validateAreaPairs);;

  static final ForgeConfigSpec SPEC = BUILDER.build();

  public static List<AreaEntry> areaEntries;

  private static boolean validateAreaPairs(final Object obj) {
    return obj instanceof String pair && pair.contains(":") && Utils.isInteger(pair.split(":", 2)[0]);
  }

  /**
   * This method converts the raw String list from the config file
   * into our usable AreaEntry objects.
   */
  static void bakeConfig() {
    areaEntries = AREA_PAIRS.get().stream()
        .map(pair -> {
          String[] parts = pair.split(":", 2);
          int radius = Integer.parseInt(parts[0].trim());
          String name = parts[1].trim();
          return new AreaEntry(radius, new AreaManager.Area(AreaManager.AreaType.USER, name));
        }).collect(Collectors.toList());
  }

  @SubscribeEvent
  static void onConfigEvent(final ModConfigEvent event) {
    // Only bake if the config file belonging to THIS mod was changed
    if (event.getConfig().getSpec() == SPEC) {
      bakeConfig();
    }
  }

  public record AreaEntry(int radius, AreaManager.Area area) {
  }
}
