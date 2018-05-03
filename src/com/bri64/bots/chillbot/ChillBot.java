package com.bri64.bots.chillbot;

import com.bri64.bots.Bot;
import com.bri64.bots.chillbot.audio.MusicScheduler;
import com.bri64.bots.chillbot.db.DBManager;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.handle.obj.IGuild;

@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
public class ChillBot extends Bot {

  private IGuild guild;
  private MusicListener musicListener;
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

    // Connect to DB
    connectDB();

    // Register listeners
    registerListeners();

    // Initialize audio
    initTrackScheduler();

    // Shutdown DB on exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> dbm.stop()));
  }

  public IGuild getGuild() {
    return guild;
  }

  public MusicScheduler getMusicScheduler() {
    return musicScheduler;
  }

  public DBManager getDBM() {
    return dbm;
  }

  @Override
  protected void registerListeners() {
    client.getDispatcher().registerListener(musicListener = new MusicListener(this));
  }

  private void initTrackScheduler() {
    musicScheduler = new MusicScheduler(this);
  }

  private void connectDB() {
    dbm = new DBManager(this);
    dbm.start();
  }
}