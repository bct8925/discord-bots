package com.bri64.bots.commands.db;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DBManager;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.DiscordCommand;
import com.bri64.bots.commands.error.InvalidGuildError;
import java.sql.SQLException;

public class JoinLeaveDBCommand extends DiscordCommand {

  private DBManager database;
  private boolean join;
  private String type;

  public JoinLeaveDBCommand(final CommandEvent event, final DBManager database, boolean join) {
    super(event);
    this.database = database;
    this.join = join;
    this.type = (join) ? "join" : "leave";
  }

  @Override
  public void execute() {
    // Argument check
    String[] args = getMessage().split(" ");
    if (args.length != 2) {
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
      String userName = getUser().getName() + "#" + getUser().getDiscriminator();
      String URL = getMessage().split(" ")[1];
      if (join) {
        database.setJoin(userName, URL);
      } else {
        database.setLeave(userName, URL);
      }
      BotUtils.sendMessage(
          getUser().mention() + " " + type + " set to " + URL, getUser().getOrCreatePMChannel());
    } catch (SQLException e) {
      database.reconnect();
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: " + type + " url",
        getUser().getOrCreatePMChannel());
  }
}
