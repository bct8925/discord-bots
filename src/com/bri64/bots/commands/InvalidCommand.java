package com.bri64.bots.commands;

import com.bri64.bots.BotUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("FieldCanBeLocal")
public class InvalidCommand extends MessageCommand {

  public InvalidCommand(final MessageEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    BotUtils.sendMessage(user.mention() + " " + "\"" + message + "\" is not a valid command!",
        user.getOrCreatePMChannel());
  }
}
