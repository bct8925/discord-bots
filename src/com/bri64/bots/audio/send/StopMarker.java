package com.bri64.bots.audio.send;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;

@SuppressWarnings("WeakerAccess")
public class StopMarker implements TrackMarkerHandler {

  private AudioPlayer player;

  public StopMarker(final AudioPlayer player) {
    this.player = player;
  }

  @Override
  public void handle(MarkerState state) {
    player.stopTrack();
  }
}
