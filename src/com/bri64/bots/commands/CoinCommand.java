package com.bri64.bots.commands;

import com.bri64.bots.BotUtils;
import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public class CoinCommand extends DiscordCommand {

  public CoinCommand(final CommandEvent event) {
    super(event);
  }

  @Override
  public void execute() {
    BotUtils.sendMessage(getUser().mention() + " "
        + ((new Random().nextBoolean()) ? "Heads!" : "Tails!"), getUser().getOrCreatePMChannel());
  }
}
