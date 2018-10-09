package com.bri64.bots.chillbot;

import com.bri64.bots.BotUtils;
import com.bri64.bots.MessageListener;
import com.bri64.bots.chillbot.audio.LoopMode;
import java.sql.SQLException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.ICategory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

@SuppressWarnings("WeakerAccess")
public class MusicListener extends MessageListener {

  private ChillBot main;

  public MusicListener(ChillBot main) {
    this.main = main;
    this.help = "```" +
        "ChillBot:\n" +
        "\t@ChillBot = Help Command\n" +
        "\t!play [url] = Play the audio at the specified url now\n" +
        "\t!queue [url] = Queue the audio at the specified url\n" +
        "\t!pause = Pause/unpause the current track\n" +
        "\t!song = Show info for currently playing track\n" +
        "\t!playlist = Show info for current playlist\n" +
        "\t!next/skip = Skip to next track in queue\n" +
        "\t!prev = Skip to previous track in queue\n" +
        "\t!seek [query] = Skip to next track whose title contains query\n" +
        "\t!loop [mode] = Toggle loop mode [none, one, all]\n" +
        "\t!shuffle [on, off] = Toggle queue shuffling or shuffle\n" +
        "\t!remove = Remove current track from queue\n" +
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

      // Queue/Play
      if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "queue") || message
          .split(" ")[0].equalsIgnoreCase(main.getSymbol() + "play")) {
        String url = message.split(" ")[1];
        boolean skip = message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "play");

        // Play audio if specified
        if (url != null) {
          if (BotUtils.getConnectedChannel(main.getGuild(), user) != null) {
            main.getMusicScheduler().loadTracks(user, url, skip);
          }
        }
      }

      // Pause
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "pause")) {
        main.getMusicScheduler().pause();
      }

      // Song Info
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "song")) {
        BotUtils.sendMessage(user.mention() + " " + main.getMusicScheduler().getTrackInfo(),
            user.getOrCreatePMChannel());
      }

      // Playlist Info
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "playlist")) {
        BotUtils.sendMessage(user.mention() + " " + main.getMusicScheduler().getPlaylistInfo(),
            user.getOrCreatePMChannel());
      }

      // Next
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "next") || message
          .split(" ")[0].equalsIgnoreCase(main.getSymbol() + "skip")) {
        main.getMusicScheduler().changeTrack(true);
      }

      // Previous
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "prev")) {
        main.getMusicScheduler().changeTrack(false);
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
        String arg = message.split(" ")[1];
        switch (arg.toLowerCase()) {
          case "none":
            main.getMusicScheduler().setLoop(LoopMode.NONE);
            break;
          case "one":
            main.getMusicScheduler().setLoop(LoopMode.ONE);
            break;
          case "all":
            main.getMusicScheduler().setLoop(LoopMode.ALL);
            break;
          default:
            BotUtils.sendMessage(
                user.mention() + " Invalid argument! Usage: " + main.getSymbol()
                    + "loop [none, one, all]",
                user.getOrCreatePMChannel());
            break;
        }

      }

      // Shuffle
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "shuffle")) {
        String mode = (message.split(" ").length > 1) ? message.split(" ")[1] : null;
        if (mode != null) {
          switch (mode.toLowerCase()) {
            case "on":
              main.getMusicScheduler().setShuffle(true);
              break;
            case "off":
              main.getMusicScheduler().setShuffle(false);
              break;
            default:
              BotUtils.sendMessage(
                  user.mention() + " Invalid argument! Usage: " + main.getSymbol()
                      + "shuffle [on, off]",
                  user.getOrCreatePMChannel());
              break;
          }
        } else {
          main.getMusicScheduler().shuffle();
        }
      }

      // Remove
      else if (message.split(" ")[0].equalsIgnoreCase(main.getSymbol() + "remove")) {
        if (main.getMusicScheduler().getPlaylistSize() > 1) {
          main.getMusicScheduler().remove();
        } else {
          main.getMusicScheduler().stop();
        }
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

      // Kill
      else if (message.equalsIgnoreCase(main.getSymbol() + "kill")) {
        main.getMusicScheduler().stop();
        main.leaveChannels();
      }

      // Else
      else if (message.matches("^[" + main.getSymbol() + "][^" + main.getSymbol() + "].*")) {
        try {
          String command = message.replaceAll("^" + main.getSymbol(), "").split(" ")[0];
          String url = null;
          String[] data = main.getDBM().getCommandData(command);
          if (data != null) {
            url = data[0];
          }

          // Play audio if specified
          if (url != null) {
            if (BotUtils.getConnectedChannel(main.getGuild(), user) != null) {
              main.getMusicScheduler().loadTracks(user, url, false);
            }
          }
        } catch (SQLException e) {
          main.getDBM().reconnect();
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
    } else if (main.isReady() && event.getNewChannel().getName()
        .equalsIgnoreCase("~~~ /kick ~~~")) {
      //System.out.println(event.getNewChannel().getRoleOverrides());
      event.getNewChannel().delete();
      IVoiceChannel newChannel = main.getGuild().createVoiceChannel("~~~ /kick ~~~");
      ICategory cata = main.getGuild().getCategoriesByName("Kick").get(0);
      newChannel.changeCategory(cata);
      //IRole everyone = main.getGuild().getEveryoneRole();
      //newChannel.overrideRolePermissions(everyone, null, EnumSet.of(Permissions.READ_MESSAGES, Permissions.VOICE_CONNECT));
    }
  }
}