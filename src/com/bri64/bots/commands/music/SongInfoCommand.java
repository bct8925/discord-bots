package com.bri64.bots.commands.music;

import com.bri64.bots.BotUtils;
import com.bri64.bots.audio.send.MusicScheduler;
import com.bri64.bots.commands.CommandEvent;

public class SongInfoCommand extends MusicCommand {

  public SongInfoCommand(final CommandEvent event, final MusicScheduler scheduler) {
    super(event, scheduler);
  }

  @Override
  public void execute() {
    BotUtils.sendMessage(getUser().mention() + " " + scheduler.getTrackInfo(),
        getUser().getOrCreatePMChannel());
  }
}
