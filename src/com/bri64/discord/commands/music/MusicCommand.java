package com.bri64.discord.commands.music;

import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.DiscordCommand;

@SuppressWarnings("WeakerAccess")
public abstract class MusicCommand extends DiscordCommand {

  protected MusicScheduler scheduler;

  protected MusicCommand(final CommandEvent event, final MusicScheduler scheduler, boolean force) {
    super(event, force);
    this.scheduler = scheduler;
  }
}
