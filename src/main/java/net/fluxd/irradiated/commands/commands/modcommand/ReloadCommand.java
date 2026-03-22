package net.fluxd.irradiated.commands.commands.modcommand;

import java.util.List;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.fluxd.irradiated.commands.ICommand;
import net.fluxd.irradiated.config.Config;
import net.minecraft.ChatFormatting;
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
    List<String> errors = Config.load();

    if (errors.isEmpty()) {
      context.getSource()
          .sendSuccess(() -> Component.literal("Config reloaded successfully!").withStyle(ChatFormatting.GREEN), true);
    } else {
      context.getSource().sendFailure(Component.literal("Config reloaded with errors:").withStyle(ChatFormatting.RED));
      for (String error : errors) {
        context.getSource().sendFailure(Component.literal("- " + error).withStyle(ChatFormatting.GRAY));
      }
    }
    return 1;
  }
}
