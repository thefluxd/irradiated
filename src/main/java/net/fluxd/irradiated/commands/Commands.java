package net.fluxd.irradiated.commands;

import java.util.List;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.commands.commands.AreaCommand;
import net.fluxd.irradiated.commands.commands.ModCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Irradiated.MODID)
public class Commands {
  private static final List<ICommand> COMMANDS = List.of(
      new AreaCommand(),
      new ModCommand());

  @SubscribeEvent
  public static void onRegisterCommands(RegisterCommandsEvent event) {
    COMMANDS.forEach(command -> event.getDispatcher().register(command.register()));
  }
}
