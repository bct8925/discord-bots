package com.bri64.bots.commands.music;

import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.DiscordCommand;

@SuppressWarnings("WeakerAccess")
public abstract class MusicCommand extends DiscordCommand {

  protected MusicScheduler scheduler;

  protected MusicCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event);
    this.scheduler = scheduler;
  }
}
