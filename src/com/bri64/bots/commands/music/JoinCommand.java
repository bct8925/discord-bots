package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.DiscordBot;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;
import com.bri64.bots.commands.error.InvalidGuildError;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class JoinCommand extends MusicCommand {

  private DiscordBot bot;

  public JoinCommand(final CommandEvent event, final DiscordBot bot,
      final MusicScheduler scheduler) {
    super(event, scheduler);
    this.bot = bot;
  }

  @Override
  public void execute() {
    // Valid guild check
    if (getGuild() == null) {
      new InvalidGuildError(event).execute();
      return;
    }

    valid();
  }

  @Override
  public void valid() {
    IVoiceChannel channel = BotUtils.getConnectedChannel(getGuild(), getUser());
    if (channel != null) {
      scheduler.sendSilence();
      bot.joinChannel(channel);
    } else {
      BotUtils.sendMessage(getUser().mention() + " " + "You must be in a voice channel!",
          getUser().getOrCreatePMChannel());
    }
  }

  @Override
  public void invalidArgs() {
    BotUtils.sendMessage(getUser().mention() + " " + "Invalid arguments! Usage: play url",
        getUser().getOrCreatePMChannel());
  }
}
