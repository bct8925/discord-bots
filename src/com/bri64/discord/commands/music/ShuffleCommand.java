package com.bri64.discord.commands.music;

import com.bri64.discord.BotUtils;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;

public class ShuffleCommand extends MusicCommand {

  public ShuffleCommand(final CommandEvent event, final MusicScheduler scheduler) {
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
    if (args.length > 2) {
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
    String mode = (getMessage().split(" ").length > 1) ? getMessage().split(" ")[1] : null;
    if (mode != null) {
      switch (mode.toLowerCase()) {
        case "on":
          scheduler.setShuffle(true);
          break;
        case "off":
          scheduler.setShuffle(false);
          break;
        default:
          invalidArgs();
          break;
      }
    } else {
      scheduler.shuffle();
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: shuffle [on|off]",
        getOutChannel());
  }
}
