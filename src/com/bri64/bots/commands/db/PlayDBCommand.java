package com.bri64.bots.commands.db;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DBManager;
import com.bri64.bots.audio.MusicScheduler;
import com.bri64.bots.commands.InvalidCommand;
import com.bri64.bots.commands.music.MusicCommand;
import java.sql.SQLException;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class PlayDBCommand extends MusicCommand {

  private DBManager database;

  public PlayDBCommand(final MessageEvent event, final MusicScheduler scheduler,
      final DBManager database) {
    super(event, scheduler);
    this.database = database;
  }

  @Override
  public void execute() {
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    String[] args = message.split(" ");

    // Argument check
    if (args.length != 1) {
      new InvalidCommand(event).execute();
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
      String command = message.substring(1);
      String url = null;
      String[] data = database.getCommandData(command);
      if (data != null) {
        url = data[0];
      }

      // Command exists
      if (url != null) {
        if (BotUtils.getConnectedChannel(event.getGuild(), user) != null) {
          scheduler.loadTracks(user, url, false);
        }
      }

      // Command not found
      else {
        BotUtils.sendMessage("\"" + message + "\" is not a valid command!",
            user.getOrCreatePMChannel());
      }
    } catch (SQLException e) {
      database.reconnect();
    }
  }
}
