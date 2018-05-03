package com.bri64.bots.chillbot;

import com.bri64.bots.BotUtils;
import com.bri64.bots.MessageListener;
import java.sql.SQLException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("WeakerAccess")
public class MusicListener extends MessageListener {

  private ChillBot main;

  public MusicListener(ChillBot main) {
    this.main = main;
    this.help = "```" +
        "ChillBot:\n" +
        "\t@ChillBot = Help Command\n" +
        "\t!queue [url] = Queue the audio at the specified url\n" +
        "\t!search [query] = Show the top 5 videos from the query\n" +
        "\t!qsearch [query] = Queue the top 5 videos from the query\n" +
        "\t!info = Show info for currently playing track\n" +
        "\t!skip = Skip to next track in queue\n" +
        "\t!seek [query] = Skip to next track whose title contains query\n" +
        "\t!loop = Toggle queue looping\n" +
        "\t!shuffle = Toggle queue shuffling on playlist load\n" +
        "\t!add [command] [url] [title] = [Admin] Add a new emote command to the database\n" +
        "\t!kill = Clear queue and kick ChillBot from channel" +
        "```";
  }

  @EventSubscriber
  public void onMention(MentionEvent event) {
    if (!event.getMessage().getContent().contains("@everyone") && !event.getMessage().getContent()
        .contains("@here")) {
      IUser user = event.getMessage().getAuthor();
      IChannel channel = user.getOrCreatePMChannel();
      BotUtils.sendMessage(help, channel);
    }
  }

  @EventSubscriber
  public void onMessage(MessageReceivedEvent event) {
    if (main.isReady()) {
      // Setup variables
      IUser user = event.getMessage().getAuthor();
      String message = event.getMessage().getContent();
      String url = null;
      boolean skip = false;

      // Help
      if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "help")) {
        BotUtils.sendMessage(help, user.getOrCreatePMChannel());
      }

      // Pause
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "pause")) {
        main.getMusicScheduler().pause();
      }

      // Queue/Play
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "queue") || message
          .split(" ")[0].equalsIgnoreCase(main.getSymbol() + "play")) {
        url = message.split(" ")[1];
        skip = message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "play");
      }

      // Info
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "info")) {
        BotUtils.sendMessage(user.mention() + " " + main.getMusicScheduler().getTrackInfo(),
            user.getOrCreatePMChannel());
      }

      // Skip
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "skip")) {
        main.getMusicScheduler().skip();
      }

      // Seek
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "seek")) {
        String search = message.replace(message.split(" ")[0] + " ", "");
        if (!main.getMusicScheduler().seek(search)) {
          BotUtils.sendMessage(
              user.mention() + " Track containing \"" + search + "\" not found in playlist.",
              user.getOrCreatePMChannel());
        }
      }

      // Loop
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "loop")) {
        main.getMusicScheduler().setLoop(!main.getMusicScheduler().isLoop());
      }

      // Shuffle
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "shuffle")) {
        main.getMusicScheduler().setShuffle(!main.getMusicScheduler().isShuffle());
      }

      // Add
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "add")) {
        try {
          String userName = user.getName() + "#" + user.getDiscriminator();
          if (main.getDBM().isAdmin(userName)) {
            if (message.split(" ").length >= 3) {
              String newcomm = message.split(" ")[1];
              String newurl = message.split(" ")[2];
              main.getDBM().addCommand(newcomm, newurl);
              BotUtils
                  .sendMessage(user.mention() + " added " + newcomm, user.getOrCreatePMChannel());
            } else {
              BotUtils.sendMessage(
                  user.mention() + " USAGE: " + main.getSymbol() + "add [command] [url]",
                  user.getOrCreatePMChannel());
            }
          }
        } catch (SQLException e) {
          main.getDBM().reconnect();
        }
      }

      // List
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "list")) {
        main.getMusicScheduler().getList(user);
      }

      // Kill
      else if (message.equalsIgnoreCase(main.getSymbol() + "kill")) {
        main.getMusicScheduler().stop();
        main.leaveChannels();
      }

      // Else
      else if (message.matches("^[" + main.getSymbol() + "][^" + main.getSymbol() + "].*")) {
        try {
          String command = message.replaceAll("^" + main.getSymbol(), "").split(" ")[0];
          String[] data = main.getDBM().getCommandData(command);
          if (data != null) {
            url = data[0];
          } /*else {
            BotUtils
                .sendMessage(user.mention() + " \"" + message + "\" is not a dank enough command!",
                    user.getOrCreatePMChannel());
          }*/
        } catch (SQLException e) {
          main.getDBM().reconnect();
        }
      }

      // Play audio if specified
      if (url != null) {
        if (BotUtils.getConnectedChannel(main.getGuild(), user) != null) {
          main.getMusicScheduler().loadTracks(user, url, skip);
        }
      }
    }
  }

  @EventSubscriber
  public void onUserVoiceChannelChange(UserVoiceChannelMoveEvent event) {
    if (main.isReady() && event.getUser().getName().equals(main.getUser().getName())
        && event.getNewChannel() == main.getGuild().getAFKChannel()) {
      main.getMusicScheduler().stop();
      main.leaveChannels();
    }
  }
}