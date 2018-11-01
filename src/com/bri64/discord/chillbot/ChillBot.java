package com.bri64.discord.chillbot;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DBManager;
import com.bri64.discord.DiscordBot;
import com.bri64.discord.audio.receive.ActivateListener;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.chillbot.web.WebPlayer;
import sx.blah.discord.handle.obj.IGuild;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ChillBot extends DiscordBot {

  private DBManager dbm;
  private MusicScheduler musicScheduler;
  private WebPlayer webPlayer;
  private ActivateListener voiceListener;
  private ChillListener chillListener;
  private ChannelListener channelListener;

  public ChillBot(String symbol, String token) {
    // Setup bot
    super(symbol, token);

    // Connect to DB
    dbm = new DBManager(this);
    dbm.start();

    // Initialize audio
    this.musicScheduler = new MusicScheduler(this);

    // Initialize web server
    this.webPlayer = new WebPlayer(this, musicScheduler);

    // Initialize voice
    //this.voiceListener = new ActivateListener(this);

    // Register listeners
    client.getDispatcher()
        .registerListener(chillListener = new ChillListener(this, musicScheduler, dbm));
    client.getDispatcher()
        .registerListener(channelListener = new ChannelListener(this, musicScheduler));

    // Shutdown DB on exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> dbm.stop()));
  }

  @Override
  public IGuild getGuild() {
    return guilds.get(0);
  }

  @Override
  public void reboot() {
    musicScheduler.pause(true);

    ready = false;
    while (!ready) {
      BotUtils.waiting();
    }

    musicScheduler.initAudio();
    musicScheduler.pause(false);
  }
}