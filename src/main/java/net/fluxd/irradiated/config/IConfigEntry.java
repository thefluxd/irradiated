package net.fluxd.irradiated.config;

import com.google.gson.JsonObject;
import java.util.List;

public interface IConfigEntry {
  /**
   * Reads from JSON and validates immediately.
   * If validation fails, add a message to errors and keep the previous/default
   * value.
   */
  void load(JsonObject json, List<String> errors);

  void setToDefaults();

  JsonObject serialize();
}