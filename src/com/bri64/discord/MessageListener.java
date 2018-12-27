package com.bri64.discord;

import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.HelpCommand;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent.Reason;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

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
    IUser user = event.getMessage().getAuthor();
    IVoiceChannel voiceChannel = BotUtils.getConnectedChannel(guild, user);
    String message = event.getMessage().getContent();
    new HelpCommand(new CommandEvent(guild, user, voiceChannel,
        event.getChannel(), user.getOrCreatePMChannel(),
        message, false), help).execute();
  }

  @EventSubscriber
  public void onMessage(MessageReceivedEvent event) {
    IGuild guild = (event.getChannel().isPrivate()) ? null : event.getGuild();
    IUser user = event.getMessage().getAuthor();
    IVoiceChannel voiceChannel = BotUtils.getConnectedChannel(guild, user);
    String message = event.getMessage().getContent();
    onCommand(new CommandEvent(guild,
        user, voiceChannel, event.getChannel(), user.getOrCreatePMChannel(),
        message, false));
  }

  @EventSubscriber
  public void onShardDisconnect(DisconnectedEvent event) {
    if (!event.getReason().equals(Reason.LOGGED_OUT)) {
      BotUtils.log(bot, "Connection Error: " + event.getReason().name());
      BotUtils.log(bot, "Rebooting system...");
      bot.reboot();
      BotUtils.log(bot, "Rebooted!");
    }
  }

  @EventSubscriber
  public abstract void onCommand(CommandEvent event);
}
