package com.bri64.discord.commands.db;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DBManager;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.error.InvalidCommandError;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;
import com.bri64.discord.commands.music.MusicCommand;
import java.sql.SQLException;

public class PlayDBCommand extends MusicCommand {

  private DBManager database;

  public PlayDBCommand(final CommandEvent event, final MusicScheduler scheduler,
      final DBManager database, boolean force) {
    super(event, scheduler, force);
    this.database = database;
  }

  @Override
  public void execute() {
    // Manual override
    if (force) {
      valid();
      return;
    }

    // Argument check
    String[] args = getMessage().split(" ");
    if (args.length != 1) {
      new InvalidCommandError(event).execute();
      return;
    }

    // Valid guild check
    if (getGuild() == null) {
      new InvalidGuildError(event).execute();
      return;
    }

    // User connected check
    if (BotUtils.getConnectedChannel(getGuild(), getUser()) == null) {
      new NotConnectedError(event).execute();
      return;
    }

    valid();
  }

  @Override
  public void valid() {
    try {
      String command = getMessage().substring(1);
      String url = null;
      String[] data = database.getCommandData(command);
      if (data != null) {
        url = data[0];
      }

      // Command exists
      if (url != null) {
        scheduler.loadTracks(getUser(), url, false);
      }

      // Command not found
      else {
        new InvalidCommandError(event).execute();
      }
    } catch (SQLException e) {
      database.reconnect();
    }
  }
}
