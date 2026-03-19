package net.fluxd.irradiated.effects.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ProtectionEffect extends MobEffect {
  public static final int COLOR = 0x2F0F05;

  public ProtectionEffect() {
    super(MobEffectCategory.BENEFICIAL, COLOR);
  }
}