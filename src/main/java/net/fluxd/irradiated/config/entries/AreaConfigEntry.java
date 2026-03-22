package net.fluxd.irradiated.config.entries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fluxd.irradiated.config.IConfigEntry;
import net.fluxd.irradiated.core.AreaManager;
import net.fluxd.irradiated.core.AreaManager.AreaEntry;

import java.util.*;

public class AreaConfigEntry implements IConfigEntry {
  private static final String KEY = "areas_by_dimension";

  // Current valid state
  private Map<String, List<AreaEntry>> areasByDimension = new HashMap<>();

  @Override
  public void load(JsonObject json, List<String> errors) {
    if (!json.has(KEY))
      return;

    Map<String, List<AreaEntry>> tempMap = new HashMap<>();
    JsonObject mapObj = json.getAsJsonObject(KEY);

    for (Map.Entry<String, JsonElement> entry : mapObj.entrySet()) {
      String dimension = entry.getKey();
      List<AreaEntry> areas = new ArrayList<>();
      JsonArray array = entry.getValue().getAsJsonArray();

      for (int i = 0; i < array.size(); i++) {
        JsonObject obj = array.get(i).getAsJsonObject();
        String name = obj.has("name") ? obj.get("name").getAsString() : "unnamed";
        int radius = obj.has("radius") ? obj.get("radius").getAsInt() : -1;

        // VALIDATION: radius should be positive
        if (radius <= 0) {
          errors.add("Dimension [" + dimension + "], Area [" + name + "]: Radius must be a positive integer (found: "
              + radius + ")");
        } else {
          areas.add(new AreaEntry(radius, new AreaManager.Area(AreaManager.AreaType.USER, name)));
        }
      }
      tempMap.put(dimension, areas);
    }

    // Only update the live config if we found valid entries
    if (!tempMap.isEmpty()) {
      this.areasByDimension = tempMap;
    }
  }

  @Override
  public void setToDefaults() {
    areasByDimension.clear();
    areasByDimension.put("minecraft:overworld",
        List.of(new AreaEntry(100, new AreaManager.Area(AreaManager.AreaType.USER, "Safety"))));
  }

  @Override
  public JsonObject serialize() {
    JsonObject main = new JsonObject();
    JsonObject map = new JsonObject();
    areasByDimension.forEach((dim, list) -> {
      JsonArray arr = new JsonArray();
      for (AreaEntry a : list) {
        JsonObject o = new JsonObject();
        o.addProperty("name", a.area().name());
        o.addProperty("radius", a.radius());
        arr.add(o);
      }
      map.add(dim, arr);
    });
    main.add(KEY, map);
    return main;
  }

  public Map<String, List<AreaEntry>> getValue() {
    return areasByDimension;
  }
}
