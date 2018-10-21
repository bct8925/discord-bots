package com.bri64.bots.commands.music;

import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.error.InvalidGuildError;

public class PauseCommand extends MusicCommand {

  public PauseCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    // Valid guild check
    if (getGuild() == null) {
      new InvalidGuildError(event).execute();
      return;
    }

    valid();
  }

  @Override
  public void valid() {
    scheduler.pause();
  }
}
