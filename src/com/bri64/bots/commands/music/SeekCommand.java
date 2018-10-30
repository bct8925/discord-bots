package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.error.InvalidGuildError;
import com.bri64.bots.commands.error.NotConnectedError;

public class SeekCommand extends MusicCommand {

  public SeekCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    // Argument check
    String[] args = getMessage().split(" ");
    if (args.length < 2) {
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
    String search = getMessage().replace(getMessage().split(" ")[0] + " ", "");
    if (!scheduler.seek(search)) {
      BotUtils.sendMessage(
          getUser().mention() + " " + "Track containing \"" + search + "\" not found in queue!",
          getUser().getOrCreatePMChannel());
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: seek query",
        getUser().getOrCreatePMChannel());
  }
}
