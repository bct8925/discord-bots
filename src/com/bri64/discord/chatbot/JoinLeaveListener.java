package com.bri64.discord.chatbot;

import com.bri64.discord.DBManager;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.KickCommand;
import java.sql.SQLException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class JoinLeaveListener {

  private static String kickChannel = "~~~ /kick ~~~";
  private ChatBot bot;
  private MusicScheduler scheduler;
  private DBManager dbManager;


  public JoinLeaveListener(final ChatBot bot, final MusicScheduler scheduler,
      final DBManager dbManager) {
    this.bot = bot;
    this.scheduler = scheduler;
    this.dbManager = dbManager;
  }

  @EventSubscriber
  public void onUserVoiceChannelMove(UserVoiceChannelMoveEvent event) {
    if (event.getNewChannel().getName().equalsIgnoreCase(kickChannel)
        && !event.getUser().isBot()) {
      new KickCommand(event, bot).execute();
    }
  }

  @EventSubscriber
  public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
    if (!event.getUser().equals(bot.getUser())) {
      String username = event.getUser().getName() + "#" + event.getUser().getDiscriminator();
      try {
        String[] data = dbManager
            .getJoinLeave(username);
        if (data != null) {
          event.getVoiceChannel().join();
          scheduler.loadTracks(event.getUser(), data[0], true);
        } else {
          event.getVoiceChannel().join();
          String url = "http://brianstrains.com/train2.mp3";
          scheduler.loadTracks(event.getUser(), url, true);
          dbManager
              .setJoin(username, url);
        }
      } catch (SQLException e) {
        dbManager.reconnect();
      }
    }
  }

  @EventSubscriber
  public void onUserVoiceChannelLeave(UserVoiceChannelLeaveEvent event) {
    if (!event.getUser().equals(bot.getUser())) {
      String username = event.getUser().getName() + "#" + event.getUser().getDiscriminator();
      try {
        String[] data = dbManager
            .getJoinLeave(username);
        if (data != null) {
          event.getVoiceChannel().join();
          scheduler.loadTracks(event.getUser(), data[1], true);
        } else {
          event.getVoiceChannel().join();
          String url = "http://brianstrains.com/train3.mp3";
          scheduler.loadTracks(event.getUser(), url, true);
          dbManager
              .setLeave(username, url);
        }
      } catch (SQLException e) {
        dbManager.reconnect();
      }
    }
  }
}
