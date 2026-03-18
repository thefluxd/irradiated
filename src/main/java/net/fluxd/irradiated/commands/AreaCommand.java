package net.fluxd.irradiated.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.fluxd.irradiated.core.AreaManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AreaCommand {
  public AreaCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("area")
        .executes(this::execute));
  }

  private int execute(CommandContext<CommandSourceStack> context) {
    ServerPlayer player = context.getSource().getPlayer();
    if (player == null)
      return 0;

    String name = AreaManager.getCurrentArea(player).currentName();
    String formattedName = name.replace('&', '§');
    context.getSource().sendSuccess(() -> Component.literal("You are in " + formattedName), false);
    return 1;
  }
}
