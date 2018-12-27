package com.bri64.discord.commands;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DiscordBot;

public class RebootCommand extends DiscordCommand {

  private DiscordBot bot;

  public RebootCommand(CommandEvent event, DiscordBot bot) {
    super(event);
    this.bot = bot;
  }


  @Override
  public void execute() {
    BotUtils.log(bot, "Rebooting system...");
    bot.reboot();
    BotUtils.log(bot, "Rebooted!");
  }

}
