package com.bri64.discord.commands;

import com.bri64.discord.BotUtils;

@SuppressWarnings("FieldCanBeLocal")
public class HelpCommand extends DiscordCommand {

  private String help;

  public HelpCommand(final CommandEvent event, final String help) {
    super(event);
    this.help = help;
  }

  @Override
  public void execute() {
    if (!getMessage().contains("@everyone") && !getMessage().contains("@here")) {
      valid();
    }
  }

  @Override
  public void valid() {
    BotUtils.sendMessage(getUser().mention() + " " + help, getOutChannel());
  }
}
