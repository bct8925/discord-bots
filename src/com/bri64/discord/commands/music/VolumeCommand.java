package com.bri64.discord.commands.music;

import com.bri64.discord.BotUtils;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;

@SuppressWarnings("Duplicates")
public class VolumeCommand extends MusicCommand {

  public VolumeCommand(final CommandEvent event, final MusicScheduler scheduler) {
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
    try {
      int percent = Integer.parseInt(getMessage().split(" ")[1]);
      if (percent > 0 && percent <= 50) {
        scheduler.setVolume(percent);
      } else {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException ex) {
      BotUtils.sendMessage(getUser().mention() + " " + "Percent must be a number 1-50!",
          getOutChannel());
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: volume percent",
        getOutChannel());
  }
}
