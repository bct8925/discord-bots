package com.bri64.bots.commands;

import com.bri64.bots.BotUtils;
import java.util.Random;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("FieldCanBeLocal")
public class CoinCommand extends MessageCommand {

  public CoinCommand(final MessageEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    BotUtils.sendMessage(user.mention() + " "
        + ((new Random().nextBoolean()) ? "Heads!" : "Tails!"), user.getOrCreatePMChannel());

  }
}
