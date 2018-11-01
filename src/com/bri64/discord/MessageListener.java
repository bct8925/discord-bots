package com.bri64.discord;

import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.HelpCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public abstract class MessageListener {

  private DiscordBot bot;
  private String help;

  public MessageListener(final DiscordBot bot, final String help) {
    this.bot = bot;
    this.help = help;
  }

  @EventSubscriber
  public void onMention(MentionEvent event) {
    IGuild guild = (event.getChannel().isPrivate()) ? null : event.getGuild();
    IChannel channel = event.getChannel();
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    new HelpCommand(new CommandEvent(guild, null, channel, user, message), help, false).execute();
  }

  @EventSubscriber
  public void onMessage(MessageReceivedEvent event) {
    IGuild guild = (event.getChannel().isPrivate()) ? null : event.getGuild();
    IChannel channel = event.getChannel();
    IUser user = event.getMessage().getAuthor();
    String message = event.getMessage().getContent();
    onCommand(new CommandEvent(guild, null, channel, user, message));
  }

  /*@EventSubscriber
  public void onShardDisconnect(DisconnectedEvent event) {
    if (!event.getReason().equals(Reason.LOGGED_OUT)) {
      BotUtils.log(bot, "Connection Error: " + event.getReason().name());
      BotUtils.log(bot, "Rebooting system...");
      bot.reboot();
      BotUtils.log(bot, "Rebooted!");
    }
  }*/

  @EventSubscriber
  public abstract void onCommand(CommandEvent event);
}
