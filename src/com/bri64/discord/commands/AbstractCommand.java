package com.bri64.discord.commands;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractCommand implements Command {

  protected CommandEvent event;

  protected AbstractCommand(final CommandEvent event) {
    this.event = event;
  }

  public IGuild getGuild() {
    return event.getGuild();
  }

  public IChannel getChannel() {
    return event.getChannel();
  }

  public IUser getUser() {
    return event.getUser();
  }

  public String getMessage() {
    return event.getMessage();
  }
}
