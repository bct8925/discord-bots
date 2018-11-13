package com.bri64.discord.commands.music;

import com.bri64.discord.BotUtils;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;

public class PauseCommand extends MusicCommand {

  public PauseCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    // Manual override
    if (shouldForce()) {
      valid();
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
    scheduler.pause(!scheduler.isPaused());
  }
}
