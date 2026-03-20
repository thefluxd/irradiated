package net.fluxd.irradiated.mixins;

import net.fluxd.irradiated.effects.Effects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class OverdoseVisualsMixin {

  @Shadow
  public abstract boolean hasEffect(MobEffect effect);

  @Shadow
  public abstract MobEffectInstance getEffect(MobEffect effect);

  @Shadow
  public abstract float getHealth();

  @Unique
  private MobEffectInstance irradiated$fakeDarkness;
  @Unique
  private MobEffectInstance irradiated$fakeNausea;
  @Unique
  private boolean irradiated$isProcessing = false;

  // Timer to keep spoofing for a few ticks after death/expiry to allow fade-out
  @Unique
  private int irradiated$cleanupTicks = 0;

  @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
  private void mirrorHasEffect(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
    if (irradiated$isProcessing)
      return;

    if ((Object) this instanceof LocalPlayer) {
      if (effect == MobEffects.CONFUSION || effect == MobEffects.DARKNESS) {
        if (irradiated$shouldSpoof()) {
          cir.setReturnValue(true);
        }
      }
    }
  }

  @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
  private void mirrorGetEffect(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> cir) {
    if (irradiated$isProcessing)
      return;

    if ((Object) this instanceof LocalPlayer) {
      if (effect == MobEffects.CONFUSION || effect == MobEffects.DARKNESS) {
        irradiated$isProcessing = true;
        MobEffectInstance overdose = this.getEffect(Effects.OVERDOSE.get());
        irradiated$isProcessing = false;

        if (overdose != null && this.getHealth() > 0) {
          // Normal spoofing while alive and effect is active
          cir.setReturnValue(irradiated$getOrCreateFake(effect, overdose.getDuration(), overdose.getAmplifier()));
        } else if (irradiated$cleanupTicks > 0) {
          // Graceful Fade-out: return an "expired" effect so the renderer clears the fog
          cir.setReturnValue(new MobEffectInstance(effect, 0, 0, true, false, false));
        }
      }
    }
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void tickVisuals(CallbackInfo ci) {
    if (!((Object) this instanceof LocalPlayer player))
      return;
    if (!player.level().isClientSide)
      return;

    irradiated$isProcessing = true;
    boolean hasOverdose = this.hasEffect(Effects.OVERDOSE.get());
    irradiated$isProcessing = false;

    if (hasOverdose && this.getHealth() > 0) {
      // Player is alive and has the effect
      irradiated$cleanupTicks = 20; // Maintain a 1-second "cleanup" window
      if (irradiated$fakeDarkness != null)
        irradiated$fakeDarkness.tick((LivingEntity) (Object) this, () -> {
        });
      if (irradiated$fakeNausea != null)
        irradiated$fakeNausea.tick((LivingEntity) (Object) this, () -> {
        });
    } else {
      // Player died or effect ended
      if (irradiated$cleanupTicks > 0) {
        irradiated$cleanupTicks--;

        // Force a renderer refresh every few ticks during the cleanup phase
        // if (irradiated$cleanupTicks % 5 == 0) {
        // Minecraft.getInstance().levelRenderer.allChanged();
        // }

        // Final tick: hard reset
        if (irradiated$cleanupTicks == 0) {
          irradiated$fakeDarkness = null;
          irradiated$fakeNausea = null;
          Minecraft.getInstance().levelRenderer.allChanged();
        }
      }
    }
  }

  @Unique
  private boolean irradiated$shouldSpoof() {
    // Spoof if the player has the effect OR if we are in the 1-second fade-out
    // window
    irradiated$isProcessing = true;
    boolean hasEffect = this.hasEffect(Effects.OVERDOSE.get());
    irradiated$isProcessing = false;

    return (hasEffect && this.getHealth() > 0) || irradiated$cleanupTicks > 0;
  }

  @Unique
  private MobEffectInstance irradiated$getOrCreateFake(MobEffect type, int duration, int amp) {
    if (type == MobEffects.DARKNESS) {
      if (irradiated$fakeDarkness == null) {
        irradiated$fakeDarkness = new MobEffectInstance(type, duration, amp, true, false, false);
      }
      return irradiated$fakeDarkness;
    } else {
      if (irradiated$fakeNausea == null) {
        irradiated$fakeNausea = new MobEffectInstance(type, duration, amp, true, false, false);
      }
      return irradiated$fakeNausea;
    }
  }
}