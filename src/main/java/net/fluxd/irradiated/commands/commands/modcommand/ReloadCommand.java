package net.fluxd.irradiated.commands.commands.modcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.fluxd.irradiated.commands.ICommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadCommand implements ICommand {
  public LiteralArgumentBuilder<CommandSourceStack> register() {
    return Commands.literal("reload")
        .requires(source -> source.hasPermission(3))
        .executes(this::execute);
  }

  public int execute(CommandContext<CommandSourceStack> context) {
    context.getSource().sendSuccess(() -> Component.literal("Reload broken :("), false);
    return 1;
  }
}
