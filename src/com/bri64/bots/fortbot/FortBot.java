package com.bri64.bots.fortbot;

import com.bri64.bots.Bot;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.RequestBuffer;

@SuppressWarnings("WeakerAccess")
public class FortBot extends Bot {

  private IGuild guild;
  private FortListener fortListener;

  public FortBot(String symbol, String token) {
    // Setup bot
    this.symbol = symbol;
    client = new ClientBuilder().withToken(token).build();

    // Register base listener
    registerBotListener();

    // Login and wait for connection
    login();

    // Setup guild
    this.guild = client.getGuilds().get(0);

    // Register listeners
    registerListeners();

    RequestBuffer.request(
        () -> client.changePresence(StatusType.ONLINE, ActivityType.LISTENING, "@me for help!"));
  }

  public IGuild getGuild() {
    return guild;
  }

  @Override
  protected void registerListeners() {
    client.getDispatcher().registerListener(fortListener = new FortListener(this));
  }
}
