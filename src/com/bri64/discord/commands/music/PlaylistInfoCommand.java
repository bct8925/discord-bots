package com.bri64.discord.commands.music;

import com.bri64.discord.BotUtils;
import com.bri64.discord.audio.send.MusicScheduler;
import com.bri64.discord.commands.CommandEvent;

public class PlaylistInfoCommand extends MusicCommand {

  public PlaylistInfoCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event, scheduler, false);
  }

  @Override
  public void execute() {
    BotUtils.sendMessage(getUser().mention() + " " + scheduler.getPlaylistInfo(),
        getUser().getOrCreatePMChannel());
  }
}
