package com.bri64.discord.commands.music;

import com.bri64.discord.BotUtils;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;

@SuppressWarnings("Duplicates")
public class PlayCommand extends MusicCommand {

  public PlayCommand(final CommandEvent event, final MusicScheduler scheduler, boolean force) {
    super(event, scheduler, force);
  }

  @Override
  public void execute() {
    // Manual override
    if (force) {
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
    String URL = getMessage().split(" ")[1];
    scheduler.loadTracks(getUser(), URL, true);
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: play url",
        getUser().getOrCreatePMChannel());
  }
}
