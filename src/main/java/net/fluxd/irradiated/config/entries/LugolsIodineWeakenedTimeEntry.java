package net.fluxd.irradiated.config.entries;

import java.util.List;

import com.google.gson.JsonObject;

import net.fluxd.irradiated.config.IConfigEntry;

public class LugolsIodineWeakenedTimeEntry implements IConfigEntry {
  private static final String KEY = "lugols_iodine_weakened_time_s";

  private int time = 7200;

  @Override
  public void load(JsonObject json, List<String> errors) {
    if (json.has(KEY)) {
      int val = json.get(KEY).getAsInt();
      if (val < 0) {
        errors.add(KEY + ": Value cannot be negative (found: " + val + ")");
      } else {
        this.time = val;
      }
    }
  }

  @Override
  public void setToDefaults() {
    this.time = 7200;
  }

  @Override
  public JsonObject serialize() {
    JsonObject obj = new JsonObject();
    obj.addProperty(KEY, time);
    return obj;
  }

  public int getValue() {
    return time;
  }
}