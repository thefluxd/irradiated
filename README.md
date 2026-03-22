# Irradiated

A mod that adds configurable areas and radiation to force players to build close to each other.

## Configuration

Available at `config/irradiated-config.json`. Example:

```json
{
  "areas_by_dimension": {
    "minecraft:overworld": [
      {
        "name": "Safety",
        "radius": 100
      },
      {
        "name": "&bNot Safety",
        "radius": 50
      }
    ]
  },
  "lugols_iodine_protection_time_s": 3600,
  "lugols_iodine_weakened_time_s": 7200
}
```

The radius of the area is additive, so the second area in the example has a radius of 150 blocks. It's important not to set the area radius below 30 blocks, as I was too lazy to fix it.
Area name supports Minecraft's color codes.
Radiation appears after the set areas. If areas for dimension are not set, radiation is disabled.
In `overworld` and `the_nether` dimension, center position is taken from world spawn (in `the_nether` divided by 8), otherwise its `0, 0`.
