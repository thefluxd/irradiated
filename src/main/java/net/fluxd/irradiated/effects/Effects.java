package net.fluxd.irradiated.effects;

import java.util.function.Supplier;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.effects.effects.OverdoseEffect;
import net.fluxd.irradiated.effects.effects.ProtectionEffect;
import net.fluxd.irradiated.effects.effects.RadiationEffect;
import net.fluxd.irradiated.effects.effects.WeakenedEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Effects {
  public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister
      .create(ForgeRegistries.MOB_EFFECTS, Irradiated.MODID);

  public static final RegistryObject<MobEffect>//
  RADIATION = add("radiation", RadiationEffect::new),
      PROTECTION = add("protection", ProtectionEffect::new),
      WEAKENED = add("weakened", WeakenedEffect::new),
      OVERDOSE = add("overdose", OverdoseEffect::new);

  private static RegistryObject<MobEffect> add(String id, Supplier<MobEffect> sup) {
    return MOB_EFFECTS.register(id, sup);
  }

  // INSTANCES
  public static MobEffectInstance radiationInstance(int duration) {
    return new MobEffectInstance(RADIATION.get(), duration, 0, false, false, false);
  }

  public static MobEffectInstance protectionInstance(int duration) {
    return new MobEffectInstance(PROTECTION.get(), duration, 0, false, false, true);
  }

  public static MobEffectInstance weakenedInstance(int duration) {
    return new MobEffectInstance(WEAKENED.get(), duration, 0, false, false, true);
  }

  public static MobEffectInstance overdoseInstance(int duration) {
    return new MobEffectInstance(OVERDOSE.get(), duration, 0, false, false, false);
  }

}