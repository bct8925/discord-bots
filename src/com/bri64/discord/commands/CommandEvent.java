package com.bri64.discord.commands;

import sx.blah.discord.handle.impl.events.guild.GuildEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

@SuppressWarnings("WeakerAccess")
public class CommandEvent extends GuildEvent {

  private IVoiceChannel voiceChannel;
  private IChannel inChannel;
  private IChannel outChannel;
  private IUser user;
  private String message;
  private boolean force;

  public CommandEvent(final IGuild guild, final IUser user, final IVoiceChannel voiceChannel,
      final IChannel inChannel, final IChannel outChannel,
      final String message, final boolean force) {
    super(guild);
    this.voiceChannel = voiceChannel;
    this.inChannel = inChannel;
    this.outChannel = outChannel;
    this.user = user;
    this.message = message;
    this.force = force;
  }

  public IVoiceChannel getVoiceChannel() {
    return voiceChannel;
  }

  public IChannel getInChannel() {
    return inChannel;
  }

  public IChannel getOutChannel() {
    return outChannel;
  }

  public IUser getUser() {
    return user;
  }

  public String getMessage() {
    return message;
  }

  public boolean shouldForce() {
    return force;
  }

}
