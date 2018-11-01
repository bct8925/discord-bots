package com.bri64.discord.commands.music;

import com.bri64.discord.BotUtils;
import com.bri64.discord.DiscordBot;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;
import com.bri64.discord.commands.error.InvalidGuildError;
import com.bri64.discord.commands.error.NotConnectedError;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class JoinCommand extends MusicCommand {

  private DiscordBot bot;

  public JoinCommand(final CommandEvent event, final DiscordBot bot,
      final MusicScheduler scheduler, boolean force) {
    super(event, scheduler, force);
    this.bot = bot;
  }

  @Override
  public void execute() {
    // Manual override
    if (force) {
      valid();
      return;
    }

    // Valid guild check
    if (getGuild() == null) {
      new InvalidGuildError(event).execute();
      return;
    }

    // User connected check
    if (BotUtils.getConnectedChannel(getGuild(), getUser()) == null) {
      new NotConnectedError(event).execute();
      return;
    }

    valid();
  }

  @Override
  public void valid() {
    IVoiceChannel channel = BotUtils.getConnectedChannel(getGuild(), getUser());
    scheduler.sendSilence();
    bot.joinChannel(channel);
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: play url",
        getUser().getOrCreatePMChannel());
  }
}
