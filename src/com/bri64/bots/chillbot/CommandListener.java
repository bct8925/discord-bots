package com.bri64.bots.chillbot;

import com.bri64.bots.MessageListener;
import com.bri64.bots.chillbot.audio.MusicScheduler;
import com.bri64.bots.chillbot.commands.HelpCommand;
import com.bri64.bots.chillbot.commands.KickCommand;
import com.bri64.bots.chillbot.commands.db.AddDBCommand;
import com.bri64.bots.chillbot.commands.db.PlayDBCommand;
import com.bri64.bots.chillbot.commands.music.KillCommand;
import com.bri64.bots.chillbot.commands.music.LoopCommand;
import com.bri64.bots.chillbot.commands.music.NextCommand;
import com.bri64.bots.chillbot.commands.music.PauseCommand;
import com.bri64.bots.chillbot.commands.music.PlayCommand;
import com.bri64.bots.chillbot.commands.music.PlaylistInfoCommand;
import com.bri64.bots.chillbot.commands.music.PreviousCommand;
import com.bri64.bots.chillbot.commands.music.QueueCommand;
import com.bri64.bots.chillbot.commands.music.RemoveTrackCommand;
import com.bri64.bots.chillbot.commands.music.SeekCommand;
import com.bri64.bots.chillbot.commands.music.ShuffleCommand;
import com.bri64.bots.chillbot.commands.music.SongInfoCommand;
import com.bri64.bots.chillbot.commands.music.VolumeCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;

@SuppressWarnings("WeakerAccess")
public class CommandListener extends MessageListener {

  private static String kickChannel = "~~~ /kick ~~~";
  private ChillBot bot;
  private MusicScheduler scheduler;
  private DBManager database;

  public CommandListener(final ChillBot bot, final MusicScheduler scheduler,
      final DBManager database) {
    this.bot = bot;
    this.scheduler = scheduler;
    this.database = database;
  }

  @EventSubscriber
  public void onMention(MentionEvent event) {
    new HelpCommand(event).execute();
  }

  @EventSubscriber
  public void onMessage(MessageReceivedEvent event) {
    if (bot.isReady()) {
      // Setup variables
      String message = event.getMessage().getContent();

      // Play
      if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "play")) {
        new PlayCommand(event, scheduler).execute();
      }

      // Queue
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "queue")) {
        new QueueCommand(event, scheduler).execute();
      }

      // Pause
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "pause")) {
        new PauseCommand(event, scheduler).execute();
      }

      // Song Info
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "song")) {
        new SongInfoCommand(event, scheduler).execute();
      }

      // Playlist Info
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "playlist")) {
        new PlaylistInfoCommand(event, scheduler).execute();
      }

      // Next
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "next") || message
          .split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "skip")) {
        new NextCommand(event, scheduler).execute();
      }

      // Previous
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "prev")) {
        new PreviousCommand(event, scheduler).execute();
      }

      // Seek
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "seek")) {
        new SeekCommand(event, scheduler).execute();
      }

      // Loop
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "loop")) {
        new LoopCommand(event, scheduler).execute();
      }

      // Shuffle
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "shuffle")) {
        new ShuffleCommand(event, scheduler).execute();
      }

      // Remove
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "remove")) {
        new RemoveTrackCommand(event, scheduler).execute();
      }

      // Volume
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "volume")) {
        new VolumeCommand(event, scheduler).execute();
      }

      // Add
      else if (message.split(" ")[0].equalsIgnoreCase(bot.getSymbol() + "add")) {
        new AddDBCommand(event, database).execute();
      }

      // Kill
      else if (message.equalsIgnoreCase(bot.getSymbol() + "kill")) {
        new KillCommand(event, scheduler).execute();
      }

      // Else
      else if (message.matches("^[" + bot.getSymbol() + "][^" + bot.getSymbol() + "].*")) {
        new PlayDBCommand(event, scheduler, database).execute();
      }
    }
  }

  @EventSubscriber
  public void onUserVoiceChannelChange(UserVoiceChannelMoveEvent event) {
    if (bot.isReady()) {
      if (event.getUser().getName().equals(bot.getUser().getName())
          && event.getNewChannel() == event.getGuild().getAFKChannel()) {
        new KillCommand(null, scheduler).execute();
      } else if (event.getNewChannel().getName().equalsIgnoreCase(kickChannel)
          && !event.getUser().getName().equals(bot.getUser().getName())) {
        new KickCommand(event, bot).execute();
      }
    }
  }

//  @EventSubscriber
//  public void onVoiceDisconnect(VoiceDisconnectedEvent event) {
//    new KillCommand(null, scheduler).execute();
//  }
}