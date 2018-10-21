package com.bri64.bots.commands;

import sx.blah.discord.handle.impl.events.guild.GuildEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class CommandEvent extends GuildEvent {

  private IChannel channel;
  private IUser user;
  private String message;

  public CommandEvent(final IGuild guild, final IChannel channel, final IUser user,
      final String message) {
    super(guild);
    this.channel = channel;
    this.user = user;
    this.message = message;
  }

  public IChannel getChannel() {
    return channel;
  }

  public IUser getUser() {
    return user;
  }

  public String getMessage() {
    return message;
  }
}
