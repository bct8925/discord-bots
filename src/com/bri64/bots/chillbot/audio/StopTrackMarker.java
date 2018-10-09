package com.bri64.bots.chillbot.audio;

import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;


public class StopTrackMarker implements TrackMarkerHandler {

  private MusicScheduler musicScheduler;

  StopTrackMarker(MusicScheduler musicScheduler) {
    this.musicScheduler = musicScheduler;
  }

  @Override
  public void handle(MarkerState state) {
    musicScheduler.stopTrack();
  }
}
