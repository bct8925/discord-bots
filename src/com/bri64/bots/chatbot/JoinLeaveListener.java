package com.bri64.bots.chatbot;

import com.bri64.bots.DBManager;
import com.bri64.bots.DiscordBot;
import com.bri64.bots.audio.MusicScheduler;
import com.bri64.bots.commands.KickCommand;
import java.sql.SQLException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class JoinLeaveListener {

  private static String kickChannel = "~~~ /kick ~~~";
  private DiscordBot bot;
  private MusicScheduler scheduler;
  private DBManager dbManager;


  public JoinLeaveListener(final DiscordBot bot, final MusicScheduler scheduler,
      final DBManager dbManager) {
    this.bot = bot;
    this.scheduler = scheduler;
    this.dbManager = dbManager;
  }

  @EventSubscriber
  public void onUserVoiceChannelJoin(UserVoiceChannelJoinEvent event) {
    if (!event.getUser().getName().equals(bot.getUser().getName())) {
      // Kick bot
      if (event.getVoiceChannel().getName().equalsIgnoreCase(kickChannel)) {
        new KickCommand(event, bot).execute();
      }
      // Join bot
      else {
        try {
          String[] data = dbManager
              .getJoinLeave(event.getUser().getName() + "#" + event.getUser().getDiscriminator());
          if (data != null) {
            event.getVoiceChannel().join();
            scheduler.loadTracks(event.getUser(), data[0], true);
          } else {
            event.getVoiceChannel().join();
            String url = "http://brianstrains.com/train2.mp3";
            scheduler.loadTracks(event.getUser(), url, true);
            dbManager
                .setJoin(event.getUser().getName() + "#" + event.getUser().getDiscriminator(), url);
          }
        } catch (SQLException e) {
          dbManager.reconnect();
        }
      }
    }
  }

  @EventSubscriber
  public void onUserVoiceChannelLeave(UserVoiceChannelLeaveEvent event) {
    if (!event.getUser().getName().equals(bot.getUser().getName())) {
      // Leave bot
      try {
        String[] data = dbManager
            .getJoinLeave(event.getUser().getName() + "#" + event.getUser().getDiscriminator());
        if (data != null) {
          event.getVoiceChannel().join();
          scheduler.loadTracks(event.getUser(), data[1], true);
        } else {
          event.getVoiceChannel().join();
          String url = "http://brianstrains.com/train3.mp3";
          scheduler.loadTracks(event.getUser(), url, true);
          dbManager
              .setLeave(event.getUser().getName() + "#" + event.getUser().getDiscriminator(), url);
        }
      } catch (SQLException e) {
        dbManager.reconnect();
      }
    }
  }
}
