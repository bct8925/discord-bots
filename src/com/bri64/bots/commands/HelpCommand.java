package com.bri64.bots.commands;

import com.bri64.bots.BotUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("FieldCanBeLocal")
public class HelpCommand extends MessageCommand {

  private String help;

  public HelpCommand(final MessageEvent event, final String help) {
    super(event);
    this.help = help;
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();

    if (!message.contains("@everyone") && !message.contains("@here")) {
      BotUtils.sendMessage(user.mention() + " " + help, user.getOrCreatePMChannel());
    }
  }
}
