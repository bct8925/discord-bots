package com.bri64.bots.chillbot.commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;

@SuppressWarnings("WeakerAccess")
public abstract class MessageCommand implements Command {

  protected MessageEvent event;

  protected MessageCommand(final MessageEvent event) {
    this.event = event;
  }
}
