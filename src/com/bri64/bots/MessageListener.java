package com.bri64.bots;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class MessageListener {

  protected String help;

  @EventSubscriber
  public abstract void onMention(MentionEvent event);

  @EventSubscriber
  public abstract void onMessage(MessageReceivedEvent event);
}
