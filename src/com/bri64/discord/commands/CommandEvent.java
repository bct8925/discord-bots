package com.bri64.discord.commands;

import sx.blah.discord.handle.impl.events.guild.GuildEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class CommandEvent extends GuildEvent {

  private IVoiceChannel voiceChannel;
  private IChannel channel;
  private IUser user;
  private String message;

  public CommandEvent(final IGuild guild, final IVoiceChannel voiceChannel, final IChannel channel,
      final IUser user,
      final String message) {
    super(guild);
    this.voiceChannel = voiceChannel;
    this.channel = channel;
    this.user = user;
    this.message = message;
  }

  public IVoiceChannel getVoiceChannel() {
    return voiceChannel;
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
