package com.bri64.discord.commands.error;

import com.bri64.discord.BotUtils;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.DiscordCommand;

public class NotConnectedError extends DiscordCommand {

  public NotConnectedError(CommandEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    BotUtils.sendMessage(getUser().mention() + " " +
            "Error, user must be in a voice channel.",
        getOutChannel());
  }
}
