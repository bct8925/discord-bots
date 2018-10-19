package com.bri64.bots.chillbot;

import com.bri64.bots.chillbot.audio.MusicScheduler;
import com.bri64.bots.chillbot.commands.KickCommand;
import com.bri64.bots.chillbot.commands.music.KillCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class ChannelListener {

  private static String kickChannel = "~~~ /kick ~~~";
  private ChillBot bot;
  private MusicScheduler scheduler;

  public ChannelListener(final ChillBot bot, final MusicScheduler scheduler) {
    this.bot = bot;
    this.scheduler = scheduler;
  }

  @EventSubscriber
  public void onUserVoiceChannelChange(UserVoiceChannelMoveEvent event) {
    if (bot.isReady()) {
      if (event.getUser().getName().equals(bot.getUser().getName())
          && event.getNewChannel() == event.getGuild().getAFKChannel()) {
        new KillCommand(null, scheduler).execute();
      } else if (event.getNewChannel().getName().equalsIgnoreCase(kickChannel)
          && !event.getUser().getName().equals(bot.getUser().getName())) {
        new KickCommand(event, bot).execute();
      }
    }
  }
}