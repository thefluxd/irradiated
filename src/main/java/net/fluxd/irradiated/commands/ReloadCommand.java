package net.fluxd.irradiated.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.fluxd.irradiated.Config;
import net.fluxd.irradiated.Irradiated;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadCommand {
  public ReloadCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal(Irradiated.MODID).then(
        Commands.literal("reload")
            .requires(source -> source.hasPermission(3))
            .executes(this::execute)));
  }

  private int execute(CommandContext<CommandSourceStack> context) {
    // TODO: fix this shit
    context.getSource().sendSuccess(() -> Component.literal("Trying to reload config"), false);
    return 1;
  }
}
