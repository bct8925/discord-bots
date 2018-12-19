package com.bri64.discord.phonebot;

import com.bri64.discord.DiscordBot;
import sx.blah.discord.handle.obj.IGuild;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class PhoneBot extends DiscordBot {

  public PhoneBot(String symbol, String token) {
    // Setup bot
    super(symbol, token);

  }

  @Override
  public IGuild getGuild() {
    return guilds.get(0);
  }

  @Override
  public void reboot() {
  }
}