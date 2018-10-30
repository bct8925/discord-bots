package com.bri64.bots.commands.db;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DBManager;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.error.InvalidCommandError;
import com.bri64.bots.commands.error.InvalidGuildError;
import com.bri64.bots.commands.error.NotConnectedError;
import com.bri64.bots.commands.music.MusicCommand;
import java.sql.SQLException;

public class PlayDBCommand extends MusicCommand {

  private DBManager database;

  public PlayDBCommand(final CommandEvent event, final MusicScheduler scheduler,
      final DBManager database) {
    super(event, scheduler);
    this.database = database;
  }

  @Override
  public void execute() {
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
