package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.error.InvalidGuildError;

public class VolumeCommand extends MusicCommand {

  public VolumeCommand(final CommandEvent event, final MusicScheduler scheduler) {
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
    try {
      int percent = Integer.parseInt(getMessage().split(" ")[1]);
      if (percent > 0 && percent <= 50) {
        scheduler.setVolume(percent);
      } else {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException ex) {
      BotUtils.sendMessage(getUser().mention() + " " + "Percent must be a number 1-50!",
          getUser().getOrCreatePMChannel());
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: volume percent",
        getUser().getOrCreatePMChannel());
  }
}
