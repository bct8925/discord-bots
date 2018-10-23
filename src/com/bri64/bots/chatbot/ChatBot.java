package com.bri64.bots.chatbot;

import com.bri64.bots.DBManager;
import com.bri64.bots.DiscordBot;
import com.bri64.bots.audio.send.LoopMode;
import com.bri64.bots.audio.send.MusicScheduler;
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

    // Fix dangling instances
    fixDangles();

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
}
