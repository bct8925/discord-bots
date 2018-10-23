package com.bri64.bots.chillbot;

import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.music.KillCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;

@SuppressWarnings("WeakerAccess")
public class ChannelListener {

  private static String kickChannel = "~~~ /kick ~~~";
  private ChillBot bot;
  private MusicScheduler scheduler;

  public ChannelListener(final ChillBot bot, final MusicScheduler scheduler) {
    this.bot = bot;
    this.scheduler = scheduler;
  }

  @EventSubscriber
  public void onUserVoiceChannelMove(UserVoiceChannelMoveEvent event) {
    if (event.getUser().equals(bot.getUser())
        && (event.getNewChannel().equals(event.getGuild().getAFKChannel())
        || event.getNewChannel().getName().equalsIgnoreCase(kickChannel))) {
      new KillCommand(new CommandEvent(event.getGuild(), event.getNewChannel(),
          null, event.getUser(), null), scheduler).execute();
    }
  }
}