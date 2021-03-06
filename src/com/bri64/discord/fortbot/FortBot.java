package com.bri64.discord.fortbot;

import com.bri64.discord.DiscordBot;
import sx.blah.discord.handle.obj.IGuild;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "unused"})
public class FortBot extends DiscordBot {

  private FortListener fortListener;

  public FortBot(String symbol, String token) {
    // Setup bot
    super(symbol, token);

    // Register listeners
    client.getDispatcher().registerListener(fortListener = new FortListener(this));

    //RequestBuffer.request(
    //    () -> client.changePresence(StatusType.ONLINE, ActivityType.LISTENING, "@me for help!"));
  }

  @Override
  public IGuild getGuild() {
    return guilds.get(0);
  }

  @Override
  public void reboot() {

  }
}
