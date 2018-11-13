package com.bri64.discord.commands.db;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DBManager;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.DiscordCommand;
import com.bri64.discord.commands.error.InvalidGuildError;
import java.sql.SQLException;

public class JoinLeaveDBCommand extends DiscordCommand {

  private MusicScheduler scheduler;
  private DBManager database;
  private boolean join;
  private String type;

  public JoinLeaveDBCommand(final CommandEvent event, final MusicScheduler scheduler,
      final DBManager database, boolean join) {
    super(event);
    this.scheduler = scheduler;
    this.database = database;
    this.join = join;
    this.type = (join) ? "join" : "leave";
  }

  @Override
  public void execute() {
    // Manual override
    if (shouldForce()) {
      valid();
      return;
    }

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
      String url = getMessage().split(" ")[1];
      boolean disabled = false;
      if (url.equalsIgnoreCase("null")) {
        disabled = true;
      } else if (!scheduler.validateURL(url)) {
        BotUtils.sendMessage(getUser().mention() + " " + url + " is invalid!",
            getOutChannel());
        return;
      }

      if (join) {
        database.setJoin(userName, url);
      } else {
        database.setLeave(userName, url);
      }

      if (!disabled) {
        BotUtils.sendMessage(
            getUser().mention() + " " + type + " set to " + url,
            getOutChannel());
      } else {
        BotUtils.sendMessage(
            getUser().mention() + " " + "disabling " + type + " sound!",
            getOutChannel());
      }
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
