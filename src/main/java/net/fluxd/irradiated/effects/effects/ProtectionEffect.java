package net.fluxd.irradiated.effects.effects;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

public class ProtectionEffect extends MobEffect {
  public static final int COLOR = 0x2F0F05;

  public ProtectionEffect() {
    super(MobEffectCategory.BENEFICIAL, COLOR);
  }

  @Override
  public List<ItemStack> getCurativeItems() {
    return Collections.emptyList();
  }
}