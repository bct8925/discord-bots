package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.error.InvalidGuildError;

public class QueueCommand extends MusicCommand {

  public QueueCommand(final CommandEvent event, final MusicScheduler scheduler) {
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
    String URL = getMessage().split(" ")[1];
    scheduler.loadTracks(getUser(), URL, false);
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: queue url",
        getUser().getOrCreatePMChannel());
  }
}
