package com.bri64.bots.commands.error;

import com.bri64.bots.BotUtils;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.DiscordCommand;

@SuppressWarnings("FieldCanBeLocal")
public class InvalidCommandError extends DiscordCommand {

  public InvalidCommandError(final CommandEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    BotUtils
        .sendMessage(getUser().mention() + " " + "\"" + getMessage() + "\" is not a valid command!",
            getUser().getOrCreatePMChannel());
  }
}
