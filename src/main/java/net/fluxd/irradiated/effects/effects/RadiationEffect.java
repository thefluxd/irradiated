package net.fluxd.irradiated.effects.effects;

import java.util.Collections;
import java.util.List;

import net.fluxd.irradiated.effects.Effects;
import net.fluxd.irradiated.effects.ModDamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RadiationEffect extends MobEffect {
  public static final int COLOR = 0x00FF00;

  public RadiationEffect() {
    super(MobEffectCategory.HARMFUL, COLOR);
  }

  private static final float DAMAGE_H = 1.0F;
  private static final int DELAY_T = 10;

  @Override
  public void applyEffectTick(LivingEntity entity, int amplifier) {
    if (!entity.hasEffect(Effects.PROTECTION.get()) && entity.getHealth() > 0) {
      entity.hurt(ModDamageSources.getRadiationDamageSource(entity.level()), DAMAGE_H * amplifier);
    }
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return duration % DELAY_T == 0; // Tick rate
  }

  @Override
  public List<ItemStack> getCurativeItems() {
    return Collections.emptyList();
  }
}