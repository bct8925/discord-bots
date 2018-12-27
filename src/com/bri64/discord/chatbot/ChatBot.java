package com.bri64.discord.chatbot;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DBManager;
import com.bri64.discord.DiscordBot;
import com.bri64.discord.audio.send.LoopMode;
import com.bri64.discord.audio.send.MusicScheduler;
import sx.blah.discord.handle.obj.IGuild;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ChatBot extends DiscordBot {

  private DBManager dbm;
  private MusicScheduler musicScheduler;
  private ChatListener chatListener;
  private JoinLeaveListener joinLeaveListener;

  public ChatBot(String symbol, String token) {
    // Setup bot
    super(symbol, token);

    // Connect to DB
    dbm = new DBManager(this);
    dbm.start();

    // Initialize audio
    this.musicScheduler = new MusicScheduler(this);
    musicScheduler.setLoop(LoopMode.NONE);
    musicScheduler.setVolume(10);

    // Register listeners
    client.getDispatcher()
        .registerListener(chatListener = new ChatListener(this, musicScheduler, dbm));
    client.getDispatcher()
        .registerListener(joinLeaveListener = new JoinLeaveListener(this, musicScheduler, dbm));

    // Shutdown DB on exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> dbm.stop()));
  }

  public IGuild getGuild() {
    return guilds.get(0);
  }

  @Override
  public void reboot() {
    while (!client.isReady()) {
      BotUtils.waiting();
    }

    fixDangles();
    musicScheduler.initAudio();
  }
}
