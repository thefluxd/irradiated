package net.fluxd.irradiated.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class BrewingRecipes {
  public static void register() {
    ItemStack awkwardPotionStack = PotionUtils.setPotion(new ItemStack(net.minecraft.world.item.Items.POTION),
        Potions.AWKWARD);
    BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
        Ingredient.of(awkwardPotionStack),
        Ingredient.of(net.minecraft.world.item.Items.TORCHFLOWER),
        new ItemStack(Items.LUGOLS_IODINE.get(), 1)));

  }
}