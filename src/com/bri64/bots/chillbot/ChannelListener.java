package com.bri64.bots.chillbot;

import com.bri64.bots.DiscordBot;
import com.bri64.bots.audio.MusicScheduler;
import com.bri64.bots.commands.music.KillCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;

@SuppressWarnings("WeakerAccess")
public class ChannelListener {

  private DiscordBot bot;
  private MusicScheduler scheduler;

  public ChannelListener(final DiscordBot bot, final MusicScheduler scheduler) {
    this.bot = bot;
    this.scheduler = scheduler;
  }

  @EventSubscriber
  public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
    if (event.getUser().getName().equals(bot.getUser().getName())
        && event.getVoiceChannel().equals(event.getGuild().getAFKChannel())) {
      new KillCommand(null, scheduler).execute();
    }
  }
}