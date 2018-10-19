package com.bri64.bots.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;

@SuppressWarnings({"WeakerAccess", "BooleanMethodIsAlwaysInverted"})
class Track {

  private MusicScheduler musicScheduler;
  private AudioTrack track;
  private String url;

  Track(MusicScheduler musicScheduler, AudioTrack track, String url) {
    this.musicScheduler = musicScheduler;
    this.track = track;
    this.url = url;
  }

  public AudioTrack cloneTrack() {
    AudioTrack clone = track.makeClone();
    if (clone.isSeekable()) {
      long startTime = 0;
      long endTime = 0;

      String stime = url.replaceAll("^.*t=([0-9.]+).*$", "$1");
      try {
        startTime = (long) (Double.parseDouble(stime) * 1000L);
      } catch (NumberFormatException ignored) {
      }

      String etime = url.replaceAll("^.*e=([0-9.]+).*$", "$1");
      try {
        endTime = (long) (Double.parseDouble(etime) * 1000L);
      } catch (NumberFormatException ignored) {
      }

      clone.setPosition(startTime);
      if (endTime != 0) {
        clone.setMarker(new TrackMarker(endTime, new StopMarker(musicScheduler)));
      }
    }
    return clone;
  }

  public void play(AudioPlayer player) {
    player.playTrack(cloneTrack());
  }

  public boolean titleMatches(String query) {
    return track.getInfo().title.toLowerCase().matches(".*" + query.toLowerCase() + ".*");
  }

  String info() {
    return (track == null) ? "null"
        : "\"" + track.getInfo().title + "\", by " + track.getInfo().author + "\n" + url;
  }

  @Override
  public String toString() {
    return ("\"" + track.getInfo().title + "\"");
  }
}
