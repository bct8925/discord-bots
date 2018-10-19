package com.bri64.bots.commands.db;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DBManager;
import com.bri64.bots.commands.MessageCommand;
import java.sql.SQLException;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class JoinLeaveDBCommand extends MessageCommand {

  private DBManager database;
  private boolean join;

  public JoinLeaveDBCommand(final MessageEvent event, final DBManager database, boolean join) {
    super(event);
    this.database = database;
    this.join = join;
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    String[] args = message.split(" ");
    String type = (join) ? "join" : "leave";

    // Argument check
    if (args.length != 2) {
      BotUtils.sendMessage(user.mention() + " " + "Invalid arguments! Usage: " + type + " url",
          user.getOrCreatePMChannel());
      return;
    }

    // Valid user check
    IGuild guild = !event.getChannel().isPrivate() ? event.getChannel().getGuild() : null;
    if (BotUtils.getConnectedChannel(guild, user) == null) {
      BotUtils.sendMessage(user.mention() + " " +
              "Error, can only be run from guild while user is in a voice channel.",
          user.getOrCreatePMChannel());
      return;
    }

    try {
      String userName = user.getName() + "#" + user.getDiscriminator();
      String URL = args[1];
      if (join) {
        database.setJoin(userName, URL);
      } else {
        database.setLeave(userName, URL);
      }
      BotUtils
          .sendMessage(user.mention() + " " + type + " set to " + URL, user.getOrCreatePMChannel());
    } catch (SQLException e) {
      database.reconnect();
    }
  }
}
