package com.bri64.bots.chillbot;

import com.bri64.bots.DBManager;
import com.bri64.bots.MessageListener;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.db.AddDBCommand;
import com.bri64.bots.commands.db.PlayDBCommand;
import com.bri64.bots.commands.music.JoinCommand;
import com.bri64.bots.commands.music.KillCommand;
import com.bri64.bots.commands.music.LoopCommand;
import com.bri64.bots.commands.music.NextCommand;
import com.bri64.bots.commands.music.PauseCommand;
import com.bri64.bots.commands.music.PlayCommand;
import com.bri64.bots.commands.music.PlaylistInfoCommand;
import com.bri64.bots.commands.music.PreviousCommand;
import com.bri64.bots.commands.music.QueueCommand;
import com.bri64.bots.commands.music.RemoveTrackCommand;
import com.bri64.bots.commands.music.SeekCommand;
import com.bri64.bots.commands.music.ShuffleCommand;
import com.bri64.bots.commands.music.SongInfoCommand;
import com.bri64.bots.commands.music.VolumeCommand;
import sx.blah.discord.api.events.EventSubscriber;

@SuppressWarnings("WeakerAccess")
public class ChillListener extends MessageListener {

  private static String CHILLBOT_HELP = "```" +
      "ChillBot:\n" +
      "\t@ChillBot = Help Command\n" +
      "\t'join' = Join the channel you are in\n" +
      "\t'play url' = Play the audio at the specified url now\n" +
      "\t'queue url' = Queue the audio at the specified url\n" +
      "\t'pause' = Pause/unpause the current track\n" +
      "\t'song' = Show info for currently playing track\n" +
      "\t'playlist' = Show info for current playlist\n" +
      "\t'next|skip' = Skip to next track in queue\n" +
      "\t'prev' = Skip to previous track in queue\n" +
      "\t'seek query' = Skip to next track whose title contains query\n" +
      "\t'loop mode' = Toggle loop mode [none, one, all]\n" +
      "\t'shuffle [on|off]' = Toggle queue shuffling or shuffle\n" +
      "\t'remove' = Remove current track from queue\n" +
      "\t'volume percent' = Change volume percent (1-50)\n" +
      "\t'add command url' = [Admin] Add a new command to the database\n" +
      "\t'kill' = Clear queue and kick ChillBot from channel" +
      "```";
  private ChillBot bot;
  private MusicScheduler scheduler;
  private DBManager database;

  public ChillListener(final ChillBot bot, final MusicScheduler scheduler,
      final DBManager database) {
    super(CHILLBOT_HELP);
    this.bot = bot;
    this.scheduler = scheduler;
    this.database = database;
  }

  @Override
  @EventSubscriber
  public void onCommandEvent(CommandEvent event) {
    // Setup variables
    String message = event.getMessage();
    String command = message.split(" ")[0];

    // Join
    if (command.equalsIgnoreCase(bot.getSymbol() + "join")) {
      new JoinCommand(event, bot, scheduler).execute();
    }

    // Play
    else if (command.equalsIgnoreCase(bot.getSymbol() + "play")) {
      new PlayCommand(event, scheduler).execute();
    }

    // Queue
    else if (command.equalsIgnoreCase(bot.getSymbol() + "queue")) {
      new QueueCommand(event, scheduler).execute();
    }

    // Pause
    else if (command.equalsIgnoreCase(bot.getSymbol() + "pause")) {
      new PauseCommand(event, scheduler).execute();
    }

    // Song Info
    else if (command.equalsIgnoreCase(bot.getSymbol() + "song")) {
      new SongInfoCommand(event, scheduler).execute();
    }

    // Playlist Info
    else if (command.equalsIgnoreCase(bot.getSymbol() + "playlist")) {
      new PlaylistInfoCommand(event, scheduler).execute();
    }

    // Next
    else if (command.equalsIgnoreCase(bot.getSymbol() + "next")
        || command.equalsIgnoreCase(bot.getSymbol() + "skip")) {
      new NextCommand(event, scheduler).execute();
    }

    // Previous
    else if (command.equalsIgnoreCase(bot.getSymbol() + "prev")) {
      new PreviousCommand(event, scheduler).execute();
    }

    // Seek
    else if (command.equalsIgnoreCase(bot.getSymbol() + "seek")) {
      new SeekCommand(event, scheduler).execute();
    }

    // Loop
    else if (command.equalsIgnoreCase(bot.getSymbol() + "loop")) {
      new LoopCommand(event, scheduler).execute();
    }

    // Shuffle
    else if (command.equalsIgnoreCase(bot.getSymbol() + "shuffle")) {
      new ShuffleCommand(event, scheduler).execute();
    }

    // Remove
    else if (command.equalsIgnoreCase(bot.getSymbol() + "remove")) {
      new RemoveTrackCommand(event, scheduler).execute();
    }

    // Volume
    else if (command.equalsIgnoreCase(bot.getSymbol() + "volume")) {
      new VolumeCommand(event, scheduler).execute();
    }

    // Add
    else if (command.equalsIgnoreCase(bot.getSymbol() + "add")) {
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