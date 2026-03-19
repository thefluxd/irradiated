package net.fluxd.irradiated.items;

import net.fluxd.irradiated.effects.Effects;
import net.fluxd.irradiated.effects.effects.ProtectionEffect;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class LugolsIodineItem extends Item {
  public static final int COLOR = ProtectionEffect.COLOR;
  public static final int RADIATION_PROTECTION_T = 72000; // 1h
  public static final int WEAKENED_T = 144000; // 2h
  // TODO: move to mod config

  public LugolsIodineItem() {
    super(new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE));
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
    if (!level.isClientSide) {
      if (entity.hasEffect(Effects.WEAKENED.get())) {
        entity.addEffect(Effects.overdoseInstance(Integer.MAX_VALUE));
      } else {
        entity.addEffect(Effects.protectionInstance(RADIATION_PROTECTION_T));
        entity.addEffect(Effects.weakenedInstance(WEAKENED_T));
      }
    }

    // Handle player statistics and advancement triggers
    if (entity instanceof ServerPlayer serverPlayer) {
      CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
      serverPlayer.awardStat(Stats.ITEM_USED.get(this));
    }

    // Logic for returning the empty glass bottle
    if (entity instanceof Player player && !player.getAbilities().instabuild) {
      stack.shrink(1);
      if (stack.isEmpty()) {
        return new ItemStack(Items.GLASS_BOTTLE);
      }
      player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
    }

    return stack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : stack;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.DRINK;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    return ItemUtils.startUsingInstantly(level, player, hand);
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    return 32;
  }
}