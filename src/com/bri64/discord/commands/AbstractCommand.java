package com.bri64.discord.commands;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractCommand implements Command {

  protected CommandEvent event;

  protected AbstractCommand(final CommandEvent event) {
    this.event = event;
  }

  public IGuild getGuild() {
    return event.getGuild();
  }

  public IVoiceChannel getVoiceChannel() {
    return event.getVoiceChannel();
  }

  public IChannel getInChannel() {
    return event.getInChannel();
  }

  public IChannel getOutChannel() {
    return event.getOutChannel();
  }

  public IUser getUser() {
    return event.getUser();
  }

  public String getMessage() {
    return event.getMessage();
  }

  public boolean shouldForce() {
    return event.shouldForce();
  }
}
