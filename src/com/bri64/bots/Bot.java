package com.bri64.bots;

import java.util.List;
import org.apache.commons.text.StringEscapeUtils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.RequestBuffer;

@SuppressWarnings("WeakerAccess")
public abstract class Bot {

  protected IDiscordClient client;
  protected String symbol;
  private BotListener botListener;

  public String getSymbol() {
    return StringEscapeUtils.escapeJava(symbol);
  }

  public boolean isReady() {
    return botListener.isReady();
  }

  protected void registerBotListener() {
    client.getDispatcher().registerListener(botListener = new BotListener());
  }

  protected abstract void registerListeners();

  protected void login() {
    client.login();
    BotUtils.log(this, "Initializing...");
    while (!botListener.isReady()) {
      BotUtils.waiting();
    }
    BotUtils.log(this, "Ready!");
  }

  public void clearStatus() {
    RequestBuffer
        .request(() -> client.changePresence(StatusType.ONLINE));
  }

  public void setStatus(String status) {
    RequestBuffer
        .request(() -> client.changePresence(StatusType.ONLINE, ActivityType.LISTENING, status));
  }

  public IUser getUser() {
    return client.getOurUser();
  }

  public List<IVoiceChannel> getVoiceChannels() {
    return client.getConnectedVoiceChannels();
  }

  public void leaveChannels() {
    java.awt.EventQueue.invokeLater(() -> getVoiceChannels().forEach((channel) -> {
      channel.leave();
      BotUtils.log(this, "Disconnected from voice channel \"" + channel.getName() + "\".");
    }));
  }

  public void fixDangles() {
    if (getVoiceChannels().size() > 0) {
      BotUtils.log(this, "Fixing dangling instances...");
      getVoiceChannels().forEach((channel) -> {
        try {
          channel.leave();
          Thread.sleep(1000);
          channel.join();
          Thread.sleep(1000);
          channel.leave();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
  }
}
