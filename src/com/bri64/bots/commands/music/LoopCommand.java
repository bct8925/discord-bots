package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.audio.send.LoopMode;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.error.InvalidGuildError;

public class LoopCommand extends MusicCommand {

  public LoopCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    // Argument check
    String[] args = getMessage().split(" ");
    if (args.length != 2) {
      invalidArgs();
      return;
    }

    // Valid guild check
    if (getGuild() == null) {
      new InvalidGuildError(event).execute();
      return;
    }

    valid();
  }

  @Override
  public void valid() {
    String mode = getMessage().split(" ")[1];
    switch (mode.toLowerCase()) {
      case "none":
        scheduler.setLoop(LoopMode.NONE);
        break;
      case "one":
        scheduler.setLoop(LoopMode.ONE);
        break;
      case "all":
        scheduler.setLoop(LoopMode.ALL);
        break;
      default:
        invalidArgs();
        break;
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: loop none|one|all",
        getUser().getOrCreatePMChannel());
  }
}
