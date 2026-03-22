package net.fluxd.irradiated.config;

import com.google.gson.*;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.config.entries.AreaConfigEntry;
import net.fluxd.irradiated.config.entries.LugolsIodineProtectionTimeEntry;
import net.fluxd.irradiated.config.entries.LugolsIodineWeakenedTimeEntry;
import net.minecraftforge.fml.loading.FMLPaths;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final File FILE = FMLPaths.CONFIGDIR.get().resolve(Irradiated.CONFIG_FILE).toFile();

  // Registry of modular entries
  public static final AreaConfigEntry AREAS = new AreaConfigEntry();
  public static final LugolsIodineProtectionTimeEntry LUGOLS_IODINE_PROTECTION_TIME = new LugolsIodineProtectionTimeEntry();
  public static final LugolsIodineWeakenedTimeEntry LUGOLS_IODINE_WEAKENED_TIME = new LugolsIodineWeakenedTimeEntry();
  private static final List<IConfigEntry> ENTRIES = List.of(
      AREAS,
      LUGOLS_IODINE_PROTECTION_TIME,
      LUGOLS_IODINE_WEAKENED_TIME);

  public static List<String> load() {
    List<String> errors = new ArrayList<>();
    if (!FILE.exists()) {
      // initialize defaults
      for (IConfigEntry entry : ENTRIES) {
        entry.setToDefaults();
      }
      save();
      return errors;
    }

    try (FileReader reader = new FileReader(FILE)) {
      JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
      // Load and Validate logic is merged here
      for (IConfigEntry entry : ENTRIES) {
        entry.load(json, errors);
      }
    } catch (Exception e) {
      errors.add("Critical JSON Error: " + e.getMessage());
    }
    return errors;
  }

  public static void save() {
    JsonObject combined = new JsonObject();
    for (IConfigEntry entry : ENTRIES) {
      JsonObject serialized = entry.serialize();
      serialized.entrySet().forEach(e -> combined.add(e.getKey(), e.getValue()));
    }
    try (FileWriter writer = new FileWriter(FILE)) {
      GSON.toJson(combined, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
