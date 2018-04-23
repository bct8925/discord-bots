package com.bri64.bots.fortbot;

import com.bri64.bots.DankUtils;
import com.bri64.bots.MessageListener;
import com.bri64.bots.fortbot.json.FortniteAccount;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class FortListener extends MessageListener {

  private FortBot main;

  private Map<String, Player> trackedPlayers;

  public FortListener(FortBot main) {
    this.main = main;
    this.help = "```" +
        "FortBot:\n" +
        "\t@FortBot = Help Command\n" +
        "\t!stats [username] = Check player stats\n" +
        "\t!track [username] = Start tracking player stats\n" +
        "\t!untrack [username] = Stop tracking player stats\n" +
        "\t!winner = Update stats manually\n" +
        "\t!update = Update stats manually" +
        "```";

    this.trackedPlayers = new HashMap<>();

    new Thread(() -> {
      while (!main.isReady()) {
        DankUtils.waiting();
      }

      trackPlayer("bri64");
      trackPlayer("thedukenator");
      trackPlayer("bluemidget14");
      trackPlayer("ttimhcsnad");

      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          updatePlayers();
        }
      }, 5000, 300000);
    }).start();
  }

  @EventSubscriber
  @Override
  public void onMention(MentionEvent event) {
    if (!event.getMessage().getContent().contains("@everyone") && !event.getMessage().getContent()
        .contains("@here")) {
      IUser user = event.getMessage().getAuthor();
      IChannel channel = user.getOrCreatePMChannel();
      DankUtils.sendMessage(help, channel);
    }
  }

  @EventSubscriber
  @Override
  public void onMessage(MessageReceivedEvent event) {
    if (main.isReady()) {

      // Setup variables
      IUser user = event.getMessage().getAuthor();
      String message = event.getMessage().getContent();

      // Stats
      if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "stats")) {
        String userName = message.split(" ")[1];
        FortniteAccount account = getAccount(userName);
        if (account != null) {
          DankUtils.sendMessage("```\n" + account.toString() + "```", user.getOrCreatePMChannel());
        } else {
          DankUtils
              .sendMessage(user.mention() + " An error occurred!", user.getOrCreatePMChannel());
        }
      }

      // Track
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "track")) {
        String userName = message.split(" ")[1];
        if (trackPlayer(userName)) {
          DankUtils
              .sendMessage(user.mention() + " Tracking successful.", user.getOrCreatePMChannel());
        } else {
          DankUtils
              .sendMessage(user.mention() + " An error occurred!", user.getOrCreatePMChannel());
        }
      }

      // Untrack
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "untrack")) {
        String userName = message.split(" ")[1];
        trackedPlayers.remove(userName);
        DankUtils
            .sendMessage(user.mention() + " Untracking successful.", user.getOrCreatePMChannel());
      }

      // Update/Winner
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "winner") || message
          .split(" ")[0].equalsIgnoreCase(main.getSymbol() + "update")) {
        updatePlayers();
      }

      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "test")) {
        String[] userNames = message.split(" ");
        Map<String, Integer> temp = new HashMap<>();
        for (int i = 1; i < userNames.length; i += 2) {
          temp.put(userNames[i], Integer.parseInt(userNames[i + 1]));
        }
        DankUtils.sendMessage(testMessage(temp), DankUtils.getChannelByName(main.getGuild(), "trophy_room"));
      }
    }
  }

  private synchronized void updatePlayers() {
    Map<Player, FortniteAccount> winners = new HashMap<>();
    for (String username : trackedPlayers.keySet()) {
      Player player = trackedPlayers.get(username);
      FortniteAccount account = getAccount(username);
      if (account != null) {
        if (player.winHappened(account)) {
          winners.put(player, account);
        }
        trackedPlayers.put(username, new Player(username, account));
      }
    }

    if (!winners.isEmpty()) {
      DankUtils.sendMessage(
          winnerMessage(winners), DankUtils.getChannelByName(main.getGuild(), "trophy_room"));
    }
  }

  private EmbedObject winnerMessage(Map<Player, FortniteAccount> winners) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(66, 188, 244);
    builder.withImage("https://cdn.discordapp.com/attachments/434758225640816641/435235573398241281/logo.png");

    String response = "";
    for (Player p : winners.keySet()) {
      response += p.getUsername() + ": " + p.getNewKills(winners.get(p)) + " kills\n";
    }
    builder.appendField("#1 Victory Royale!", "***" + response + "***", false);

    return builder.build();
  }

  private EmbedObject testMessage(Map<String, Integer> winners) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(66, 188, 244);
    builder.withImage("https://cdn.discordapp.com/attachments/434758225640816641/435235573398241281/logo.png");

    String response = "";
    for (String username : winners.keySet()) {
      response += username + ": " + winners.get(username) + " kills\n";
    }
    builder.appendField("#1 Victory Royale!", "***" + response + "***", false);

    return builder.build();
  }

  private synchronized boolean trackPlayer(String username) {
    FortniteAccount account = getAccount(username);
    if (account != null) {
      trackedPlayers.put(username, new Player(username, account));
      return true;
    }
    return false;
  }

  private synchronized FortniteAccount getAccount(String userName) {
    try {
      Thread.sleep(2001);

      OkHttpClient client = new OkHttpClient();
      String API_URL = "https://api.fortnitetracker.com/v1/profile/pc/";
      Request request = new Request.Builder()
          .url(API_URL + userName)
          .get()
          .addHeader("TRN-Api-Key", "5f1ca39c-d3f0-4f27-9bb0-1d5832ff3f39")
          .addHeader("Accept", "application/json")
          .build();
      Response response = client.newCall(request).execute();
      String json = response.body().string();
      return new Gson().fromJson(json, FortniteAccount.class);
    } catch (Exception e) {
      return null;
    }
  }
}
