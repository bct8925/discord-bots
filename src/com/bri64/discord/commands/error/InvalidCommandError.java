package com.bri64.discord.commands.error;

import com.bri64.discord.BotUtils;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.DiscordCommand;

@SuppressWarnings("FieldCanBeLocal")
public class InvalidCommandError extends DiscordCommand {

  public InvalidCommandError(final CommandEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    BotUtils
        .sendMessage(getUser().mention() + " " + "\"" + getMessage() + "\" is not a valid command!",
            getOutChannel());
  }
}
