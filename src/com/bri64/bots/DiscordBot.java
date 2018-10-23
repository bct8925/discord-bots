package com.bri64.bots;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.text.StringEscapeUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.RequestBuffer;

@SuppressWarnings("WeakerAccess")
public abstract class DiscordBot {

  protected IDiscordClient client;
  protected String status;
  protected List<IGuild> guilds;
  protected String symbol;
  protected boolean ready;

  protected DiscordBot(final String symbol, final String token) {
    this.symbol = symbol;
    client = new ClientBuilder().withToken(token).build();

    // Register base listener
    client.getDispatcher().registerListener(this);

    // Login and wait for connection
    this.ready = false;
    login();

    // Set guilds
    this.guilds = client.getGuilds();

    // Keep updating status
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        updateStatus();
      }
    }, 0, 2000);
  }

  public IUser getUser() {
    return client.getOurUser();
  }

  public void setStatus(String status) {
    this.status = status;
    updateStatus();
  }

  public String getSymbol() {
    return StringEscapeUtils.escapeJava(symbol);
  }

  public abstract IGuild getGuild();

  public boolean isReady() {
    return ready;
  }

  private void login() {
    client.login();
    BotUtils.log(this, "Initializing...");
    while (!ready) {
      BotUtils.waiting();
    }
    BotUtils.log(this, "Ready!");
  }

  public void updateStatus() {
    if (status != null) {
      RequestBuffer.request(() ->
          client.changePresence(StatusType.ONLINE, ActivityType.LISTENING, status));
    } else {
      RequestBuffer.request(() ->
          client.changePresence(StatusType.ONLINE));
    }
  }

  public void dispatch(Event e) {
    client.getDispatcher().dispatch(e);
  }

  public List<IVoiceChannel> getVoiceChannels() {
    return client.getConnectedVoiceChannels();
  }

  public void joinChannel(IVoiceChannel channel) {
    channel.join();
    BotUtils.log(this, "Connected to \"" + channel.getName() + "\".");
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

  @EventSubscriber
  public void onReady(ReadyEvent event) {
    ready = true;
  }
}
