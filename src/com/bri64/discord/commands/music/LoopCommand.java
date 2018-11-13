package com.bri64.discord.commands.music;

import com.bri64.discord.BotUtils;
import com.bri64.discord.audio.send.LoopMode;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;

@SuppressWarnings("Duplicates")
public class LoopCommand extends MusicCommand {

  public LoopCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    // Manual override
    if (shouldForce()) {
      valid();
      return;
    }

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

    // User connected check
    if (BotUtils.getConnectedChannel(getGuild(), getUser()) == null) {
      new NotConnectedError(event).execute();
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
        getOutChannel());
  }
}
