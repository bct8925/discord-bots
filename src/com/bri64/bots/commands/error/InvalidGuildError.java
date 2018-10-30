package com.bri64.bots.commands.error;

import com.bri64.bots.BotUtils;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.DiscordCommand;

public class InvalidGuildError extends DiscordCommand {

  public InvalidGuildError(CommandEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    BotUtils.sendMessage(getUser().mention() + " " +
            "Error, command must be run from guild.",
        getUser().getOrCreatePMChannel());
  }
}
