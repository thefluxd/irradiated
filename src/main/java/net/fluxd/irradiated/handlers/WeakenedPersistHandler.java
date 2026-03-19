package net.fluxd.irradiated.handlers;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.effects.Effects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Irradiated.MODID)
public class WeakenedPersistHandler {
  @SubscribeEvent
  public static void onPlayerRespawn(PlayerEvent.Clone event) {
    if (event.isWasDeath()) {
      // Check if the old player had the Weakened effect
      MobEffectInstance weakened = event.getOriginal().getEffect(Effects.WEAKENED.get());
      if (weakened != null) {
        // Apply it back to the new player
        event.getEntity().addEffect(new MobEffectInstance(weakened));
      }
    }
  }
}
