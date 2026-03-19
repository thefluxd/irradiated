package net.fluxd.irradiated.handlers;

import java.util.List;
import java.util.function.Supplier;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.effects.Effects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Irradiated.MODID)
public class PersistEffectsHandler {
  private static final List<Supplier<MobEffect>> PERSISTENT_EFFECTS = List.of(
      Effects.PROTECTION,
      Effects.WEAKENED);

  @SubscribeEvent
  public static void onPlayerRespawn(PlayerEvent.Clone event) {
    if (event.isWasDeath()) {
      for (Supplier<MobEffect> effectSupplier : PERSISTENT_EFFECTS) {
        MobEffect effect = effectSupplier.get();

        // Check if the old player had this specific effect
        MobEffectInstance activeEffect = event.getOriginal().getEffect(effect);
        if (activeEffect != null) {
          event.getEntity().addEffect(new MobEffectInstance(activeEffect));
        }
      }
    }
  }
}
