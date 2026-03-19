package net.fluxd.irradiated.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;

public interface ICommand {
  LiteralArgumentBuilder<CommandSourceStack> register();

  int execute(CommandContext<CommandSourceStack> context);
}
