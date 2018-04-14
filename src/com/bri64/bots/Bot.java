package com.bri64.bots;

import java.util.List;
import org.apache.commons.text.StringEscapeUtils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.RequestBuffer;

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
    while (!botListener.isReady()) {
      DankUtils.waiting();
    }
  }

  public void setStatus(String status) {
    RequestBuffer.request(() -> client.changePlayingText(status));
  }

  public IUser getUser() {
    return client.getOurUser();
  }

  public List<IVoiceChannel> getVoiceChannels() {
    return client.getConnectedVoiceChannels();
  }

  public void leaveChannels() {
    java.awt.EventQueue.invokeLater(() -> getVoiceChannels().forEach(IVoiceChannel::leave));
  }
}
