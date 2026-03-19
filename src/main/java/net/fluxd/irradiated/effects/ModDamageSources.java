package net.fluxd.irradiated.effects;

import net.fluxd.irradiated.Irradiated;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class ModDamageSources {
  // This key must match the name of the JSON file we will create later
  public static final ResourceKey<DamageType>//
  RADIATION_KEY = addKey("radiation"),
      OVERDOSE_KEY = addKey("overdose");

  private static ResourceKey<DamageType> addKey(String path) {
    return ResourceKey.create(Registries.DAMAGE_TYPE,
        ResourceLocation.fromNamespaceAndPath(Irradiated.MODID, path));
  }

  public static DamageSource getRadiationDamageSource(Level level) {
    return new DamageSource(level.registryAccess()
        .registryOrThrow(Registries.DAMAGE_TYPE)
        .getHolderOrThrow(RADIATION_KEY));
  }

  public static DamageSource getOverdoseDamageSource(Level level) {
    return new DamageSource(level.registryAccess()
        .registryOrThrow(Registries.DAMAGE_TYPE)
        .getHolderOrThrow(OVERDOSE_KEY));
  }
}