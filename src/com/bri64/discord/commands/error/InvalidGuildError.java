package com.bri64.discord.commands.error;

import com.bri64.discord.BotUtils;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.DiscordCommand;

public class InvalidGuildError extends DiscordCommand {

  public InvalidGuildError(CommandEvent event) {
    super(event, false);
  }

  @Override
  public void execute() {
    BotUtils.sendMessage(getUser().mention() + " " +
            "Error, command must be run from guild.",
        getUser().getOrCreatePMChannel());
  }
}
