package com.bri64.discord.commands.music;

import com.bri64.discord.audio.send.Track;
import sx.blah.discord.handle.impl.events.guild.GuildEvent;
import sx.blah.discord.handle.obj.IGuild;

public class PlaylistChangedEvent extends GuildEvent {

  private Track track;
  private boolean paused;
  private boolean resumed;

  public PlaylistChangedEvent(IGuild guild, Track track, boolean paused, boolean resumed) {
    super(guild);
    this.track = track;
    this.paused = paused;
    this.resumed = resumed;
  }

  public Track getTrack() {
    return track;
  }

  public boolean wasPaused() {
    return paused;
  }

  public boolean wasResumed() {
    return resumed;
  }
}
