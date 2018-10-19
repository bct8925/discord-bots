package com.bri64.bots.audio;

import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;


public class StopMarker implements TrackMarkerHandler {

  private MusicScheduler musicScheduler;

  StopMarker(MusicScheduler musicScheduler) {
    this.musicScheduler = musicScheduler;
  }

  @Override
  public void handle(MarkerState state) {
    musicScheduler.stopTrack();
  }
}
