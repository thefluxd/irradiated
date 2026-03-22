package net.fluxd.irradiated.commands.commands;

import java.net.URL;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.commands.ICommand;
import net.fluxd.irradiated.commands.commands.modcommand.ReloadCommand;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class ModCommand implements ICommand {
  public LiteralArgumentBuilder<CommandSourceStack> register() {
    return Commands.literal(Irradiated.MODID)
        .executes(this::execute)
        .then(new ReloadCommand().register());
  }

  public int execute(CommandContext<CommandSourceStack> context) {
    IModInfo modInfo = ModList.get().getModContainerById(Irradiated.MODID)
        .map(container -> container.getModInfo())
        .orElse(null);

    if (modInfo == null) {
      context.getSource().sendFailure(Component.literal("Could not find mod metadata."));
      return 0;
    }

    // Extract the data from mods.toml
    String name = modInfo.getDisplayName();
    String version = modInfo.getVersion().toString();
    String authors = modInfo.getConfig().getConfigElement("authors").map(Object::toString).orElse("Unknown");
    String url = modInfo.getModURL().map(URL::toString).orElse("No URL provided");

    Component message = Component.empty()
        .append(Component.literal("--- ").withStyle(ChatFormatting.GRAY))
        .append(Component.literal(name).withStyle(ChatFormatting.WHITE))
        .append(Component.literal(" ---").withStyle(ChatFormatting.GRAY))
        .append("\n")
        .append(Component.literal("Version: ").withStyle(ChatFormatting.GRAY))
        .append(Component.literal(version).withStyle(ChatFormatting.WHITE))
        .append("\n")
        .append(Component.literal("Author: ").withStyle(ChatFormatting.GRAY))
        .append(Component.literal(authors).withStyle(ChatFormatting.WHITE))
        .append("\n").append(Component.literal("[Home page]").withStyle(style -> style
            .withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Opens mod's home page")))
            .withUnderlined(true)));

    context.getSource().sendSuccess(() -> message, false);
    return 1;
  }
}
