package com.bri64.bots;

import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.HelpCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public abstract class MessageListener {

  private String help;

  public MessageListener(final String help) {
    this.help = help;
  }

  @EventSubscriber
  public void onMention(MentionEvent event) {
    IGuild guild = (event.getChannel().isPrivate()) ? null : event.getGuild();
    IChannel channel = event.getChannel();
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    new HelpCommand(new CommandEvent(guild, channel, user, message), help).execute();
  }

  @EventSubscriber
  public void onMessage(MessageReceivedEvent event) {
    IGuild guild = (event.getChannel().isPrivate()) ? null : event.getGuild();
    IChannel channel = event.getChannel();
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    onCommandEvent(new CommandEvent(guild, channel, user, message));
  }

  @EventSubscriber
  public abstract void onCommandEvent(CommandEvent event);
}
