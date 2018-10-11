package com.bri64.bots.chillbot;

import com.bri64.bots.Bot;
import com.bri64.bots.chillbot.audio.MusicScheduler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.handle.obj.IGuild;

@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
public class ChillBot extends Bot {

  private IGuild guild;
  private CommandListener commandListener;
  private MusicScheduler musicScheduler;
  private DBManager dbm;

  public ChillBot(String symbol, String token) {
    // Setup bot
    this.symbol = symbol;
    client = new ClientBuilder().withToken(token).build();

    // Register base listener
    registerBotListener();

    // Login and wait for connection
    login();

    // Setup guild
    this.guild = client.getGuilds().get(0);

    // Fix dangling instances
    fixDangles();

    // Connect to DB
    connectDB();

    // Initialize audio
    initTrackScheduler();

    // Register listeners
    registerListeners();

    // Shutdown DB on exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> dbm.stop()));
  }

  public IGuild getGuild() {
    return guild;
  }

  @Override
  protected void registerListeners() {
    client.getDispatcher()
        .registerListener(commandListener = new CommandListener(this, musicScheduler, dbm));
  }

  private void initTrackScheduler() {
    musicScheduler = new MusicScheduler(this);
  }

  private void connectDB() {
    dbm = new DBManager(this);
    dbm.start();
  }
}