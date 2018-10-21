package com.bri64.bots.commands.db;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DBManager;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.DiscordCommand;
import com.bri64.bots.commands.error.InvalidGuildError;
import java.sql.SQLException;

public class AddDBCommand extends DiscordCommand {

  private DBManager database;

  public AddDBCommand(final CommandEvent event, final DBManager database) {
    super(event);
    this.database = database;
  }

  @Override
  public void execute() {
    // Argument check
    String[] args = getMessage().split(" ");
    if (args.length != 3) {
      invalidArgs();
      return;
    }

    // Valid guild check
    if (getGuild() == null) {
      new InvalidGuildError(event).execute();
      return;
    }

    valid();
  }

  @Override
  public void valid() {
    try {
      String username = getUser().getName() + "#" + getUser().getDiscriminator();
      if (database.isAdmin(username)) {
        String newcomm = getMessage().split(" ")[1];
        String newurl = getMessage().split(" ")[2];
        database.addCommand(newcomm, newurl);
        BotUtils.sendMessage(getUser().mention() + " \"" + newcomm + "\" added successfully!",
            getUser().getOrCreatePMChannel());
      }
    } catch (SQLException e) {
      database.reconnect();
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: add command url",
        getUser().getOrCreatePMChannel());
  }
}
