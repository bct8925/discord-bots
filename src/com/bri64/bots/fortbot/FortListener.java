package com.bri64.bots.fortbot;

import com.bri64.bots.BotUtils;
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

  private static String API_KEY = "5f1ca39c-d3f0-4f27-9bb0-1d5832ff3f39";
  private static String IMG_URL = "https://cdn.discordapp.com/attachments/434758225640816641/435235573398241281/logo.png";

  private static String CHANNEL = "trophy_room";
  private static int ONE_MINUTE = 60000;
  private static int UPDATE_DELAY = ONE_MINUTE * 2;
  
  private FortBot main;

  private Map<String, Player> trackedPlayers;

  public FortListener(FortBot main) {
    this.main = main;
    this.help = "```" +
        "FortBot:\n" +
        "\t@FortBot = Help Command\n" +
        "\t!stats [platform/username] = Check player stats\n" +
        "\t!track [platform/username] = Start tracking player stats\n" +
        "\t!untrack [platform/username] = Stop tracking player stats\n" +
        "\t!winner = Update stats after a win\n" +
        "\t!update = Update stats manually" +
        "```";

    this.trackedPlayers = new HashMap<>();

    new Thread(() -> {
      while (!main.isReady()) {
        BotUtils.waiting();
      }

      trackPlayer("pc/bri64");
      trackPlayer("pc/thedukenator");
      trackPlayer("pc/Stoquertk");
      trackPlayer("pc/ttimhcsnad");
      trackPlayer("pc/lwizzle9dizzle");

      /*
      trackPlayer("pc/Ahdrick");
      trackPlayer("pc/Abbádon");
      trackPlayer("pc/CallsignWhisper");
      trackPlayer("psn/glymzz");
      */

      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          updatePlayers();
        }
      }, 5000, UPDATE_DELAY);
    }).start();
  }

  @EventSubscriber
  @Override
  public void onMention(MentionEvent event) {
    if (!event.getMessage().getContent().contains("@everyone") && !event.getMessage().getContent()
        .contains("@here")) {
      IUser user = event.getMessage().getAuthor();
      IChannel channel = user.getOrCreatePMChannel();
      BotUtils.sendMessage(help, channel);
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
        String username = message.split(" ")[1];
        FortniteAccount account = getAccount(username);
        if (account != null) {
          BotUtils.sendMessage("```\n" + account.toString() + "```", user.getOrCreatePMChannel());
        } else {
          BotUtils
              .sendMessage(user.mention() + " Could not load " + username + "'s stats",
                  user.getOrCreatePMChannel());
        }
      }

      // Track
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "track")) {
        String username = message.split(" ")[1];
        if (trackPlayer(username)) {
          BotUtils
              .sendMessage(user.mention() + " Tracking successful.", user.getOrCreatePMChannel());
        } else {
          BotUtils
              .sendMessage(user.mention() + " Could not load " + username + "'s stats",
                  user.getOrCreatePMChannel());
        }
      }

      // Untrack
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "untrack")) {
        String username = message.split(" ")[1];
        trackedPlayers.remove(username);
        BotUtils
            .sendMessage(user.mention() + " Untracking successful.", user.getOrCreatePMChannel());
        BotUtils.log(main, " " + username + " untracked.");
      }

      // Update/Winner
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "winner") || message
          .split(" ")[0].equalsIgnoreCase(main.getSymbol() + "update")) {
        updatePlayers();
      }

      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "test")) {
        String[] usernames = message.split(" ");
        Map<String, Integer> temp = new HashMap<>();
        for (int i = 1; i < usernames.length; i += 2) {
          temp.put(usernames[i], Integer.parseInt(usernames[i + 1]));
        }
        BotUtils
            .sendMessage(testMessage(temp), BotUtils.getChannelByName(main.getGuild(), CHANNEL));
      }
    }
  }

  private synchronized void updatePlayers() {
    BotUtils.log(main, " Updating stats...");
    boolean won = false;
    Map<String, FortniteAccount> updatedStats = new HashMap<>();
    for (String username : trackedPlayers.keySet()) {
      Player player = trackedPlayers.get(username);
      FortniteAccount account = getAccount(username);
      if (account != null) {
        updatedStats.put(username, account);
        if (player.winHappened(account)) {
          won = true;
        }
      }
    }

    // Win, recheck for wins
    if (won) {
      BotUtils.log(main, " Wins detected");
      try {
        Thread.sleep(ONE_MINUTE);
      } catch (Exception ignored) {
      }

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

      BotUtils.sendMessage(
          winnerMessage(winners), BotUtils.getChannelByName(main.getGuild(), CHANNEL));
    }

    // No wins, update stats
    else {
      for (String username : updatedStats.keySet()) {
        trackedPlayers.put(username, new Player(username, updatedStats.get(username)));
      }
    }
  }

  private EmbedObject winnerMessage(Map<Player, FortniteAccount> winners) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(66, 188, 244);
    builder.withImage(IMG_URL);

    StringBuilder response = new StringBuilder();
    for (Player p : winners.keySet()) {
      response.append(p.getDisplayName(winners.get(p))).append(": ")
          .append(p.getNewKills(winners.get(p))).append(" kills\n");
    }
    builder.appendField("#1 Victory Royale!", "***" + response + "***", false);

    return builder.build();
  }

  private EmbedObject testMessage(Map<String, Integer> winners) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(66, 188, 244);
    builder.withImage(IMG_URL);

    StringBuilder response = new StringBuilder();
    for (String username : winners.keySet()) {
      response.append(username).append(": ").append(winners.get(username)).append(" kills\n");
    }
    builder.appendField("#1 Victory Royale!", "***" + response + "***", false);

    return builder.build();
  }

  private synchronized boolean trackPlayer(String username) {
    FortniteAccount account = getAccount(username);
    if (account != null) {
      trackedPlayers.put(username, new Player(username, account));
      BotUtils.log(main, " " + username + " tracked.");
      return true;
    }
    return false;
  }

  private synchronized FortniteAccount getAccount(String username) {
    try {
      Thread.sleep(2001);

      OkHttpClient client = new OkHttpClient();
      String API_URL = "https://api.fortnitetracker.com/v1/profile/";
      Request request = new Request.Builder()
          .url(API_URL + username)
          .get()
          .addHeader("TRN-Api-Key", API_KEY)
          .addHeader("Accept", "application/json")
          .build();
      Response response = client.newCall(request).execute();
      String json = response.body().string();
      FortniteAccount account = new Gson().fromJson(json, FortniteAccount.class);
      if (account.getError() != null) {
        return null;
      }
      return account;
    } catch (Exception e) {
      BotUtils.log(main, " [Error] Could not load " + username + "'s stats");
      return null;
    }
  }
}
