package com.bri64.discord.commands;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DiscordBot;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;

public class LonelyCommand extends DiscordCommand {

  private DiscordBot bot;

  public LonelyCommand(CommandEvent event, DiscordBot bot) {
    super(event);
    this.bot = bot;
  }

  @Override
  public void execute() {
    // Manual override
    if (shouldForce()) {
      valid();
      return;
    }

    // Valid guild check
    if (getGuild() == null) {
      new InvalidGuildError(event).execute();
      return;
    }

    // User connected check
    if (BotUtils.getConnectedChannel(getGuild(), getUser()) == null) {
      new NotConnectedError(event).execute();
      return;
    }

    valid();
  }

  @Override
  public void valid() {
    bot.setLonely(!bot.isLonely());
  }
}
