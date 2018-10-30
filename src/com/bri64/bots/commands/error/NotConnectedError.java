package com.bri64.bots.commands.error;

import com.bri64.bots.BotUtils;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.DiscordCommand;

public class NotConnectedError extends DiscordCommand {

  public NotConnectedError(CommandEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    BotUtils.sendMessage(getUser().mention() + " " +
            "Error, user must be in a voice channel.",
        getUser().getOrCreatePMChannel());
  }
}
