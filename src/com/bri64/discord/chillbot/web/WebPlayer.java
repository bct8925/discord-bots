package com.bri64.discord.chillbot.web;

import com.bri64.discord.audio.send.LoopMode;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.chillbot.ChillBot;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.music.LoopCommand;
import com.bri64.discord.commands.music.NextCommand;
import com.bri64.discord.commands.music.PauseCommand;
import com.bri64.discord.commands.music.PlaylistChangedEvent;
import com.bri64.discord.commands.music.PreviousCommand;
import com.bri64.discord.commands.music.ShuffleCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class WebPlayer {

  private ChillBot bot;
  private MusicScheduler scheduler;

  private LoopMode loop;

  public WebPlayer(final ChillBot bot, final MusicScheduler scheduler) {
    this.bot = bot;
    this.scheduler = scheduler;

    this.loop = scheduler.getLoop();

    init();
  }

  private void init() {
    // Create webserver
    KwebPlayerKt.init();

    // Initialize callbacks
    PlayingState.Companion.setPlayCallback(() -> {
      new PauseCommand(new CommandEvent(bot.getGuild(), null, null, null, null, null, true),
          scheduler).execute();
      return null;
    });
    PlayingState.Companion.setPrevCallback(() -> {
      new PreviousCommand(new CommandEvent(bot.getGuild(), null, null, null, null, null, true),
          scheduler).execute();
      return null;
    });
    PlayingState.Companion.setNextCallback(() -> {
      new NextCommand(new CommandEvent(bot.getGuild(), null, null, null, null, null, true),
          scheduler).execute();
      return null;
    });
    PlayingState.Companion.setShuffleCallback(() -> {
      new ShuffleCommand(new CommandEvent(null, null, null, null, null, "", true),
          scheduler).execute();
      return null;
    });
    PlayingState.Companion.setLoopCallback(() -> {
      toggleLoop();
      new LoopCommand(new CommandEvent(null, null, null, null, null,
          "!loop " + loop.name().toLowerCase(), true), scheduler).execute();
      return null;
    });
    PlayingState.Companion.setCommandCallback((request) -> {
      if (request == null) {
        return null;
      }
      String command = "!" + request;
      IUser bri64 = bot.getGuild().getUserByID(150048671164137472L);
      IVoiceChannel channel = bot.getGuild().getVoiceChannelsByName("General").get(0);
      bot.dispatch(new CommandEvent(bot.getGuild(), bri64, channel, null, null, command, true));
      return null;
    });
  }

  @EventSubscriber
  public void onPlaylistStateChange(PlaylistChangedEvent event) {
    //socket.sendMessage("update");

    // Update playing text
    String text = (event.getTrack() != null) ? event.getTrack().getTitle() : "No song playing!";
    PlayingState.Companion.getNowPlaying().setValue(text);

    // Update playing status
    if (event.wasPaused()) {
      PlayingState.Companion.getPlaying().setValue(false);
    } else if (event.wasResumed()) {
      PlayingState.Companion.getPlaying().setValue(true);
    } else {
      PlayingState.Companion.getPlaying().setValue(true);
    }

    // Update playlist
    PlayingState.Companion.getPlaylist().setValue(scheduler.getNextSongs());

    // Update loop
    loop = scheduler.getLoop();
    PlayingState.Companion.getLoop().setValue(loop);

  }

  private void toggleLoop() {
    switch (loop) {
      case ALL:
        loop = LoopMode.ONE;
        break;
      case ONE:
        loop = LoopMode.NONE;
        break;
      default:
        loop = LoopMode.ALL;
        break;
    }
  }
}
