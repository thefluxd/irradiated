package net.fluxd.irradiated.effects.effects;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

public class WeakenedEffect extends MobEffect {
  public static final int COLOR = 0x8B4513;

  public WeakenedEffect() {
    super(MobEffectCategory.HARMFUL, COLOR);
  }

  @Override
  public List<ItemStack> getCurativeItems() {
    return Collections.emptyList();
  }
}