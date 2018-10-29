package com.bri64.bots.chillbot;

import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.music.KillCommand;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("WeakerAccess")
public class ChannelListener {

  private static String kickChannel = "~~~ /kick ~~~";
  private static long ONE_MINUTE = 1000 * 60;

  private ChillBot bot;
  private MusicScheduler scheduler;
  private Timer afkTimer;

  public ChannelListener(final ChillBot bot, final MusicScheduler scheduler) {
    this.bot = bot;
    this.scheduler = scheduler;
    this.afkTimer = new Timer();
  }

  @EventSubscriber
  public void onUserVoiceChannelMove(UserVoiceChannelMoveEvent event) {
    bot.dispatch(new UserVoiceChannelJoinEvent(event.getNewChannel(), event.getUser()));
    bot.dispatch(new UserVoiceChannelLeaveEvent(event.getOldChannel(), event.getUser()));
  }

  @EventSubscriber
  public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
    if (!event.getUser().equals(bot.getUser())) {
      List<IUser> connectedUsers = event.getVoiceChannel().getConnectedUsers();
      if (connectedUsers.contains(bot.getUser())) {
        cleanTimer();
      }
    } else {
      if (event.getVoiceChannel().equals(event.getGuild().getAFKChannel())
          || event.getVoiceChannel().getName().equalsIgnoreCase(kickChannel)) {
        new KillCommand(new CommandEvent(event.getGuild(), null,
            null, null, null), scheduler).execute();
      } else if (event.getVoiceChannel().getConnectedUsers().size() == 1) {
        startTimer();
      }
    }
  }

  @EventSubscriber
  public void onUserVoiceChannelLeave(UserVoiceChannelLeaveEvent event) {
    if (!event.getUser().equals(bot.getUser())) {
      List<IUser> connectedUsers = event.getVoiceChannel().getConnectedUsers();
      if (connectedUsers.contains(bot.getUser())
          && connectedUsers.size() == 1) {
        startTimer();
      }
    }
  }

  private synchronized void startTimer() {
    cleanTimer();
    afkTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        new KillCommand(new CommandEvent(bot.getGuild(), null,
            null, null, null), scheduler).execute();
      }
    }, ONE_MINUTE);
  }

  private synchronized void cleanTimer() {
    try {
      afkTimer.cancel();
    } catch (Exception ignored) {
    }

    afkTimer = new Timer();
  }
}