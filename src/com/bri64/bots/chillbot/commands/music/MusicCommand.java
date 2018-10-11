package com.bri64.bots.chillbot.commands.music;

import com.bri64.bots.chillbot.audio.MusicScheduler;
import com.bri64.bots.chillbot.commands.MessageCommand;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;

@SuppressWarnings("WeakerAccess")
public abstract class MusicCommand extends MessageCommand {

  protected MusicScheduler scheduler;

  protected MusicCommand(final MessageEvent event, final MusicScheduler scheduler) {
    super(event);
    this.scheduler = scheduler;
  }
}
