package com.bri64.bots.chillbot;

import com.bri64.bots.DiscordBot;
import com.bri64.bots.chillbot.audio.MusicScheduler;
import sx.blah.discord.handle.obj.IGuild;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ChillBot extends DiscordBot {

  private DBManager dbm;
  private MusicScheduler musicScheduler;
  private CommandListener commandListener;
  private ChannelListener channelListener;

  public ChillBot(String symbol, String token) {
    // Setup bot
    super(symbol, token);

    // Fix dangling instances
    fixDangles();

    // Connect to DB
    dbm = new DBManager(this);
    dbm.start();

    // Initialize audio
    this.musicScheduler = new MusicScheduler(this);

    // Register listeners
    client.getDispatcher()
        .registerListener(commandListener = new CommandListener(this, musicScheduler, dbm));
    client.getDispatcher()
        .registerListener(channelListener = new ChannelListener(this, musicScheduler));

    // Shutdown DB on exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> dbm.stop()));
  }

  public IGuild getGuild() {
    return guilds.get(0);
  }
}