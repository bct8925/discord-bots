package com.bri64.bots.commands.db;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DBManager;
import com.bri64.bots.commands.MessageCommand;
import java.sql.SQLException;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class AddDBCommand extends MessageCommand {

  private DBManager database;

  public AddDBCommand(final MessageEvent event, final DBManager database) {
    super(event);
    this.database = database;
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    String[] args = message.split(" ");

    // Argument check
    if (args.length != 3) {
      BotUtils.sendMessage(user.mention() + " " + "Invalid arguments! Usage: add command url",
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
      String username = user.getName() + "#" + user.getDiscriminator();
      if (database.isAdmin(username)) {
        String newcomm = message.split(" ")[1];
        String newurl = message.split(" ")[2];
        database.addCommand(newcomm, newurl);
        BotUtils.sendMessage("Added \"" + newcomm + "\"!", user.getOrCreatePMChannel());
      }
    } catch (SQLException e) {
      database.reconnect();
    }
  }
}
